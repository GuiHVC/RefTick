package com.reftick.models

import com.reftick.dao.DatabaseSingleton.dbQuery
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

val img: ImageDatabase = ImageDatabaseImpl().apply {
    runBlocking {
        addImage(Image(0, "https://images.pexels.com/photos/170811/pexels-photo-170811.jpeg?cs=srgb&dl=pexels-mikebirdy-170811.jpg&fm=jpg", "cars", "example", "example"))
        addImage(Image(0, "https://img.freepik.com/free-photo/modern-sports-car-speeds-through-dark-curve-generative-ai_188544-9136.jpg?size=626&ext=jpg&ga=GA1.1.1141335507.1719360000&semt=sph", "cars", "example", "example"))
        addImage(Image(0, "https://imgd.aeplcdn.com/370x208/n/cw/ec/130591/fronx-exterior-right-front-three-quarter-109.jpeg?isig=0&q=80", "cars", "example", "example"))
        addImage(Image(0, "https://imageio.forbes.com/specials-images/imageserve/5d35eacaf1176b0008974b54/0x0.jpg?format=jpg&crop=4560,2565,x790,y784,safe&height=900&width=1600&fit=bounds", "cars", "example", "example"))
        addImage(Image(0, "https://upload.wikimedia.org/wikipedia/commons/a/a4/2019_Toyota_Corolla_Icon_Tech_VVT-i_Hybrid_1.8.jpg", "cars", "example", "example"))
    }
}