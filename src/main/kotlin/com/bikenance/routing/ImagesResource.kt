package com.bikenance.routing

import com.bikenance.features.login.config.AppConfig
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*

fun Application.imageRoutes() {

    val config: AppConfig by inject()

    routing {

        static("/files") {
            staticRootFolder = File(config.storage.imageUploadFolder)
            files(".")
        }

        post("/files/upload") {
            val multipart = call.receiveMultipart()
            var fileName: String? = null
            try {
                multipart.forEachPart { partData ->
                    when (partData) {
                        is PartData.FormItem -> {
//                            //to read additional parameters that we sent with the image
//                            if (partData.name == "text") {
//                                text = partData.value
//                            }
                        }
                        is PartData.FileItem -> {
                            fileName = partData.save(config.storage.imageUploadFolder)
                        }
                        is PartData.BinaryItem -> Unit
                    }
                }

                println("F")

                call.respond(if(fileName != null) HttpStatusCode.OK else HttpStatusCode.InternalServerError, fileName ?: "")

            } catch (ex: Exception) {
                try {
                    //File("${config.storage.imageUploadFolder}/$fileName").delete()
                } catch (e: Exception) {
                }
                call.respond(HttpStatusCode.InternalServerError, "Error")
            }
        }
    }

}

fun PartData.FileItem.save(path: String): String {
    val fileBytes = streamProvider().readBytes()
    val fileExtension = originalFileName?.takeLastWhile { it != '.' }
    val fileName = UUID.randomUUID().toString() + "." + fileExtension
    val folder = File(path)
    folder.mkdir()
    println("Path = $path $fileName")
    File("${folder.absolutePath}/$fileName").writeBytes(fileBytes)
    return fileName
}