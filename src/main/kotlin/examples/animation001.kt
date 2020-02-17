package examples

import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.application
import org.openrndr.draw.loadFont
import org.openrndr.text.writer

/**
 * This demonstrates the most basic way of animating
 */

fun main() = application {

    configure {
        width = 768
        height = 578
    }
    program {
        val animatable = object : Animatable() {
            var x = 0.0
            var y = 0.0
        }
        // -- wait a bit
        animatable.delay(2500)
        // -- animate x and y at the same time
        animatable.animate("x", 400.0, 1000, Easing.CubicInOut)
        animatable.animate("y", 300.0, 1000, Easing.CubicInOut)
        extend {
            animatable.updateAnimation()

            drawer.circle(animatable.x, animatable.y, 100.0)
        }
    }
}