package com.reftick.models

import com.reftick.models.Image

interface ImageDatabase {
    suspend fun allImages(): List<Image>
    suspend fun imagesByTag(tag: String): List<Image>
    suspend fun imageById(id: Int): Image?
    suspend fun addImage(image: Image): Image?
    suspend fun removeImage(id: Int): Boolean
}