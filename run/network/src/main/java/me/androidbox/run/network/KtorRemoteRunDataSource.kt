package me.androidbox.run.network

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.androidbox.core.data.networking.constructRoute
import me.androidbox.core.data.networking.delete
import me.androidbox.core.data.networking.get
import me.androidbox.core.data.networking.safeCall
import me.androidbox.core.domain.run.RemoteRunDataSource
import me.androidbox.core.domain.run.RunModel
import me.androidbox.core.domain.util.DataError
import me.androidbox.core.domain.util.EmptyResult
import me.androidbox.core.domain.util.Result
import me.androidbox.core.domain.util.mapper

class KtorRemoteRunDataSource(
    private val httpClient: HttpClient
) : RemoteRunDataSource {
    override suspend fun getRuns(): Result<List<RunModel>, DataError.Network> {
        return httpClient.get<List<RunDto>>(route = "/run")
            .mapper { listOfRunDto ->
                listOfRunDto.map {
                    it.toRunModel()
                }
            }
    }

    override suspend fun postRun(
        runModel: RunModel,
        mapPicture: ByteArray
    ): Result<RunModel, DataError.Network> {
        val createRunRequest = Json.encodeToString(runModel.toCreateRunRequest())

        val result = safeCall<RunDto> {
            httpClient.submitFormWithBinaryData(
                url = constructRoute(route = "/run"),
                formData = formData {
                    append("MAP_PICTURE", mapPicture, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=map-picture.jpg")
                    })
                    append("RUN_DATA", createRunRequest, Headers.build {
                        append(HttpHeaders.ContentType, "text/plain")
                        append(HttpHeaders.ContentDisposition, "form-data; name=\"RUN_DATA\"")
                    })
                }
            ) {
                method = HttpMethod.Post
            }
        }

        return result.mapper {
            it.toRunModel()
        }
    }

    override suspend fun deleteRun(id: String): EmptyResult<DataError.Network> {
        return httpClient.delete(
            route = "/run",
            queryParameters = mapOf(
                "id" to id
            )
        )
    }
}