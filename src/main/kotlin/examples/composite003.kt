package examples

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.extra.compositor.*
import org.openrndr.extra.fx.blend.Multiply
import org.openrndr.extra.fx.blur.ApproximateGaussianBlur
import org.openrndr.text.writer

fun main() = application {
    program {
        val composite = compose {

            draw {
                drawer.background(ColorRGBa.WHITE)
            }

            layer {
                // -- single execution
                val font = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 128.0)

                draw {
                    drawer.fontMap = font
                    drawer.fill = ColorRGBa.Companion.fromHex("e04e41")
                    writer {
                        cursor.x = 40.0
                        cursor.y = height / 2.0
                        text("TEST TEXT")
                    }
                }
                post(ApproximateGaussianBlur()) {
                    window = 10
                    sigma = 6.0
                }

            }
            layer {
                // -- single execution
                val font = loadFont("data/fonts/IBMPlexMono-BoldItalic.ttf", 128.0)

                draw {
                    drawer.fontMap = font
                    drawer.fill = ColorRGBa.Companion.fromHex("84bfcd")
                    writer {
                        cursor.x = 40.0
                        cursor.y = height / 2.0
                        text("TEST TEXT")
                    }
                }
                blend(Multiply())
                post(ApproximateGaussianBlur()) {
                    window = 10
                    sigma = 6.0
                }
            }
        }
        extend {
            composite.draw(drawer)
        }
    }
}