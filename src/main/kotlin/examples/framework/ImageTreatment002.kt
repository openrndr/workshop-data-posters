package examples.framework

import FilmGrain
import archives.LoadedArticle
import archives.localArchive
import org.openrndr.application
import org.openrndr.events.Event
import org.openrndr.extra.compositor.*
import org.openrndr.extra.fx.blend.Add
import org.openrndr.extra.fx.blur.HashBlur
import org.openrndr.extra.fx.color.Duotone
import org.openrndr.extra.fx.color.LumaOpacity
import org.openrndr.extra.fx.distort.Perturb
import org.openrndr.extra.fx.distort.StackRepeat
import org.openrndr.extra.fx.shadow.DropShadow
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.noise.uniform
import org.openrndr.extra.parameters.ActionParameter
import org.openrndr.extra.parameters.Description
import org.openrndr.extras.imageFit.imageFit

// Image treatment demonstration. Learn from it, don't just copy ;)
// Here we learn that we are not limited to using a single layer

fun main() = application {
    configure {
        width = 600
        height = 800
    }

    program {
        val archive = localArchive("archives/example-poetry").iterator()
        var article = archive.next()
        val gui = GUI()

        val onNewArticle = Event<LoadedArticle>()
        val settings = @Description("Settings") object {
            @ActionParameter("Next article")
            fun nextArticle() {
                article = archive.next()
                onNewArticle.trigger(article)
            }
        }

        // all our image treatment happens inside a compose block
        val composite = compose {

            // our first image layer, we can name them to make it a bit easier to find it back
            var imageLayer0 = layer {
                // here we create variables that we will use to randomize the settings of StackRepeat
                var xo = 0.0
                var yo = 0.0

                // listen for a new article event and randomize
                onNewArticle.listen {
                    xo = Double.uniform(-0.25, 0.25)
                    yo = Double.uniform(-0.25, 0.25)
                }

                draw {
                    // draw the article image full page but have 10 px margins
                    drawer.imageFit(article.images[0], drawer.bounds.offsetEdges(-10.0))
                }
                // add a drop shadow effect first
                post(DropShadow())
                post(StackRepeat()) {
                    // we can learn which settings StackRepeat has by moving the cursor over StackRepeat and
                    // pressing command-b on macOS or ctrl-b on Windows/Linux
                    this.xOffset = xo
                    this.yOffset = yo
                }.addTo(gui)

                // Add a duotone effect, use the default colors
                post(Duotone()) {
                    // this.backgroundColor = ColorRGBa.BLACK
                    // this.foregroundColor = ColorRGBa.WHITE
                }
                post(FilmGrain())
            }

            var imageLayer1 = layer {

                var perturbPhase = 0.0
                onNewArticle.listen {
                    perturbPhase = Double.uniform(-1.0, 1.0)
                }

                draw {
                    drawer.imageFit(article.images[0], drawer.bounds.offsetEdges(-100.0))
                }
                // LumaOpacity maps color intensity to transparency
                post(LumaOpacity()) {
                    this.foregroundLuma = 0.5
                    this.backgroundLuma = 0.3
                }
                // Make everything gooey looking
                post(Perturb()) {
                    this.phase = perturbPhase
                }

                // Apply a bit of diffusion blur
                post(HashBlur()) {
                    this.radius = 2.0
                }
                blend(Add())

            }

        }
        onNewArticle.trigger(article)

        gui.add(settings)
        extend(gui)
        extend {
            gui.visible = mouse.position.x < 200.0

            composite.draw(drawer)
        }
    }
}