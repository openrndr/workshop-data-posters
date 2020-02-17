package archives

import com.google.gson.Gson
import org.openrndr.resourceUrl
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDateTime
import java.util.*
import javax.net.ssl.HttpsURLConnection

val baseAddress = "https://newsapi.org/v2"

data class Result(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)

data class Article(
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String?
)

data class Source(
    val id: String,
    val name: String
)

private var storedApiKey: String? = null

fun googleNewsSetApiKey(apiKey: String) {
    storedApiKey = apiKey
}

/**
 * Get the Google News API key from either a VM arg -DgoogleNews.apiKey=<your_key> or
 * a googlenews.properties file with a googleNews.apiKey=<your_key> entry
 */
fun googleNewsApiKey(): String {
    val apiKey = storedApiKey ?: System.getProperty("googleNews.apiKey") ?: run {
        try {
            URL(resourceUrl("/googlenews.properties")).openStream().use {
                val p = Properties()
                p.load(it)
                p.getProperty("googleNews.apiKey") ?: error("googlenews.properties does not contain googleNews.apiKey")
            }
        } catch (e: RuntimeException) {
            error("No Google News API key found. Set one through -DgoogleNews.apiKey or googlenews.properties")
        }
    }
    storedApiKey = apiKey
    return apiKey
}

enum class GoogleNewsEndPoint(val endpointAddress: String) {
    TopHeadlines("top-headlines"),
    Everything("everything")
}

/**
 * The order to sort the articles in. Possible options: relevancy, popularity, publishedAt.
relevancy = articles more closely related to q come first.
popularity = articles from popular sources and publishers come first.
publishedAt = newest articles come first.
Default: publishedAt
 */
enum class GoogleNewsSortyBy(val value: String) {
    Relevancy("relevancy"),
    Popularity("popularity"),
    PublishedAt("publishedAt")
}

private val cache = mutableMapOf<String, String>()

/**
 * Request news articles from Google News
 * @param endPoint
 * @param query Keywords or phrases to search for in the article title and body.
 * @param titleQuery Query words in title only (only supported on [Everyting] end-point)
 * @param country ([TopHeadLines] only) The 2-letter ISO 3166-1 code of the country you want to get headlines for: ae ar at au be bg br ca ch cn co cu cz de eg fr gb gr hk hu id ie il in it jp kr lt lv ma mx my ng nl no nz ph pl pt ro rs ru sa se sg si sk th tr tw ua us ve za
 * @param language ([Everyting] only) The 2-letter ISO 3166-1 code of the language you want to get headlines for: ae ar at au be bg br ca ch cn co cu cz de eg fr gb gr hk hu id ie il in it jp kr lt lv ma mx my ng nl no nz ph pl pt ro rs ru sa se sg si sk th tr tw ua us ve za
 * @param category The category you want to get headlines for: business entertainment general health science sports technology
 * @param sources
 * @param domains A comma-seperated string of domains (eg bbc.co.uk, techcrunch.com, engadget.com) to restrict the search to. (only supported on [Everyting] end-point)
 * @param excludeDomains A comma-seperated string of domains (eg bbc.co.uk, techcrunch.com, engadget.com) to remove from the results. ((only supported on [Everyting] end-point)
 * @param sortBy ([Everything] only) in what order the results should be returned
 * @param fromTime ([Everything] only) latest article data
 * @param toTime ([Everything] only) most recent article data
 */
