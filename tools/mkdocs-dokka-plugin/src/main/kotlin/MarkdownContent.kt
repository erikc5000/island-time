package io.islandtime.gradle

class MarkdownContent : Appendable {
    private var inlineCodeBlockLevel = 0
    val inInlineCodeBlock: Boolean get() = inlineCodeBlockLevel > 0
    private var codeBlockIsTerminated = true
    private val stringBuilder = StringBuilder()

    override fun append(content: CharSequence): MarkdownContent = with(stringBuilder) {
        val adjustedContent = if (inInlineCodeBlock) {
            if (codeBlockIsTerminated) {
                append("<code>")
                codeBlockIsTerminated = false
            }

            content.toString().replace("*", "\\*")
        } else {
            content
        }

        append(adjustedContent)
        this@MarkdownContent
    }

    override fun append(csq: CharSequence?, start: Int, end: Int): MarkdownContent {
        return if (csq != null) append(csq.subSequence(start, end)) else this
    }

    override fun append(c: Char): MarkdownContent {
        return append(c.toString())
    }

    fun appendNonCode(content: String): Unit = with(stringBuilder) {
        append(content)
    }

    fun appendLineBreak(): Unit = with(stringBuilder) {
        append('\n')
    }

    fun pushInlineCodeBlock() {
        inlineCodeBlockLevel++
    }

    fun popInlineCodeBlock() {
        val previousLevel = inlineCodeBlockLevel
        inlineCodeBlockLevel = (inlineCodeBlockLevel - 1).coerceAtLeast(0)

        if (previousLevel > 0 && inlineCodeBlockLevel == 0 && !codeBlockIsTerminated) {
            stringBuilder.append("</code>")
        }

        codeBlockIsTerminated = true
    }

    fun build(): String = stringBuilder.toString().trim().replace("[\n]{3,}".toRegex(), "\n\n")
}

inline fun MarkdownContent.inlineCodeBlock(block: MarkdownContent.() -> Unit) {
    pushInlineCodeBlock()
    block()
    popInlineCodeBlock()
}

inline fun buildMarkdownContent(block: MarkdownContent.() -> Unit): String {
    return MarkdownContent().apply(block).build()
}
