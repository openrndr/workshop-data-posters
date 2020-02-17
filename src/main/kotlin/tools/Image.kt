package tools

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBuffer
import org.openrndr.math.Vector2

class ImageStatistics(val histogram: ColorHistogram, val average: ColorRGBa,
                      val brightCog: Vector2,
                      val darkCog: Vector2)
fun ColorBuffer.statistics(): ImageStatistics {
    val s = shadow
    s.download()

    val histogram = calculateHistogram(this, 16)
    var sr = 0.0
    var sg = 0.0
    var sb = 0.0
    var bsx = 0.0
    var bsy = 0.0
    var dsx = 0.0
    var dsy = 0.0


    var bw = 0.0
    var dw = 0.0
    for (y in 0 until height) {
        for (x in 0 until width) {

            val c = s[x, y]
            sr += c.r
            sg += c.g
            sb += c.b
            val w = (c.r + c.g + c.b) / 3.0
            bsx += x * w
            bsy += y * w
            dsx += x * (1.0-w)
            dsy += y * (1.0-w)
            dw += (1.0-w)
            bw += w

        }
    }

    if (bw < 0.0001) { bw = 1.0 }
    val bcog = Vector2(bsx / bw, bsy / bw)
    val dcog = Vector2(dsx / dw, dsy / dw)
    val cw = width * height
    val averageColor = ColorRGBa(sr / cw, sg / cw, sb / cw)
    s.destroy()
    return ImageStatistics(histogram, averageColor, bcog, dcog)
}

fun calculateHistogram(buffer: ColorBuffer, binCount: Int): ColorHistogram {

    val bins = Array(binCount) { Array(binCount) { DoubleArray(binCount) } }

    buffer.shadow.download()

    val s = buffer.shadow
    for (y in 0 until buffer.height) {
        for (x in 0 until buffer.width) {
            val c = s[x, y]
            val rb = (c.r * binCount).toInt().coerceIn(0, binCount - 1)
            val gb = (c.g * binCount).toInt().coerceIn(0, binCount - 1)
            val bb = (c.b * binCount).toInt().coerceIn(0, binCount - 1)
            bins[rb][gb][bb] += 1.0
        }
    }

    var maxValue = 0.0
    var maxR = 0
    var maxG = 0
    var maxB = 0
    for (r in 0 until binCount) {
        for (g in 0 until binCount) {
            for (b in 0 until binCount) {
                if (bins[r][g][b] > maxValue) {
                    maxValue = bins[r][g][b]
                    maxR = r
                    maxG = g
                    maxB = b
                }
            }
        }
    }

    if (maxValue > 0)
        for (r in 0 until binCount) {
            for (g in 0 until binCount) {
                for (b in 0 until binCount) {
                    bins[r][g][b] /= maxValue
                }
            }
        }

    buffer.shadow.destroy()

    return ColorHistogram(bins, binCount)
}


class ColorHistogram(val freqs: Array<Array<DoubleArray>>, val binCount: Int) {
    fun colors(): List<Pair<ColorRGBa, Double>> {
        val result = mutableListOf<Pair<ColorRGBa, Double>>()
        for (r in 0 until binCount) {
            for (g in 0 until binCount) {
                for (b in 0 until binCount) {

                    result.add(
                        Pair(
                            ColorRGBa(r / (binCount - 1.0), g / (binCount - 1.0), b / (binCount - 1.0)),
                            freqs[r][g][b]
                        )
                    )
                }
            }
        }
        return result.sortedByDescending { it.second }
    }
}