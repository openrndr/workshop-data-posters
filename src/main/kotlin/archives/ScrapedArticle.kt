package archives

import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.loadImage
import org.openrndr.draw.persistent
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.security.MessageDigest

class FileArticle(val title: String, val texts: List<String>, val imageUrls: List<String>) {
    fun file() {
        for (iu in imageUrls) {
            val imageCache = File("cache/images")
            if (!imageCache.exists()) {
                imageCache.mkdirs()
            }
            downloadFile(iu, File(imageCache, hashImageUrl(iu)))
        }
    }

    fun load(): LoadedArticle {
        val imageCache = File("cache/images")
        val images = imageUrls.mapNotNull {
            val hash = File(imageCache, hashImageUrl(it))
            if (hash.exists()) {
                persistent { loadImage(hash) }
            } else null
        }
        return LoadedArticle(title, texts, images)
    }
}

fun downloadFile(urlString: String, target: File) {
    if (!target.exists()) {
        try {
            val data = URL(urlString).readBytes()
            target.writeBytes(data)
        } catch(e: FileNotFoundException) {
            println("file not found: ${urlString}")
        }
    }
}

class LoadedArticle(val title: String, val texts: List<String>, val images: List<ColorBuffer>) {
    fun destroy() {
        images.forEach { it.destroy() }
    }
}

fun hashImageUrl(url: String): String {
    val extension = when {
        url.endsWith(".png") -> ".png"
        url.endsWith(".jpg") -> ".jpg"
        else -> ".jpg" // -- yeah about that ..
    }
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(url.toByteArray())
    return digest.fold("", { str, it -> str + "%02x".format(it) }) + extension
}