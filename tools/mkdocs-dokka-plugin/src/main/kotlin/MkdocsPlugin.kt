package io.islandtime.gradle

import org.jetbrains.dokka.CoreExtensions
import org.jetbrains.dokka.DokkaException
import org.jetbrains.dokka.Platform
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.renderers.DefaultRenderer
import org.jetbrains.dokka.base.renderers.PackageListCreator
import org.jetbrains.dokka.base.renderers.RootCreator
import org.jetbrains.dokka.base.renderers.isImage
import org.jetbrains.dokka.base.resolvers.local.DokkaLocationProvider
import org.jetbrains.dokka.base.resolvers.local.LocationProviderFactory
import org.jetbrains.dokka.base.resolvers.shared.RecognizedLinkFormat
import org.jetbrains.dokka.model.DisplaySourceSet
import org.jetbrains.dokka.pages.*
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.plugability.plugin
import org.jetbrains.dokka.plugability.query
import org.jetbrains.dokka.transformers.pages.PageTransformer
//import org.jetbrains.dokka.gfm.GfmCommand.Companion.templateCommand
//import org.jetbrains.dokka.gfm.ResolveLinkGfmCommand
import java.util.*

class MkdocsPlugin : DokkaPlugin() {
    val mkdocsPreprocessors by extensionPoint<PageTransformer>()

    private val dokkaBase by lazy { plugin<DokkaBase>() }

    val renderer by extending {
        (CoreExtensions.renderer
            providing { MkdocsRenderer(it) }
            override dokkaBase.htmlRenderer)
    }

    val locationProvider by extending {
        (dokkaBase.locationProviderFactory
            providing { MarkdownLocationProviderFactory(it) }
            override dokkaBase.locationProvider)
    }

    val rootCreator by extending {
        mkdocsPreprocessors with RootCreator
    }

    val packageListCreator by extending {
        (mkdocsPreprocessors
            providing { PackageListCreator(it, RecognizedLinkFormat.DokkaGFM) }
            order { after(rootCreator) })
    }
}

class MarkdownContent : Appendable {
    private var inlineCodeBlockLevel = 0
    val inInlineCodeBlock: Boolean get() = inlineCodeBlockLevel > 0
    private var codeBlockIsTerminated = true
    private val stringBuilder = StringBuilder()

    override fun append(content: CharSequence): MarkdownContent = with(stringBuilder) {
        if (inInlineCodeBlock) {
            if (codeBlockIsTerminated) {
                //append('`')
                append("<code>")
                codeBlockIsTerminated = false
            }
        }

        append(content)
        this@MarkdownContent
    }

    override fun append(csq: CharSequence?, start: Int, end: Int): MarkdownContent {
        return if (csq != null) append(csq.subSequence(start, end)) else this
    }

    override fun append(c: Char): MarkdownContent {
        return append(c.toString())
    }

    fun appendNonCode(content: String): Unit = with(stringBuilder) {
//        if (inInlineCodeBlock && !codeBlockIsTerminated) {
//            //append('`')
//            codeBlockIsTerminated = true
//        }

        append(content)
    }

    fun appendNewLine(): Unit = with(stringBuilder) {
//        if (inInlineCodeBlock && !codeBlockIsTerminated) {
//            stringBuilder.append('`')
//            codeBlockIsTerminated = true
//        }

        append('\n')
    }

    fun pushInlineCodeBlock() {
        inlineCodeBlockLevel++
    }

    fun popInlineCodeBlock() {
        val previousLevel = inlineCodeBlockLevel
        inlineCodeBlockLevel = (inlineCodeBlockLevel - 1).coerceAtLeast(0)

        if (previousLevel > 0 && inlineCodeBlockLevel == 0 && !codeBlockIsTerminated) {
            //stringBuilder.append("`")
            stringBuilder.append("</code>")
        }

        codeBlockIsTerminated = true
    }

    fun build(): String = stringBuilder.toString().replace("[\n]{3,}".toRegex(), "\n\n")
}

private inline fun MarkdownContent.inlineCodeBlock(block: MarkdownContent.() -> Unit) {
    pushInlineCodeBlock()
    block()
    popInlineCodeBlock()
}

private inline fun buildMarkdownContent(block: MarkdownContent.() -> Unit): String {
    return MarkdownContent().apply(block).build()
}

