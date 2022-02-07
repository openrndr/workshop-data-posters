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
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.layer
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.post
import org.openrndr.extra.fx.blur.ApproximateGaussianBlur
import org.openrndr.extra.fx.shadow.DropShadow
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.parameters.ActionParameter
import org.openrndr.extra.parameters.Description
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.shape.Rectangle
import org.openrndr.writer

// This demonstrates animating post filter properties

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
                val animatable = object : Animatable() {
                    var opacity = 0.0
                    var blur = 0.0
                }
                onNewArticle.listen {
                    animatable.cancel()
                    animatable.opacity = 0.0
                    animatable.blur = 4.0
                    animatable.apply {
                        animate(::opacity, 1.0, 1000, Easing.CubicInOut)
                        animate(::blur, 0.0, 4000, Easing.CubicInOut)
                    }
                }

                draw {
                    animatable.updateAnimation()
                    if (article.images.isNotEmpty()) {
                        drawer.drawStyle.colorMatrix = tint(ColorRGBa.WHITE.opacify(animatable.opacity))
                        drawer.imageFit(article.images[0], 0.0, 0.0, width * 1.0, height * 1.0)
                    }
                }
                post(ApproximateGaussianBlur()) {
                    this.sigma = 0.001 + animatable.blur
                    this.window = 10
                }

            }

            // -- text layer
            layer {
                val animatable = object : Animatable() {
                    var opacity = 0.0
                }
                onNewArticle.listen {
                    animatable.cancel()
                    animatable.delay(500)
                    animatable.opacity = 0.0
                    animatable.apply {
                        animate(::opacity, 1.0, 1000, Easing.CubicInOut)
                    }
                }

                val font = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 32.0)
                draw {
                    animatable.updateAnimation()
                    if (article.texts.isNotEmpty()) {
                        drawer.fontMap = font
                        drawer.fill = ColorRGBa.WHITE.opacify(animatable.opacity)
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