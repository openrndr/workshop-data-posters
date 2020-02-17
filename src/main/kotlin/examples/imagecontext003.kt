package examples

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.draw.loadImage
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.shape.Rectangle
import org.openrndr.text.Cursor
import org.openrndr.text.writer
import tools.dynamicText
import tools.statistics

fun main() = application {
    configure {
        width = 1280
        height = 800
    }
    program {
        val image = loadImage("data/images/003.jpg")
        val statistics = image.statistics()

        val font = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 200.0)
        val smallFont = loadFont("data/fonts/IBMPlexMono-Medium.ttf", 32.0)
        // -- filter nice colors, with enough saturation and brightness
        val niceColors = statistics.histogram.colors().filter {
            val hsv = it.first.toHSVa()
            hsv.s > 0.2 && hsv.v > 0.7
        }
        extend {
            drawer.imageFit(image, width/2.0 + 20.0, 20.0, width/2.0-40.0, height-40.0)
            // -- pick the most dominant and 'nice' color
            drawer.fill = niceColors.first().first ?: ColorRGBa.YELLOW
            drawer.fontMap = font

            writer {
                box = Rectangle(20.0, 20.0, width/2 - 40.0, height - 40.0)
                gaplessNewLine()
                text("TITLE" )

                drawer!!.fontMap = smallFont
                newLine()
                var index = 0
                dynamicText("So, while neither lab diamond nor mined diamond industries are perfect, the wider environmental price from the latter can be higher. Indeed, the former Tiffany chief executive Michael J. Kowalski wrote in a 2015 New York Times opinion piece, “few industries in the world have a larger environmental and social footprint than mining”.") {
                    drawer!!.fill = niceColors[index % niceColors.size].first
                    index++
                }

            }
        }
    }
}