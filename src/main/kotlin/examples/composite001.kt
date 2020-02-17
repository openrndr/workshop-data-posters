package examples

import org.openrndr.application
import org.openrndr.draw.loadFont
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.layer
import org.openrndr.text.writer

fun main() = application {
    program {
        val composite = compose {

            layer {
                // -- single execution
                val font = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 64.0)

                draw {
                    drawer.fontMap = font
                    writer {
                        cursor.x = 200.0
                        cursor.y = height / 2.0
                        text("TEST TEXT")
                    }
                }
            }
        }
        extend {
            composite.draw(drawer)
        }
    }
}