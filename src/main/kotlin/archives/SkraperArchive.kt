package archives

import ru.sokomishalov.skraper.model.Image
import ru.sokomishalov.skraper.model.Post

fun skraperSequence(posts: List<Post>) = sequence {
    while (true) {
        for (post in posts) {
            val fa = FileArticle(
                post.text.orEmpty().split("\n").first(),
                listOf(post.text.orEmpty().split("\n").drop(1).joinToString("\n")),
                post.media
                    .filter { it is Image }
                    .map { it.url })
            fa.file()
            yield(fa)
        }
    }
}