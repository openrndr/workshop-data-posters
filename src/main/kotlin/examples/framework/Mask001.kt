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
import org.openrndr.extra.compositor.*
import org.openrndr.extra.fx.blur.ApproximateGaussianBlur
import org.openrndr.extra.fx.patterns.Checkers
import org.openrndr.extra.fx.shadow.DropShadow
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.parameters.ActionParameter
import org.openrndr.extra.parameters.Description
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.shape.Rectangle
import org.openrndr.writer

// This demonstrates layer masking

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
                post(Checkers())
            }

            layer {
                // -- image layer
                layer {
                    draw {
                        if (article.images.isNotEmpty()) {
                            drawer.imageFit(article.images[0], 0.0, 0.0, width * 1.0, height * 1.0)
                        }
                    }
                    val font = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 128.0)
                    mask {
                        drawer.fontMap = font
                        writer {
                            box = Rectangle(40.0, 40.0, width * 2 - 80.0, height * 2 - 80.0)
                            gaplessNewLine()
                            text(article.texts[0])
                        }
                    }
                }
                post(DropShadow()).addTo(gui)
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