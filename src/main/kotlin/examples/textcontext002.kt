package examples

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.draw.loadImage
import org.openrndr.shape.Rectangle
import org.openrndr.text.writer
import tools.dynamicText

/**
 * This demonstrates the most basic way of writing text
 */

fun main() = application {

    configure {
        width = 768
        height = 578
    }
    program {
        val font = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 24.0)
        val highlight = loadFont("data/fonts/IBMPlexMono-BoldItalic.ttf", 24.0)

        val specialWords = setOf("word", "we", "list", "words")

        extend {

            drawer.fontMap = font
            writer {
                box = Rectangle(40.0, 100.0, 300.0, 400.0)
                newLine()
                dynamicText("we can highlight any word that we want, we do this by placing them in a" +
                        " special list of words") {
                    if (it in specialWords) {
                        drawer.fontMap = highlight
                        drawer.fill = ColorRGBa.PINK
                    }
                }
            }
        }
    }
}