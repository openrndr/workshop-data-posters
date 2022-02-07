package examples

import org.openrndr.application
import org.openrndr.draw.loadImage
import org.openrndr.extras.imageFit.FitMethod
import org.openrndr.extras.imageFit.imageFit

/**
 * This demonstrates imageFit, a much easier way for placing images on the screen.
 * In this example we make an image composition but manually placing images in the window
 */

fun main() = application {
    configure {
        width = 768
        height = 578
    }
    program {
         val image = loadImage("data/images/pm5544.png")

        extend {
            val shiftX = (mouse.position.x / width) * 2.0 - 1.0
            val shiftY = (mouse.position.y / height) * 2.0 - 1.0

            drawer.imageFit(image, 0.0, 0.0, 100.0, 100.0, shiftX, shiftY)
            drawer.imageFit(image, 100.0, 0.0, 100.0, 100.0, shiftX, shiftY)
            drawer.imageFit(image, 200.0, 0.0, 100.0, 100.0, shiftX, shiftY)
            drawer.imageFit(image, 300.0, 0.0, 100.0, 100.0, shiftX, shiftY)

            drawer.imageFit(image, 00.0, 100.0, 400.0, 400.0, shiftX, shiftY)

            drawer.imageFit(image, 400.0, 0.0, 368.0, 500.0, shiftX, shiftY)

            drawer.imageFit(image, 0.0, 500.0, 768.0, 78.0, shiftX, shiftY)
        }
    }
}