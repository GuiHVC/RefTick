package com.reftick.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Image(val id: Int, val url: String, val tag: String, val uploader: String, val author: String)

object Images : Table() {
    val id = integer("id").autoIncrement()
    val url = varchar("url", 500)
    val tag = varchar("tag", 255)
    val uploader = varchar("uploader", 255)
    val author = varchar("author", 255)

    override val primaryKey = PrimaryKey(id)
}