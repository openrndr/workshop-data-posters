package examples

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.draw.loadImage
import org.openrndr.shape.Rectangle
import org.openrndr.text.writer

/**
 * This demonstrates the most basic way of writing text
 */

fun main() = application {

    configure {
        width = 768
        height = 578
    }
    program {
        val font = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 96.0)

        extend {

            drawer.fontMap = font
            writer {
                box = Rectangle(40.0, 40.0, 300.0, 400.0)
                newLine()
                text("WE WRITE IN THE BOX")
            }

            // here we visualize the box
            drawer.fill = null
            drawer.stroke = ColorRGBa.WHITE
            drawer.rectangle( 40.0, 40.0, 300.0, 400.0)

        }
    }
}