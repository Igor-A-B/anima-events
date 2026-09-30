package com.example.anima.core.image

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

// posts an image as multipart/form-data, in a part named "file"
// knows nothing about what the image is for, the path decides that (events/{id}/images, a profile photo, ...)
// failures come back as ApiException, like every other call on the client
class ImageUploader(val client: HttpClient) {

    suspend fun send(path: String, image: PickedImage): HttpResponse =
        client.submitFormWithBinaryData(
            url = path,
            formData = formData {
                append(
                    key = FILE_PART,
                    value = image.bytes,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, image.mimeType)
                        append(HttpHeaders.ContentDisposition, "filename=\"${image.fileName.replace("\"", "")}\"")
                    },
                )
            },
        )

    // the response body, decoded
    suspend inline fun <reified T> upload(path: String, image: PickedImage): T = send(path, image).body()

    private companion object {
        const val FILE_PART = "file"
    }
}
