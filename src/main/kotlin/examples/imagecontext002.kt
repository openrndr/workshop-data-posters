package examples

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImage
import tools.statistics

fun main() = application {
    configure {
        width = 1280
        height = 800
    }
    program {
        val image = loadImage("data/images/004.jpg")
        val statistics = image.statistics()
        val niceColors = statistics.histogram.colors().filter {
            val hsv = it.first.toHSVa()
            hsv.s > 0.2 && hsv.v > 0.7
        }
        extend {
            drawer.image(image)

            // draw dot on bright center of gravity
            // draw dot on dark center of gravity

            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.PINK
            drawer.circle(statistics.brightCog, 20.0)

            drawer.fill = ColorRGBa.BLACK
            drawer.circle(statistics.darkCog, 20.0)

        }
    }
}