package org.usvm

import java.net.HttpURLConnection
import java.net.URI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun checkAccessTo(url: String): Boolean {
    HttpURLConnection.setFollowRedirects(false)
    return try {
        val conn = (URI(url).toURL().openConnection() as HttpURLConnection).apply {
            requestMethod = "HEAD"
            connectTimeout = 5000
            readTimeout = 5000
        }
        conn.connect()
        conn.responseCode == HttpURLConnection.HTTP_OK
    } catch (_: Exception) {
        false
    }
}

fun displayAccessTo(url: String, accessible: Boolean) {
    println("Сайт $url ${if (!accessible) "не" else ""}доступен")
}

suspend fun checkMultipleUrlsAndTell(urls: List<String>) {
    coroutineScope {
        urls.forEach { url ->
            launch(Dispatchers.IO) {
                displayAccessTo(url, checkAccessTo(url))
            }
        }
    }
}

fun main() {
    val urlPool = listOf(
        "https://www.google.com",
        "https://www.facebook.com",
        "https://www.instagram.com",
        "https://www.github.com",
        "https://www.twitter.com"
    )

    runBlocking {
        checkMultipleUrlsAndTell(urlPool)
    }
}