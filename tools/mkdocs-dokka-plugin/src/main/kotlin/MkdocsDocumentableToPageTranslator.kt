@file:OptIn(InternalDokkaApi::class)

package io.islandtime.gradle

import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.InternalDokkaApi
import org.jetbrains.dokka.analysis.kotlin.internal.DocumentableSourceLanguageParser
import org.jetbrains.dokka.analysis.kotlin.internal.InternalKotlinAnalysisPlugin
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.base.signatures.SignatureProvider
import org.jetbrains.dokka.base.transformers.pages.comments.CommentsToContentConverter
import org.jetbrains.dokka.base.transformers.pages.tags.CustomTagContentProvider
import org.jetbrains.dokka.base.translators.documentables.DefaultPageCreator
import org.jetbrains.dokka.model.DModule
import org.jetbrains.dokka.model.Documentable
import org.jetbrains.dokka.model.SourceSetDependent
import org.jetbrains.dokka.model.doc.Description
import org.jetbrains.dokka.model.doc.TagWrapper
import org.jetbrains.dokka.pages.ContentKind
import org.jetbrains.dokka.pages.ModulePageNode
import org.jetbrains.dokka.pages.TextStyle
import org.jetbrains.dokka.plugability.*
import org.jetbrains.dokka.transformers.documentation.DocumentableToPageTranslator
import org.jetbrains.dokka.utilities.DokkaLogger
import kotlin.reflect.KClass

private typealias GroupedTags = Map<KClass<out TagWrapper>, List<Pair<DokkaConfiguration.DokkaSourceSet?, TagWrapper>>>


class MkdocsDocumentableToPageTranslator(
    context: DokkaContext
) : DocumentableToPageTranslator {
    private val configuration = configuration<DokkaBase, DokkaBaseConfiguration>(context)
    private val commentsToContentConverter = context.plugin<DokkaBase>().querySingle { commentsToContentConverter }
    private val signatureProvider = context.plugin<DokkaBase>().querySingle { signatureProvider }
    private val customTagContentProviders = context.plugin<DokkaBase>().query { customTagContentProvider }
    private val documentableSourceLanguageParser =
        context.plugin<InternalKotlinAnalysisPlugin>().querySingle { documentableSourceLanguageParser }
    private val logger = context.logger

    override fun invoke(module: DModule): ModulePageNode =
        MkdocsPageCreator(
            configuration,
            commentsToContentConverter,
            signatureProvider,
            logger,
            customTagContentProviders,
            documentableSourceLanguageParser
        ).pageForModule(module)
}

class MkdocsPageCreator(
    configuration: DokkaBaseConfiguration?,
    commentsToContentConverter: CommentsToContentConverter,
    signatureProvider: SignatureProvider,
    logger: DokkaLogger,
    customTagContentProviders: List<CustomTagContentProvider> = emptyList(),
    documentableAnalyzer: DocumentableSourceLanguageParser
) : DefaultPageCreator(
    configuration,
    commentsToContentConverter,
    signatureProvider,
    logger,
    customTagContentProviders,
    documentableAnalyzer
) {

    override fun contentForModule(m: DModule) = contentBuilder.contentFor(m) {
        group(kind = ContentKind.Cover) {
            cover(m.name)
            if (contentForDescription(m).isNotEmpty()) {
                sourceSetDependentHint(
                    m.dri,
                    m.sourceSets.toSet(),
                    kind = ContentKind.SourceSetDependentHint,
                    styles = setOf(TextStyle.UnderCoverText)
                ) {
                    +contentForDescription(m)
                }
            }
        }

        block(
            "Packages",
            2,
            ContentKind.Packages,
            m.packages,
            m.sourceSets.toSet(),
            needsAnchors = true,
            headers = listOf(
                contentBuilder.contentFor(mainDRI, mainSourcesetData) {
                    text("Name")
                    text("Summary")
                }
            )
        ) {
            val documentations = it.sourceSets.map { platform ->
                it.descriptions[platform]?.also { it.root }
            }
            val haveSameContent =
                documentations.all { it?.root == documentations.firstOrNull()?.root && it?.root != null }

            link(it.name, it.dri)
            if (it.sourceSets.size == 1 || (documentations.isNotEmpty() && haveSameContent)) {
                documentations.first()?.let { firstSentenceComment(kind = ContentKind.Comment, content = it.root) }
            }
        }
    }

    private val Documentable.groupedTags: GroupedTags
        get() = documentation.flatMap { (pd, doc) ->
            doc.children.asSequence().map { pd to it }.toList()
        }.groupBy { it.second::class }

    private val Documentable.descriptions: SourceSetDependent<Description>
        get() = groupedTags.withTypeUnnamed()

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T : TagWrapper> GroupedTags.withTypeUnnamed(): SourceSetDependent<T> =
        (this[T::class] as List<Pair<DokkaConfiguration.DokkaSourceSet, T>>?)?.toMap().orEmpty()
}
