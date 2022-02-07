package examples

import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.application

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
                animatable.apply {
                    ::x.animate(400.0, 1000, Easing.CubicInOut)
                    ::y.animate(300.0, 1000, Easing.CubicInOut)
                    ::y.complete()
                    ::x.animate(0.0, 1000, Easing.CubicInOut)
                    ::y.animate(0.0, 1000, Easing.CubicInOut)
                }
            }
            drawer.circle(animatable.x, animatable.y, 100.0)
        }
    }
}