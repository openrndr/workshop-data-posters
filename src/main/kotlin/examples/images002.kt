package examples

import org.openrndr.application
import org.openrndr.draw.loadImage

/**
 * This demonstrates an image with a position, note how it falls off the screen
 */

fun main() = application {
    configure {
        width = 768
        height = 578
    }
    program {
         val image = loadImage("data/images/pm5544.png")

        extend {
            drawer.image(image, 100.0, 100.0)
        }
    }
}