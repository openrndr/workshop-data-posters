package examples

import org.openrndr.application
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.parameters.DoubleParameter

fun main() = application {
    program {

        // -- create a gui
        val gui = GUI()

        // -- create an annotated object
        val settings = object {
            @DoubleParameter("x coordinate", 0.0, 640.0)
            var x = 5.0

            @DoubleParameter("y coordinate", 0.0, 480.0)
            var y = 5.0

            @DoubleParameter("radius", 10.0, 200.0)
            var radius = 50.0

        }

        gui.add(settings)
        extend(gui)
        extend {

            drawer.circle(settings.x, settings.y, settings.radius)
        }

    }
}