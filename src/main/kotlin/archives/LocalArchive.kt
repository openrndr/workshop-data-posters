package archives

import org.openrndr.draw.loadImage
import java.io.File

fun localArchive(base:String) : Sequence<LoadedArticle> {

    val baseDir = File(base)
    require(baseDir.isDirectory)

    var current: LoadedArticle? = null
    return sequence {
        val imageExtensions = setOf("jpg", "png", "jpeg")
        while (true) {
            val subs = baseDir.listFiles().filter { it.isDirectory && !it.isHidden }

            for (sub in subs) {
                val files = sub.listFiles().filter { it.isFile && !it.isHidden }
                val textFiles = files.filter { it.extension == "txt" }
                val texts = textFiles.map { it.readText() }
                val imageFiles = files.filter { it.extension in imageExtensions }
                val images = imageFiles.map { loadImage(it) }
                current?.destroy()
                current = LoadedArticle(sub.name, texts, images)
                yield(current!!)
            }
        }
    }

}