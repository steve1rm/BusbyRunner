package me.androidbox.core.data.networking

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.cio.Request
import io.ktor.util.InternalAPI
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import me.androidbox.core.data.BuildConfig
import me.androidbox.core.domain.util.DataError
import me.androidbox.core.domain.util.Result

suspend inline fun <reified T : Any> HttpClient.get(route: String, queryParameters: Map<String, Any?>): Result<T, DataError.Network> {
    return safeCall {
        get {
            this.url(constructRoute(route))

            queryParameters.forEach { (key, value) ->
                this.parameter(key, value)
            }
        }
    }
}

suspend inline fun <reified Request, reified T : Any> HttpClient.post(route: String, body: Request): Result<T, DataError.Network> {
    return safeCall {
        post {
            this.url(constructRoute(route))
            this.setBody(body)
        }
    }
}

suspend inline fun <reified T : Any> HttpClient.delete(route: String, queryParameters: Map<String, Any?>): Result<T, DataError.Network> {
    return safeCall {
        delete {
            this.url(constructRoute(route))

            queryParameters.forEach { (key, value) ->
                this.parameter(key, value)
            }
        }
    }
}

suspend inline fun <reified T> safeCall(execute: () -> HttpResponse): Result<T, DataError.Network> {
    val response = try {
        execute()
    }
    catch (exception: UnresolvedAddressException) {
        exception.printStackTrace()

        return Result.Failure(DataError.Network.NO_INTERNET)
    }
    catch (exception: SerializationException) {
        exception.printStackTrace()

        return Result.Failure(DataError.Network.SERIALIZATION)
    }
    catch(exception: Exception) {
        exception.printStackTrace()

        if(exception is CancellationException) {
            throw exception
        }

        return Result.Failure(DataError.Network.UNKNOWN)
    }

    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, DataError.Network> {
    return when(response.status.value) {
        in 200..299 -> {
            Result.Success(response.body<T>())
        }
        401 -> {
            Result.Failure(DataError.Network.UNAUTHORIZED)
        }
        408 -> {
            Result.Failure(DataError.Network.REQUEST_TIMEOUT)
        }
        409 -> {
            Result.Failure(DataError.Network.CONFLICT)
        }
        413 -> {
            Result.Failure(DataError.Network.PAYLOAD_TOO_LARGE)
        }
        429 -> {
            Result.Failure(DataError.Network.TOO_MANY_REQUESTS)
        }
        in 500..599 -> {
            Result.Failure(DataError.Network.SERVER_ERROR)
        }
        else -> {
            Result.Failure(DataError.Network.UNKNOWN)
        }
    }
}

fun constructRoute(route: String): String {
    return when {
        route.contains(BuildConfig.BASE_URL) -> {
            route
        }
        route.startsWith("/") -> {
            BuildConfig.BASE_URL + route
        }
        else -> {
            BuildConfig.BASE_URL + "/$route"
        }
    }
}