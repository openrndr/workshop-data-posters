package examples.framework

import FilmGrain
import archives.LoadedArticle
import archives.localArchive
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.color.rgb
import org.openrndr.draw.loadFont
import org.openrndr.draw.tint
import org.openrndr.events.Event
import org.openrndr.extra.color.spaces.toOKHSVa
import org.openrndr.extra.compositor.*
import org.openrndr.extra.fx.blend.Multiply
import org.openrndr.extra.fx.blur.ApproximateGaussianBlur
import org.openrndr.extra.fx.color.Duotone
import org.openrndr.extra.fx.color.LumaOpacity
import org.openrndr.extra.fx.distort.StackRepeat
import org.openrndr.extra.fx.patterns.Checkers
import org.openrndr.extra.fx.shadow.DropShadow
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.noise.uniform
import org.openrndr.extra.parameters.ActionParameter
import org.openrndr.extra.parameters.Description
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.shape.Rectangle
import org.openrndr.writer
import tools.statistics

// Image treatment demonstration. Learn from it, don't just copy ;)

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
            layer {

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
        }
        onNewArticle.trigger(article)

        gui.add(settings)
        extend(gui)
        extend {
            composite.draw(drawer)
        }
    }
}