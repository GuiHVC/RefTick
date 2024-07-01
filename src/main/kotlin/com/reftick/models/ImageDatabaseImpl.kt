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
        if(allImages().isNotEmpty()) return@runBlocking
        addImage(Image(0, "https://images.pexels.com/photos/1054218/pexels-photo-1054218.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "mountains", "example", "Stephan Seeber"))
        addImage(Image(0, "https://images.pexels.com/photos/633198/pexels-photo-633198.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "mountains", "example", "Tyler Lastovich"))
        addImage(Image(0, "https://images.pexels.com/photos/2444429/pexels-photo-2444429.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "mountains", "example", "Chris Czermak"))
        addImage(Image(0, "https://images.pexels.com/photos/1366907/pexels-photo-1366907.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "mountains", "example", "Eberhard Grossgasteiger"))
        addImage(Image(0, "https://upload.wikimedia.org/wikipedia/commons/e/e7/Everest_North_Face_toward_Base_Camp_Tibet_Luca_Galuzzi_2006.jpg", "mountains", "example", "Luca Galuzzi"))
        addImage(Image(0, "https://images.pexels.com/photos/20759661/pexels-photo-20759661/free-photo-of-panorama-vista-paisagem-natureza.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "mountains", "example", "Robert Schader"))
        addImage(Image(0, "https://images.pexels.com/photos/1032650/pexels-photo-1032650.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "beach", "example", "Travis Rupert"))
        addImage(Image(0, "https://images.pexels.com/photos/632522/pexels-photo-632522.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "beach", "example", "Frans Van Heerden"))
        addImage(Image(0, "https://images.pexels.com/photos/176400/pexels-photo-176400.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "beach", "example", "Miro Alt"))
        addImage(Image(0, "https://images.pexels.com/photos/1586795/pexels-photo-1586795.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "beach", "example", "Athena Sandrini"))
        addImage(Image(0, "https://images.pexels.com/photos/773471/pexels-photo-773471.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "city", "example", "H. Emre"))
        addImage(Image(0, "https://images.pexels.com/photos/586687/pexels-photo-586687.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "city", "example", "David Bartus"))
        addImage(Image(0, "https://images.pexels.com/photos/2214035/pexels-photo-2214035.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "city", "example", "Timea Kadar"))
        addImage(Image(0, "https://images.pexels.com/photos/21005541/pexels-photo-21005541/free-photo-of-natureza-campo-area-interior.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "fields", "example", "Brazil Topno"))
        addImage(Image(0, "https://images.pexels.com/photos/15959961/pexels-photo-15959961/free-photo-of-natureza-campo-area-interior.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "fields", "example", "Elizabeth Tamara"))
        addImage(Image(0, "https://images.pexels.com/photos/18476255/pexels-photo-18476255/free-photo-of-natureza-campo-area-floresta.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "fields", "example", "Darya Grey_Owl"))
        addImage(Image(0, "https://images.pexels.com/photos/170811/pexels-photo-170811.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "cars", "example", "Mike Bird"))
        addImage(Image(0, "https://img.freepik.com/free-photo/modern-sports-car-speeds-through-dark-curve-generative-ai_188544-9136.jpg?size=626&ext=jpg&ga=GA1.1.1141335507.1719360000&semt=sph", "cars", "example", "example"))
        addImage(Image(0, "https://imgd.aeplcdn.com/370x208/n/cw/ec/130591/fronx-exterior-right-front-three-quarter-109.jpeg?isig=0&q=80", "cars", "example", "example"))
        addImage(Image(0, "https://imageio.forbes.com/specials-images/imageserve/5d35eacaf1176b0008974b54/0x0.jpg?format=jpg&crop=4560,2565,x790,y784,safe&height=900&width=1600&fit=bounds", "cars", "example", "example"))
        addImage(Image(0, "https://upload.wikimedia.org/wikipedia/commons/a/a4/2019_Toyota_Corolla_Icon_Tech_VVT-i_Hybrid_1.8.jpg", "cars", "example", "example"))
        addImage(Image(0, "https://images.pexels.com/photos/1715193/pexels-photo-1715193.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "motorcycles", "example", "Pragyan Bezbaruah"))
        addImage(Image(0, "https://images.pexels.com/photos/14674649/pexels-photo-14674649.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "motorcycles", "example", "Sai Krishna"))
        addImage(Image(0, "https://images.pexels.com/photos/18833082/pexels-photo-18833082/free-photo-of-estrada-via-rua-veiculo.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "motorcycles", "example", "Muhammad Muneeb"))
        addImage(Image(0, "https://images.pexels.com/photos/8425811/pexels-photo-8425811.png?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "motorcycles", "example", "Aqib Ahmed"))
        addImage(Image(0, "https://images.pexels.com/photos/6934325/pexels-photo-6934325.png?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "portraits", "example", "Vincent Tan"))
        addImage(Image(0, "https://images.pexels.com/photos/5636942/pexels-photo-5636942.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "portraits", "example", "Dapo Abideen"))
        addImage(Image(0, "https://images.pexels.com/photos/2698935/pexels-photo-2698935.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "portraits", "example", "Philip Boakye"))
        addImage(Image(0, "https://images.pexels.com/photos/1777792/pexels-photo-1777792.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "poses", "example", "Juliano Ferreira"))
        addImage(Image(0, "https://images.pexels.com/photos/1887089/pexels-photo-1887089.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "poses", "example", "Balram Swain"))
        addImage(Image(0, "https://images.pexels.com/photos/1816225/pexels-photo-1816225.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "poses", "example", "Zachary DeBottis"))
        addImage(Image(0, "https://images.pexels.com/photos/1764506/pexels-photo-1764506.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "poses", "example", "Hussein Altameemi"))
        addImage(Image(0, "https://images.pexels.com/photos/2234912/pexels-photo-2234912.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "poses", "example", "Marcelo Moreira"))
        addImage(Image(0, "https://images.pexels.com/photos/3534674/pexels-photo-3534674.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "poses", "example", "Ola Dapo"))
    }
}