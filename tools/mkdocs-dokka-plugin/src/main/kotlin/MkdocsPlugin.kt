package io.islandtime.gradle

import org.jetbrains.dokka.CoreExtensions
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.renderers.PackageListCreator
import org.jetbrains.dokka.base.renderers.RootCreator
import org.jetbrains.dokka.base.resolvers.shared.RecognizedLinkFormat
import org.jetbrains.dokka.gfm.GfmPlugin
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.plugability.DokkaPluginApiPreview
import org.jetbrains.dokka.plugability.PluginApiPreviewAcknowledgement
import org.jetbrains.dokka.transformers.pages.PageTransformer

class MkdocsPlugin : DokkaPlugin() {
    val mkdocsPreprocessors by extensionPoint<PageTransformer>()

    private val dokkaBase by lazy { plugin<DokkaBase>() }
    private val gfmPlugin by lazy { plugin<GfmPlugin>() }

    @Suppress("unused")
    val renderer by extending {
        CoreExtensions.renderer providing ::MkdocsRenderer override gfmPlugin.renderer
    }

    val rootCreator by extending {
        mkdocsPreprocessors with RootCreator
    }

    @Suppress("unused")
    val documentableToPageTranslator by extending {
        CoreExtensions.documentableToPageTranslator providing ::MkdocsDocumentableToPageTranslator override dokkaBase.documentableToPageTranslator
    }

    @Suppress("unused")
    val packageListCreator by extending {
        (mkdocsPreprocessors
            providing { PackageListCreator(it, RecognizedLinkFormat.DokkaGFM) }
            order { after(rootCreator) })
    }

    @OptIn(DokkaPluginApiPreview::class)
    override fun pluginApiPreviewAcknowledgement(): PluginApiPreviewAcknowledgement = PluginApiPreviewAcknowledgement
}
