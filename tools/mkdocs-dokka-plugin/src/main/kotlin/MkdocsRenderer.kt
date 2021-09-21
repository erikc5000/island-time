package io.islandtime.gradle

import org.jetbrains.dokka.DokkaException
import org.jetbrains.dokka.Platform
import org.jetbrains.dokka.base.renderers.DefaultRenderer
import org.jetbrains.dokka.base.renderers.isImage
import org.jetbrains.dokka.model.DisplaySourceSet
import org.jetbrains.dokka.pages.*
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.plugability.plugin
import org.jetbrains.dokka.plugability.query
import org.jetbrains.dokka.gfm.GfmCommand.Companion.templateCommand
import org.jetbrains.dokka.gfm.ResolveLinkGfmCommand
import java.util.*

class MkdocsRenderer(
    context: DokkaContext
) : DefaultRenderer<MarkdownContent>(context) {

    override val preprocessors = context.plugin<MkdocsPlugin>().query { mkdocsPreprocessors }

    private val isPartial = context.configuration.delayTemplateSubstitution

    override fun MarkdownContent.wrapGroup(
        node: ContentGroup,
        pageContext: ContentPage,
        childrenCallback: MarkdownContent.() -> Unit
    ) {
        return when {
            node.dci.kind == ContentKind.Symbol && node.hasStyle(TextStyle.Monospace) -> {
                inlineCodeBlock { childrenCallback() }
                buildParagraph()
            }
            node.hasStyle(TextStyle.Block) -> {
                childrenCallback()
                buildParagraph()
            }
            node.hasStyle(TextStyle.Paragraph) -> {
                childrenCallback()
                buildParagraph()
            }
            else -> childrenCallback()
        }
    }

    override fun MarkdownContent.buildHeader(level: Int, node: ContentHeader, content: MarkdownContent.() -> Unit) {
        buildParagraph()
        append("#".repeat(level) + " ")
        content()
        appendLineBreak()
    }

    override fun MarkdownContent.buildLink(address: String, content: MarkdownContent.() -> Unit) {
        appendNonCode("[")
        content()
        appendNonCode("]($address)")
    }

    override fun MarkdownContent.buildList(
        node: ContentList,
        pageContext: ContentPage,
        sourceSetRestriction: Set<DisplaySourceSet>?
    ) {
        buildListLevel(node, pageContext)
    }

    private fun MarkdownContent.buildListItem(items: List<ContentNode>, pageContext: ContentPage) {
        items.forEach {
            if (it is ContentList) {
                buildList(it, pageContext)
            } else {
                append("<li>")
                append(buildMarkdownContent { it.build(this, pageContext, it.sourceSets) }.trim())
                append("</li>")
            }
        }
    }

    private fun MarkdownContent.buildListLevel(node: ContentList, pageContext: ContentPage) {
        if (node.ordered) {
            append("<ol>")
            buildListItem(node.children, pageContext)
            append("</ol>")
        } else {
            append("<ul>")
            buildListItem(node.children, pageContext)
            append("</ul>")
        }
    }

    override fun MarkdownContent.buildDRILink(
        node: ContentDRILink,
        pageContext: ContentPage,
        sourceSetRestriction: Set<DisplaySourceSet>?
    ) {
        locationProvider.resolve(node.address, node.sourceSets, pageContext)?.let {
            buildLink(it) {
                buildText(node.children, pageContext, sourceSetRestriction)
            }
        } ?: if (isPartial) {
            templateCommand(ResolveLinkGfmCommand(node.address)) {
                buildText(node.children, pageContext, sourceSetRestriction)
            }
        } else buildText(node.children, pageContext, sourceSetRestriction)
    }

    override fun MarkdownContent.buildLineBreak() {
        append("\\")
        appendLineBreak()
    }

    private fun MarkdownContent.buildParagraph() {
        appendLineBreak()
        appendLineBreak()
    }

    override fun MarkdownContent.buildPlatformDependent(
        content: PlatformHintedContent,
        pageContext: ContentPage,
        sourceSetRestriction: Set<DisplaySourceSet>?
    ) {
        buildPlatformDependentItem(content.inner, content.sourceSets, pageContext)
    }

    private fun MarkdownContent.buildPlatformDependentItem(
        content: ContentNode,
        sourceSets: Set<DisplaySourceSet>,
        pageContext: ContentPage
    ) {
        if (content is ContentGroup && content.children.firstOrNull { it is ContentTable } != null) {
            buildContentNode(content, pageContext, sourceSets)
        } else {
            val distinct = sourceSets.map {
                it to buildMarkdownContent { buildContentNode(content, pageContext, setOf(it)) }
            }.groupBy(Pair<DisplaySourceSet, String>::second, Pair<DisplaySourceSet, String>::first)

            distinct.filter { it.key.isNotBlank() }.forEach { (text, platforms) ->
                buildSourceSetTags(platforms.toSet())
                append(text)
                appendLineBreak()
            }
        }
    }

    override fun MarkdownContent.buildResource(node: ContentEmbeddedResource, pageContext: ContentPage) {
        if (node.isImage()) {
            append("!")
        }
        append("[${node.altText}](${node.address})")
    }

    override fun MarkdownContent.buildTable(
        node: ContentTable,
        pageContext: ContentPage,
        sourceSetRestriction: Set<DisplaySourceSet>?
    ) {
        appendLineBreak()
        if (node.dci.kind == ContentKind.Sample || node.dci.kind == ContentKind.Parameters) {
            node.sourceSets.forEach { sourcesetData ->
                append(sourcesetData.name)
                appendLineBreak()
                buildTable(
                    node.copy(
                        children = node.children.filter { it.sourceSets.contains(sourcesetData) },
                        dci = node.dci.copy(kind = ContentKind.Main)
                    ), pageContext, sourceSetRestriction
                )
                appendLineBreak()
            }
        } else {
            val size = node.header.firstOrNull()?.children?.size ?: node.children.firstOrNull()?.children?.size ?: 0
            if (size <= 0) return

            if (node.header.isNotEmpty()) {
                node.header.forEach {
                    it.children.forEach {
                        append("| ")
                        it.build(this, pageContext, it.sourceSets)
                        append(" ")
                    }
                }
            } else {
                append("| ".repeat(size))
            }
            append("|")
            appendLineBreak()

            append("|---".repeat(size))
            append("|")
            appendLineBreak()

            node.children.forEach { row ->
                row.children.forEach { cell ->
                    append("| ")
                    append(buildMarkdownContent { cell.build(this, pageContext) }
                        .trim()
                        .replace("#+ ".toRegex(), "") // Workaround for headers inside tables
                        .withEntersAsHtml()
//                        .replace("\\\n", "\n\n")
//                        .replace("\n[\n]+".toRegex(), "<br>")
//                        .replace("\n", " ")
                    )
                    append(" ")
                }
                append("|")
                appendLineBreak()
            }
        }
    }

    override fun MarkdownContent.buildText(textNode: ContentText) {
        if (textNode.text.isNotBlank()) {
            val decorators = decorators(textNode.style, inInlineCodeBlock)
            append(textNode.text.takeWhile { it == ' ' })
            append(decorators.first)
            append(textNode.text.trim().run {
                if (inInlineCodeBlock) {
                    replace("<", "\\<")
                    replace(">", "\\>")
                } else {
                    this
                }
            })
            append(decorators.second)
            append(textNode.text.takeLastWhile { it == ' ' })
        }
    }

    override fun MarkdownContent.buildNavigation(page: PageNode) {
        locationProvider.ancestors(page).asReversed().forEach { node ->
            append("/")
            if (node.isNavigable) buildLink(node, page)
            else append(node.name)
        }
        buildParagraph()
    }

    override fun buildPage(page: ContentPage, content: (MarkdownContent, ContentPage) -> Unit): String =
        buildMarkdownContent {
            append("---\n")
            append("title: ${page.name}\n")
            append("---\n")
            content(this, page)
        }

    override fun buildError(node: ContentNode) {
        context.logger.warn("Markdown renderer has encountered problem. The unmatched node is $node")
    }

    override fun MarkdownContent.buildDivergent(node: ContentDivergentGroup, pageContext: ContentPage) {

        val distinct =
            node.groupDivergentInstances(pageContext, { instance, _, sourceSet ->
                instance.before?.let { before ->
                    buildMarkdownContent { buildContentNode(before, pageContext, sourceSet) }
                } ?: ""
            }, { instance, _, sourceSet ->
                instance.after?.let { after ->
                    buildMarkdownContent { buildContentNode(after, pageContext, sourceSet) }
                } ?: ""
            })

        distinct.values.forEach { entry ->
            val (instance, sourceSets) = entry.getInstanceAndSourceSets()

            buildSourceSetTags(sourceSets)
            instance.before?.let {
//                buildLineBreak()
//                append("Brief description")
                appendLineBreak()
                buildContentNode(
                    it,
                    pageContext,
                    sourceSets.first()
                ) // It's workaround to render content only once
            }

//            buildLineBreak()
//            append("Content")
            entry.groupBy {
                buildMarkdownContent {
                    buildContentNode(
                        it.first.divergent,
                        pageContext,
                        setOf(it.second)
                    )
                }
            }
                .values.forEach { innerEntry ->
                    val (innerInstance, innerSourceSets) = innerEntry.getInstanceAndSourceSets()

                    appendLineBreak()
                    if (sourceSets.size > 1) {
                        buildSourceSetTags(innerSourceSets)
//                        buildLineBreak()
                    }
                    innerInstance.divergent.build(
                        this@buildDivergent,
                        pageContext,
                        setOf(innerSourceSets.first())
                    ) // It's workaround to render content only once
                }

            instance.after?.let {
                appendLineBreak()
//                append("More info")
//                buildLineBreak()
                buildContentNode(
                    it,
                    pageContext,
                    sourceSets.first()
                ) // It's workaround to render content only once
            }

            buildParagraph()
        }
    }

    override fun MarkdownContent.buildCodeBlock(code: ContentCodeBlock, pageContext: ContentPage) {
        append("```kotlin")
        buildLineBreak()
        code.children.forEach { it.build(this, pageContext) }
        append("```")
    }

    override fun MarkdownContent.buildCodeInline(code: ContentCodeInline, pageContext: ContentPage) {
        append("`")
        code.children.forEach {
            it.build(this, pageContext)
        }
        append("`")
    }

    private fun decorators(styles: Set<Style>, inInlineCodeBlock: Boolean): Pair<String, String> {
        val decorators = buildString {
            styles.forEach {
                when (it) {
                    TextStyle.Bold -> if (inInlineCodeBlock) append("<b>") else append("**")
                    TextStyle.Italic -> if (inInlineCodeBlock) append("<i>") else append("*")
                    TextStyle.Strong -> if (inInlineCodeBlock) append("<strong>") else append("**")
                    TextStyle.Strikethrough -> if (inInlineCodeBlock) append("<strike>") else append("~~")
                    else -> Unit
                }
            }
        }

        return if (inInlineCodeBlock) {
            decorators to decorators.replace("<", "</")
        } else {
            decorators to decorators.reversed()
        }
    }

    private val PageNode.isNavigable: Boolean
        get() = this !is RendererSpecificPage || strategy != RenderingStrategy.DoNothing

    private fun MarkdownContent.buildLink(to: PageNode, from: PageNode) =
        buildLink(locationProvider.resolve(to, from)!!) {
            append(to.name)
        }

    override suspend fun renderPage(page: PageNode) {
        val path by lazy {
            locationProvider.resolve(page, skipExtension = true)
                ?: throw DokkaException("Cannot resolve path for ${page.name}")
        }
        return when (page) {
            is ContentPage -> outputWriter.write(path, buildPage(page) { c, p -> buildPageContent(c, p) }, ".md")
            is RendererSpecificPage -> when (val strategy = page.strategy) {
                is RenderingStrategy.Copy -> outputWriter.writeResources(strategy.from, path)
                is RenderingStrategy.Write -> outputWriter.write(path, strategy.text, "")
                is RenderingStrategy.Callback -> outputWriter.write(path, strategy.instructions(this, page), ".md")
                is RenderingStrategy.DriLocationResolvableWrite -> outputWriter.write(
                    path,
                    strategy.contentToResolve { dri, sourcesets ->
                        locationProvider.resolve(dri, sourcesets)
                    },
                    ""
                )
                is RenderingStrategy.PageLocationResolvableWrite -> outputWriter.write(
                    path,
                    strategy.contentToResolve { pageToLocate, context ->
                        locationProvider.resolve(pageToLocate, context)
                    },
                    ""
                )
                RenderingStrategy.DoNothing -> Unit
            }
            else -> throw AssertionError(
                "Page ${page.name} cannot be rendered by renderer as it is not renderer specific nor contains content"
            )
        }
    }

    private fun String.withEntersAsHtml(): String = replace("\n", "<br>") //this
//        .replace("\\\n", "\n\n")
//        .replace("\n[\n]+".toRegex(), "<br>")
//        .replace("\n", " ")

    private fun List<Pair<ContentDivergentInstance, DisplaySourceSet>>.getInstanceAndSourceSets() =
        this.let { Pair(it.first().first, it.map { it.second }.toSet()) }

    private fun MarkdownContent.buildSourceSetTags(sourceSets: Set<DisplaySourceSet>) {
        val tags = sourceSets.joinToString(separator = " ") {
            val cssStyle = when (it.platform) {
                Platform.common -> "source-set-common"
                Platform.native -> "source-set-darwin"
                Platform.jvm -> if (it.name.lowercase().contains("android")) {
                    "source-set-android"
                } else {
                    "source-set-jvm"
                }
                else -> ""
            }

            "<span class=\"$cssStyle source-set-tag\">${it.name}</span>"
        }

        append("<p style=\"text-align: right\">$tags</p>")
    }
}