fun googleNewsRequest(
    endPoint: GoogleNewsEndPoint,
    query: String? = null,
    titleQuery: String? = null,
    country: String? = null,
    language: String? = null,
    category: String? = null,
    sources: String? = null,
    domains: String? = null,
    excludeDomains: String? = null,
    sortBy: GoogleNewsSortyBy? = null,
    fromTime: LocalDateTime?,
    toTime: LocalDateTime?,
    pageSize: Int = 20,
    page: Int = 1,
    connectTimeOut: Int = 5000
): Result {

    if (endPoint == GoogleNewsEndPoint.TopHeadlines) {
        require(!(country != null && sources != null)) { "can't query TopHeadLines for both country and sources" }
        require(!(category != null && sources != null)) { "can't query TopHeadLines for both category and sources" }
        require(domains == null) { "can't query TopHeadLines for domains" }
        require(excludeDomains == null) { "can't query TopHeadLines for excluded domains" }
        require(language == null) { "can't query TopHeadlines for language (use country instead)" }
        require(sortBy == null) { "can't query TopHeadLines for sortBy" }
        require(fromTime == null) { "can't query TopHeadLines for fromTime " }
        require(toTime == null) { "can't query TopHeadLines for toTime " }
    }

    if (endPoint == GoogleNewsEndPoint.Everything) {
        require(country == null) { "can't query Everything for country (use language instead)" }
    }

    fun String.encode(): String = URLEncoder.encode(this, "utf-8")

    val parameters =
        listOfNotNull(
            country?.let { "country=${it.encode()}" },
            language?.let { "language=${it.encode()}" },
            category?.let { "category=${it.encode()}" },
            query?.let { "q=${it.encode()}" },
            titleQuery?.let { "qInTitle=${it.encode()}" },
            domains?.let { "domains=${domains}" },
            excludeDomains?.let { "excludeDomains=${domains}" },
            sources?.let { "sources=${it.encode()}" },
            sortBy?.let { "sortBy=${it.value}" },
            fromTime?.let { "from=${it.toString().encode()}" },
            toTime?.let { "to=${it.toString().encode()}" },
            "pageSize=$pageSize",
            "page=$page"
        ).joinToString("&")

    require(parameters.isNotEmpty()) { "specify at least a query, a country or a source" }


    val requestUrl = "$baseAddress/${endPoint.endpointAddress}/?$parameters"

    val json = cache.getOrPut(requestUrl) {
        val url = URL(requestUrl)
        val con = url.openConnection() as HttpsURLConnection
        con.connectTimeout = connectTimeOut
        con.setRequestProperty("X-Api-Key", googleNewsApiKey())
        val json =
            try {
                con.connect()
                val bytes = con.inputStream.readBytes()
                String(bytes)
            } finally {
                con.disconnect()
            }
        json
    }
    return Gson().fromJson(json, Result::class.java)
}

/**
 * A sequence of google news
 */
fun googleNewsSequenceInternal(
    endPoint: GoogleNewsEndPoint,
    query: String? = null,
    titleQuery: String? = null,
    country: String? = null,
    language: String? = null,
    category: String? = null,
    sources: String? = null,
    domains: String? = null,
    excludeDomains: String? = null,
    sortBy: GoogleNewsSortyBy? = null,
    fromTime: LocalDateTime? = null,
    toTime: LocalDateTime? = null,
    pageSize: Int = 20,
    connectTimeOut: Int = 5000
) = sequence {
    var page = 1

    var count = 0
    while (true) {
        val result = googleNewsRequest(
            endPoint,
            query,
            titleQuery,
            country,
            language,
            category,
            sources,
            domains,
            excludeDomains,
            sortBy,
            fromTime,
            toTime,
            pageSize,
            page,
            connectTimeOut
        )

        for (article in result.articles) {
            count++
            yield(article)
        }

        if (count >= result.totalResults) {
            break
        } else {
            page++
        }
    }
}

fun googleNewsSequence(
    endPoint: GoogleNewsEndPoint,
    query: String? = null,
    titleQuery: String? = null,
    country: String? = null,
    language: String? = null,
    category: String? = null,
    sources: String? = null,
    domains: String? = null,
    excludeDomains: String? = null,
    sortBy: GoogleNewsSortyBy? = null,
    fromTime: LocalDateTime? = null,
    toTime: LocalDateTime? = null,
    pageSize: Int = 20,
    connectTimeOut: Int = 5000
) = sequence {

    while (true) {
        for (a in googleNewsSequenceInternal(
            endPoint,
            query,
            titleQuery,
            country,
            language,
            category,
            sources,
            domains,
            excludeDomains,
            sortBy,
            fromTime,
            toTime,
            pageSize,
            connectTimeOut
        )) {
            val fa = FileArticle(a.title, listOf(a.description), listOfNotNull(a.urlToImage))
            fa.file()
            yield(fa)
        }
    }
}

fun main() {
    val seq = googleNewsSequence(
        GoogleNewsEndPoint.Everything,
        query = "coronavirus"
    )
    for (i in seq) {
        println(i.title)
        Thread.sleep(1000)
    }
}