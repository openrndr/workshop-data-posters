package tools

import org.openrndr.draw.FontImageMap
import org.openrndr.draw.isolated
import org.openrndr.text.WriteStyle
import org.openrndr.text.Writer

fun Writer.dynamicText(text: String, parser: (String) -> Unit) {

    val currentFont = drawerRef!!.fontMap as FontImageMap

    val tokens = text.split(" ")

    for ((index, token) in tokens.withIndex()) {

        val copyStyle = WriteStyle()
        copyStyle.ellipsis = style.ellipsis
        copyStyle.leading = style.leading
        copyStyle.tracking = style.tracking
        drawerRef!!.isolated {
            parser(token)
            this@dynamicText.text(token)
        }
        style = copyStyle
        if (index != tokens.size - 1) {
            text(" ")
        }
        drawerRef!!.fontMap = currentFont
    }
}