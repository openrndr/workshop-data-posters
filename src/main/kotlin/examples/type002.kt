package examples

import org.openrndr.application
import org.openrndr.draw.loadFont
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
        val largeFont = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 96.0)
        val smallFont = loadFont("data/fonts/IBMPlexMono-Medium.ttf", 24.0)

        extend {

            drawer.fontMap = largeFont
            writer {
                box = Rectangle(40.0, 40.0, 300.0, 400.0)
                newLine()
                text("WE WRITE IN THE BOX")
            }


            writer {
                box = Rectangle(380.0, 40.0, 300.0, 400.0)
                // -- the following new line is still with the large font
                // -- that way we can line up with the baseline of the large font
                newLine()

                // -- the !! here is the result of an oversight in OPENRNDR
                drawer.fontMap = smallFont
                text("We don't have to be so loud all the time.")
                newLine()
                text("O- the sweet whispers.")
            }

        }
    }
}