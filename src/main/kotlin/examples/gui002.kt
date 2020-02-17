package examples

import org.openrndr.animatable.Animatable
import org.openrndr.application
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.layer
import org.openrndr.extra.compositor.post
import org.openrndr.extra.fx.blur.ApproximateGaussianBlur
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.parameters.ActionParameter
import org.openrndr.extra.parameters.Description
import org.openrndr.extra.parameters.DoubleParameter


/* This demonstrates gui in combination with compose

 */

fun main() = application {
    program {

        // -- create a gui
        val gui = GUI()

        // -- create an annotated object for global settings
        val settings = @Description("Global settings") object {
            @DoubleParameter("radius", 10.0, 200.0)
            var radius = 50.0
        }

        // -- create a composite
        val composite = compose {
            // -- create a layer
            layer {
                val layerSettings = @Description("1. Layer settings") object {
                    @DoubleParameter("x coordinate", 0.0, 640.0)
                    var x = 5.0

                    @DoubleParameter("y coordinate", 0.0, 480.0)
                    var y = 5.0

                    @DoubleParameter("radius", 10.0, 200.0)
                    var radius = 50.0
                }
                gui.add(layerSettings)

                draw {
                    drawer.circle(layerSettings.x, layerSettings.y, layerSettings.radius)
                }
                post(ApproximateGaussianBlur()).addTo(gui, "1. Blur")
            }.addTo(gui, "1. Layer") // -- add the layer itself to gui, to make it switchable

            // -- create another layer
            layer {
                val layerSettings = @Description("2. Layer settings") object:Animatable() {
                    @DoubleParameter("x coordinate", 0.0, 640.0)
                    var x = 5.0

                    @DoubleParameter("y coordinate", 0.0, 480.0)
                    var y = 5.0

                    @DoubleParameter("radius", 10.0, 200.0)
                    var radius = 50.0

                    @ActionParameter("animate!")
                    fun animate() {
                        cancel()
                        animate("x", Math.random() * 640.0, 1000)
                        animate("y", Math.random() * 480.0, 1000)
                    }
                }
                gui.add(layerSettings)

                draw {
                    layerSettings.updateAnimation()
                    drawer.circle(layerSettings.x, layerSettings.y, layerSettings.radius)
                }
                post(ApproximateGaussianBlur()).addTo(gui, "2. Blur")
            }.addTo(gui, "2. Layer") // -- add the layer itself to gui, to make it switchable


        }

        gui.add(settings)

        extend(gui) {
            doubleBind = true
        }
        extend {
            composite.draw(drawer)
        }

    }
}