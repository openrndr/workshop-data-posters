package examples

import org.openrndr.application
import org.openrndr.draw.loadImage

/**
 * This demonstrates the most basic way of drawing images
 */

fun main() = application {

    configure {
        width = 768
        height = 578
    }
    program {
         val image = loadImage("data/images/pm5544.png")

        extend {
            drawer.image(image)
        }
    }
}