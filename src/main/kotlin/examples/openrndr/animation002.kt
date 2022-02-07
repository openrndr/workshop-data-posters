package examples

import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.application

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
        animatable.apply {
            delay(2500)
            ::x.animate(400.0, 1000, Easing.CubicInOut)
            ::x.complete()
            ::y.animate(300.0, 1000, Easing.CubicInOut)
        }
        extend {
            animatable.updateAnimation()
            drawer.circle(animatable.x, animatable.y, 100.0)
        }
    }
}