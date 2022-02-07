package examples.framework

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
import org.openrndr.extra.fx.color.LumaOpacity
import org.openrndr.extra.fx.patterns.Checkers
import org.openrndr.extra.fx.shadow.DropShadow
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.parameters.ActionParameter
import org.openrndr.extra.parameters.Description
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.shape.Rectangle
import org.openrndr.writer
import tools.statistics

// This demonstrates how to use color statistics

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
            var background = ColorRGBa.PINK
            onNewArticle.listen {
                background = rgb(Math.random(), Math.random(), Math.random())
            }

            layer {
                draw {
                    if (article.images.isNotEmpty()) {
                        val stats = article.images[0].statistics()
                        drawer.fill = stats.average
                        drawer.stroke = null
                        drawer.rectangle(0.0, 0.0, width * 1.0, height * 1.0)

                    }
                }
            }
            layer {
                draw {
                    drawer.imageFit(article.images[0], 20.0, 20.0, width - 40.0, height - 40.0)
                }
                post(LumaOpacity()).addTo(gui)
            }
            // -- text layer
            layer {
                val font = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 64.0)
                draw {
                    if (article.texts.isNotEmpty()) {
                        val stats = article.images[0].statistics()
                        drawer.fill = stats.histogram.colors()[0].first
                        drawer.fontMap = font
                        writer {
                            box = Rectangle(40.0, 40.0, width - 80.0, height - 80.0)
                            gaplessNewLine()
                            text(article.texts[0])
                        }
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