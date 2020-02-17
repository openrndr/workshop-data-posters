package examples

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.extra.compositor.blend
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.layer
import org.openrndr.extra.fx.blend.Multiply
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
            }
        }
        extend {
            composite.draw(drawer)
        }
    }
}