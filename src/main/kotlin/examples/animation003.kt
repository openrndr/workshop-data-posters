package examples

import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.application
import org.openrndr.draw.loadFont
import org.openrndr.text.writer

/**
 * This demonstrates a looping animation
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

        extend {
            animatable.updateAnimation()
            if (!animatable.hasAnimations()) {
                animatable.animate("x", 400.0, 1000, Easing.CubicInOut)
                animatable.animate("y", 300.0, 1000, Easing.CubicInOut)
                animatable.complete()
                animatable.animate("x", 0.0, 1000, Easing.CubicInOut)
                animatable.animate("y", 0.0, 1000, Easing.CubicInOut)
            }
            drawer.circle(animatable.x, animatable.y, 100.0)
        }
    }
}