class MkdocsRenderer(
    context: DokkaContext
) : DefaultRenderer<MarkdownContent>(context) {

    override val preprocessors = context.plugin<MkdocsPlugin>().query { mkdocsPreprocessors }

//    private val isPartial = context.configuration.delayTemplateSubstitution

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
                buildNewLine()
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
        buildNewLine()
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
        } ?: /*if (isPartial) {
            templateCommand(ResolveLinkGfmCommand(node.address)) {
                buildText(node.children, pageContext, sourceSetRestriction)
            }
        } else*/ buildText(node.children, pageContext, sourceSetRestriction)
    }

    override fun MarkdownContent.buildNewLine() {
        appendNewLine()
    }

    private fun MarkdownContent.buildParagraph() {
        appendNewLine()
        appendNewLine()
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
                buildNewLine()
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
        buildNewLine()
        if (node.dci.kind == ContentKind.Sample || node.dci.kind == ContentKind.Parameters) {
            node.sourceSets.forEach { sourcesetData ->
                append(sourcesetData.name)
                buildNewLine()
                buildTable(
                    node.copy(
                        children = node.children.filter { it.sourceSets.contains(sourcesetData) },
                        dci = node.dci.copy(kind = ContentKind.Main)
                    ), pageContext, sourceSetRestriction
                )
                buildNewLine()
            }
        } else {
            val size = node.header.firstOrNull()?.children?.size ?: node.children.firstOrNull()?.children?.size ?: 0

            if (node.header.isNotEmpty()) {
                node.header.forEach {
                    append("| ")
                    it.children.forEach {
                        append(" ")
                        it.build(this, pageContext, it.sourceSets)
                        append(" | ")
                    }
                    append("\n")
                }
            } else {
                append("| ".repeat(size))
                if (size > 0) append("|\n")
            }

            append("|---".repeat(size))
            if (size > 0) append("|\n")

            node.children.forEach {
                val builder = MarkdownContent()
                it.children.forEach {
                    builder.append("| ")
                    builder.append("<a name=\"${it.dci.dri.first()}\"></a>")
                    builder.append(
                        buildMarkdownContent { it.build(this, pageContext) }.replace(
                            Regex("#+ "),
                            ""
                        ).trim()
                    )  // Workaround for headers inside tables
                }
                append(builder.build().withEntersAsHtml())
                append("|".repeat(size + 1 - it.children.size))
                appendNewLine()
            }
        }
    }

    override fun MarkdownContent.buildText(textNode: ContentText) {
        if (textNode.text.isNotBlank()) {
            val decorators = decorators(textNode.style, inInlineCodeBlock)
            append(textNode.text.takeWhile { it == ' ' })
            append(decorators.first)
            append(textNode.text.trim())
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
//                append("Brief description")
//                buildNewLine()
                buildContentNode(
                    it,
                    pageContext,
                    sourceSets.first()
                ) // It's workaround to render content only once
                buildNewLine()
            }

//            append("Content")
//            buildNewLine()
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

                    if (sourceSets.size > 1) {
                        buildSourceSetTags(innerSourceSets)
                    }
                    innerInstance.divergent.build(
                        this@buildDivergent,
                        pageContext,
                        setOf(innerSourceSets.first())
                    ) // It's workaround to render content only once
                    buildNewLine()
                }

            instance.after?.let {
//                append("More info")
//                buildNewLine()
                buildContentNode(
                    it,
                    pageContext,
                    sourceSets.first()
                ) // It's workaround to render content only once
                buildNewLine()
            }

            buildParagraph()
        }
    }

    override fun MarkdownContent.buildCodeBlock(code: ContentCodeBlock, pageContext: ContentPage) {
        append("```kotlin")
        buildNewLine()
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
        when (page) {
            is ContentPage -> outputWriter.write(path, buildPage(page) { c, p -> buildPageContent(c, p) }, ".md")
            is RendererSpecificPage -> when (val strategy = page.strategy) {
                is RenderingStrategy.Copy -> outputWriter.writeResources(strategy.from, path)
                is RenderingStrategy.Write -> outputWriter.write(path, strategy.text, "")
                is RenderingStrategy.Callback -> outputWriter.write(path, strategy.instructions(this, page), ".md")
                RenderingStrategy.DoNothing -> Unit
            }
            else -> throw AssertionError(
                "Page ${page.name} cannot be rendered by renderer as it is not renderer specific nor contains content"
            )
        }
    }

    private fun String.withEntersAsHtml(): String = replace("\n", "<br>")

    private fun List<Pair<ContentDivergentInstance, DisplaySourceSet>>.getInstanceAndSourceSets() =
        this.let { Pair(it.first().first, it.map { it.second }.toSet()) }

    private fun MarkdownContent.buildSourceSetTags(sourceSets: Set<DisplaySourceSet>) {
        val tags = sourceSets.joinToString(separator = " ") {
            val cssStyle = when (it.platform) {
                Platform.common -> "source-set-common"
                Platform.native -> "source-set-darwin"
                Platform.jvm -> if (it.name.toLowerCase(Locale.ROOT).contains("android")) {
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

class MarkdownLocationProviderFactory(val context: DokkaContext) : LocationProviderFactory {

    override fun getLocationProvider(pageNode: RootPageNode) = MarkdownLocationProvider(pageNode, context)
}

class MarkdownLocationProvider(
    pageGraphRoot: RootPageNode,
    dokkaContext: DokkaContext
) : DokkaLocationProvider(pageGraphRoot, dokkaContext, ".md")
