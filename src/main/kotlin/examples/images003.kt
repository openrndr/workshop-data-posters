package examples

import org.openrndr.application
import org.openrndr.draw.loadImage
import org.openrndr.extras.imageFit.imageFit

/**
 * This demonstrates imageFit, a much easier way for placing images on the screen.
 */

fun main() = application {
    configure {
        width = 768
        height = 578
    }
    program {
         val image = loadImage("data/images/pm5544.png")

        extend {
            drawer.imageFit(image, 20.0, 20.0, width - 40.0, height - 40.0)
        }
    }
}