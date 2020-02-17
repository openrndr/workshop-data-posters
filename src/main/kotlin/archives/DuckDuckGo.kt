package org.openrndr.duckko

import archives.FileArticle
import com.google.gson.Gson
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.colorBuffer
import java.io.UnsupportedEncodingException
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

//curl 'https://duckduckgo.com/i.js?l=wt-wt&o=json&q=amanda%20palmer&vqd=3-175854600097937131502949524563648687956-118825523468201311821809726630304904255&f=,,,&p=1' -H 'accept-encoding: gzip, deflate, br' -H 'accept-language: en-US,en;q=0.9,nl;q=0.8' -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36' -H 'accept: application/json, text/javascript, */*; q=0.01' -H 'referer: https://duckduckgo.com/' -H 'authority: duckduckgo.com' -H 'x-requested-with: XMLHttpRequest' --compressed
class Result(
    var image: String,
    var height: Int,
    var thumbnail: String,
    var source: String,
    var width: Int,
    var url: String,
    var title: String
) {

    lateinit var imageSmall: ColorBuffer
}

class SearchResults(
    var response_type: String,
    var results: List<Result>,
    var query: String,
    var next: String
)


private fun encodeValue(value: String): String {
    try {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
    } catch (ex: UnsupportedEncodingException) {
        throw RuntimeException(ex.cause)
    }

}

fun searchImages(query: String): SearchResults {
    val safeQuery = encodeValue(query)


    val pageUrl = "https://duckduckgo.com/?q=$safeQuery&t=h_&iax=images&ia=images"


    val page = URL(pageUrl)
    val pageCon = page.openConnection()
    pageCon.setRequestProperty("authority", "duckduckgo.com")
    pageCon.setRequestProperty("cache-control", "max-age=0")
    pageCon.setRequestProperty("upgrade-insecure-requests", "1")
    pageCon.setRequestProperty(
        "user-agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36"
    )
    pageCon.setRequestProperty(
        "accept",
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3"
    )
    pageCon.connect()
    val result = pageCon.getInputStream().reader().readText()
//    println(result)

    val r = Regex("vqd='([0-9-]*)'")
    val m = r.find(result)
    println(m?.groupValues!![1] ?: "no result")
    val vqd = m?.groupValues!![1]


    //curl 'https://duckduckgo.com/?q=prince&t=h_&iax=images&ia=images'
    // -H 'authority: duckduckgo.com'
    // -H 'cache-control: max-age=0'
    // -H 'upgrade-insecure-requests: 1'
    // -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36'
    // -H 'accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3' -H 'accept-encoding: gzip, deflate, br'
    // -H 'accept-language: en-US,en;q=0.9,nl;q=0.8' --compressed
//    println(page)

    val baseUrl = "https://duckduckgo.com/i.js?l=wt-wt&o=json&q=$safeQuery&vqd=$vqd=,,,&p=1"
    val base = URL(baseUrl)
    val baseCon = base.openConnection()
    baseCon.setRequestProperty("accept-language", "en-US,en;q=0.9,nl;q=0.8")
    baseCon.setRequestProperty(
        "user-agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36"
    )
    baseCon.setRequestProperty("referer", "https://duckduckgo.com")
    baseCon.setRequestProperty("authority", "duckduckgo.com")
    baseCon.setRequestProperty("x-requested-with", "XMLHttpRequest")
    baseCon.connect()
    val baseResult = baseCon.getInputStream().reader().readText()
//    println(baseResult)

    val realResult = Gson().fromJson(baseResult, SearchResults::class.java)
    return realResult
    //curl 'https://duckduckgo.com/i.js?l=wt-wt&o=json&q=amanda%20palmer&vqd=3-175854600097937131502949524563648687956-118825523468201311821809726630304904255&f=,,,&p=1'
    // -H 'accept-encoding: gzip, deflate, br'
    // -H 'accept-language: en-US,en;q=0.9,nl;q=0.8'
    // -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36'
    // -H 'accept: application/json, text/javascript, */*; q=0.01'
    // -H 'referer: https://duckduckgo.com/'
    // -H 'authority: duckduckgo.com'
    // -H 'x-requested-with: XMLHttpRequest' --compressed

    //val realUrl = URL(baseUrl)
}

fun scrapeImages(query: String, maxImages:Int = 1000): List<Result> {
    val r = searchImages(query)

    val toProc = r.results.take(maxImages)

    for ((i,result) in toProc.withIndex()) {
        try {
            println("downloading image [${i+1}/${toProc.size}]")
            result.imageSmall = ColorBuffer.fromUrl(result.thumbnail)
        } catch (e:Exception) {
            println("error downloading ${result.thumbnail}")
            result.imageSmall = colorBuffer(256,256)
        }
    }
    return toProc
}

fun duckDuckGoSequence(query:String) = sequence {

    while (true) {
        for (result in searchImages(query).results) {
            val fa = FileArticle(result.title, listOf(result.title), listOf(result.thumbnail))
            fa.file()
            yield(fa)
        }
    }

}
//
//fun main() {
//
//    val realResult = searchImages("amanda palmer")
//    for (r in realResult.results) {
//
//        println("${r.title} ${r.thumbnail}")
//    }
//
//}