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
import org.openrndr.extra.fx.distort.Lenses
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

// Type treatment demonstration. Learn from it, don't just copy ;)

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

        val composite = compose {
            layer {
                post(Checkers())
            }
            // "shadow" text layer
            layer {
                val font = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 50.0)
                draw {
                    drawer.fontMap = font
                    drawer.fill = ColorRGBa.RED
                    writer {
                        box = drawer.bounds.offsetEdges(-20.0)
                        newLine()
                        leading = -10.0
                        tracking = -5.0
                        text(article.texts[0])
                    }
                }
                post(Lenses()).addTo(gui)
                blend(Multiply())
            }.addTo(gui, "shadow text")

            // foreground text layer
            layer {
                val font = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 50.0)

                draw {
                    drawer.fontMap = font
                    drawer.fill = ColorRGBa.WHITE
                    writer {
                        box = drawer.bounds.offsetEdges(-20.0)
                        newLine()
                        leading = -10.0
                        tracking = -5.0
                        text(article.texts[0])
                    }
                }

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