package org.example.utils

import java.io.File
import java.util.*

object ImageUtils {
    private const val PROFILE_IMAGES_DIR = "uploads/profile_images"

    init {
        File(PROFILE_IMAGES_DIR).mkdirs()
    }

    fun saveBase64Image(base64: String, username: String): String {
        val parts = base64.split(",")
        if (parts.size != 2) throw IllegalArgumentException("Invalid base64 format")

        val mimeType = parts[0].substringAfter(":").substringBefore(";")
        val extension = when {
            mimeType.contains("jpeg") -> "jpg"
            mimeType.contains("png") -> "png"
            else -> throw IllegalArgumentException("Unsupported image type")
        }

        val bytes = Base64.getDecoder().decode(parts[1])
        val filename = "$username-${UUID.randomUUID()}.$extension"
        val file = File("$PROFILE_IMAGES_DIR/$filename")
        file.writeBytes(bytes)

        return filename
    }

    fun getProfileImage(filename: String): File {
        return File("$PROFILE_IMAGES_DIR/$filename")
    }
}