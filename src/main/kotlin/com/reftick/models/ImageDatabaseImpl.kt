package com.reftick.models

import com.reftick.models.*
import com.reftick.models.ImageDatabaseSingleton.dbQuery
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ImageDatabaseImpl : ImageDatabase{
    private fun resultRowToImage(row: ResultRow) = Image(
        id = row[Images.id],
        url = row[Images.url],
        tag = row[Images.tag],
        uploader = row[Images.uploader],
        author = row[Images.author]
    )

    override suspend fun allImages(): List<Image> = dbQuery {
        Images.selectAll().map(::resultRowToImage)
    }

    override suspend fun imagesByTag(tag: String): List<Image> = dbQuery {
        Images
            .select { Images.tag eq tag }
            .map(::resultRowToImage)
    }

    override suspend fun imageById(id: Int): Image? = dbQuery {
        Images
            .select { Images.id eq id }
            .map(::resultRowToImage)
            .singleOrNull()
    }

    override suspend fun addImage(image: Image): Image? = dbQuery {
        val insertStatement = Images.insert {
            it[Images.url] = image.url
            it[Images.tag] = image.tag
            it[Images.uploader] = image.uploader
            it[Images.author] = image.author
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToImage)
    }

    override suspend fun removeImage(id: Int): Boolean = dbQuery {
        Images.deleteWhere { Images.id eq id } > 0
    }

    override suspend fun imageByUploader(uploader: String): List<Image> = dbQuery {
        Images
            .select { Images.uploader eq uploader }
            .map(::resultRowToImage)
    }
}

val img = ImageDatabaseImpl().apply {
    runBlocking {

    }
}