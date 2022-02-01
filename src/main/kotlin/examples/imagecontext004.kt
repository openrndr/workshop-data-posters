package examples

import org.openrndr.application
import org.openrndr.draw.loadImage
import org.openrndr.extras.imageFit.imageFit
import tools.statistics

/* Visualize the average color of an image, which is often gray-ish */
fun main() = application {
    configure {
        width = 1280
        height = 800
    }
    program {
        val image = loadImage("data/images/004.jpg")
        val statistics = image.statistics()

        extend {
            drawer.imageFit(image, width/2.0 + 20.0, 20.0, width/2.0-40.0, height-40.0)
            // -- pick the most dominant and 'nice' color
            drawer.fill = statistics.average
            drawer.rectangle(20.0, 20.0, width/2.0 - 40.0, height-40.0)

        }
    }
}