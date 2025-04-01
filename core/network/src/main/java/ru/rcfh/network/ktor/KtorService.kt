package ru.rcfh.network.ktor

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import pro.respawn.apiresult.ApiResult
import ru.rcfh.network.model.NetworkHandbookCollection

private object Api {
    private const val BASE_URL = "http://77.222.58.130:8001"
    const val HANDBOOKS = "$BASE_URL/api/all-guide/"
}

class KtorService(
    private val client: HttpClient
) {
    suspend fun getHandbookCollection() = ApiResult {
        client.get(Api.HANDBOOKS).body<NetworkHandbookCollection>()
    }
}