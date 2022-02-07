package examples.framework

import archives.LoadedArticle
import archives.localArchive
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.color.rgb
import org.openrndr.draw.loadFont
import org.openrndr.events.Event
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.layer
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.post
import org.openrndr.extra.fx.shadow.DropShadow
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.parameters.ActionParameter
import org.openrndr.extra.parameters.Description
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.shape.Rectangle
import org.openrndr.writer

// This demonstrates animating a circle whenever a new article is loaded.
// Note how the animation layer is entirely self-contained

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

            // -- image layer
            layer {
                draw {
                    if (article.images.isNotEmpty()) {
                        drawer.imageFit(article.images[0], 0.0, 0.0, width * 1.0, height * 1.0)
                    }
                }
            }

            // -- animation layer
            layer {
                val animatable = object : Animatable() {
                    var x = 0.0
                    var y = 0.0
                }
                onNewArticle.listen {
                    // -- we first cancel all prior running animations
                    animatable.cancel()
                    animatable.x = 0.0
                    animatable.y = 0.0

                    // -- wait a bit
                    animatable.delay(500)
                    // -- animate x and y at the same time
                    animatable.apply {
                        animate(::x, 400.0, 1000, Easing.CubicInOut)
                        animate(::y, 300.0, 1000, Easing.CubicInOut)
                    }
                }
                draw {
                    animatable.updateAnimation()
                    drawer.circle(animatable.x, animatable.y, 100.0)
                }
            }

            // -- text layer
            layer {
                val font = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 32.0)
                draw {
                    if (article.texts.isNotEmpty()) {
                        drawer.fontMap = font
                        writer {
                            box = Rectangle(40.0, 40.0, width - 80.0, height - 80.0)
                            gaplessNewLine()
                            text(article.texts[0])
                        }
                    }
                }
                post(DropShadow()).addTo(gui, "2. Drop shadow")
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