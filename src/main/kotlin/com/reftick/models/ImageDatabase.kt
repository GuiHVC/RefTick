package com.reftick.models


interface ImageDatabase {
    suspend fun allImages(): List<Image>
    suspend fun imagesByTag(tag: String): List<Image>
    suspend fun imageById(id: Int): Image?
    suspend fun addImage(image: Image): Image?
    suspend fun removeImage(id: Int): Boolean
    suspend fun imageByUploader(uploader: String): List<Image>
}