package examples

import org.openrndr.application
import org.openrndr.draw.isolated
import org.openrndr.draw.loadFont
import org.openrndr.shape.Rectangle
import org.openrndr.text.writer

/**
 * This demonstrates advanced text placement and rotation.
 */

fun main() = application {

    configure {
        width = 768
        height = 578
    }
    program {
        val largeFont = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 96.0)
        val smallFont = loadFont("data/fonts/IBMPlexMono-Medium.ttf", 24.0)

        extend {
            drawer.fontMap = largeFont
            drawer.isolated {
                drawer.translate(width/2.0, height/2.0)
                drawer.rotate(seconds * 10.0)
                drawer.translate(-150.0, -200.0) // -- half of the rectangle below

                writer {
                    box = Rectangle(0.0, 0.0, 300.0, 400.0)
                    newLine()
                    text("WE WRITE IN THE BOX")
                }
            }

            drawer.fontMap = smallFont
            drawer.isolated {
                drawer.translate(smallFont.height, 10.0)
                drawer.rotate(90.0)
                writer {
                    box = Rectangle(0.0, 0.0, 300.0, 40.0)
                    text("TEXT SET LEFT-IN")
                }
            }

            drawer.isolated {
                val textHeight = 40.0
                drawer.translate(0.0, height - 10.0)
                drawer.rotate(-90.0)
                writer {
                    box = Rectangle(0.0, 0.0, 300.0, textHeight)
                    newLine()
                    text("TEXT SET LEFT-OUT")
                }
            }

            drawer.isolated {
                val textHeight = 40.0
                val textWidth = 200.0
                drawer.translate(width - smallFont.height, textWidth)
                drawer.rotate(-90.0)
                writer {
                    box = Rectangle(0.0, .0, textWidth, textHeight)
                    //newLine()
                    text("TEXT SET RIGHT-IN")
                }
            }

            drawer.isolated {
                val textWidth = 210.0
                drawer.translate(width*1.0, height - textWidth)
                drawer.rotate(90.0)
                writer {
                    box = Rectangle(0.0, 0.0, textWidth, 40.0)
                    newLine()
                    text("TEXT SET RIGHT-OUT")
                }
            }
        }
    }
}