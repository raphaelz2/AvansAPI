package prof.db.sql.seeders

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.delay
import org.jetbrains.exposed.sql.selectAll
import prof.db.sql.SqlCarRepository
import prof.db.sql.SqlEntityAttributeRepository
import prof.db.sql.migrations.Cars
import prof.db.sql.migrations.CarImages
import java.io.File
import java.util.*

class CarImageSeeder(
    val carRepository: SqlCarRepository = SqlCarRepository(
        entityAttributeRepo = SqlEntityAttributeRepository()
    )
) {
    private val client = HttpClient(CIO)

    private val carImageUrls = listOf(
       "https://www.change.inc/app/uploads/2022/02/Microlino-2.0.png",
       "https://static.overfuel.com/dealers/trust-auto/image/Pickup-Truck-Hennessey-Mammoth-1024x576.jpg",
        "https://www.carscoops.com/wp-content/uploads/2022/04/Ram-1500-TRX-1a.jpg",
        "https://www.topgear.com/sites/default/files/news-listicle/image/2023/11/TRUCKSLEAD.jpg",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSdM-awacXaPa2vhQgz_rEyyMEtJmV2ebeBlfrhESYStyDR2zA-sUn31JDC5-aUdabI80o&usqp=CAU",
        "https://www.forcesnews.com/sites/default/files/617%20Squadron%20F-35B%20jet%20lands%20on%20HMS%20Prince%20of%20Wales%20after%20participating%20in%20Exercise%20NORDIC%20RESPONSE%20050324%20CREDIT%20MOD.jpg",
    )

    suspend fun run() {
        if (CarImages.selectAll().empty()) {
            println("🚗📸 CarImageSeeder start...")

            val allCars = Cars.selectAll().map { it[Cars.id] }

            if (allCars.isEmpty()) {
                println("⚠️ No cars found")
                return
            }

            println("📊 ${allCars.size} cars found")

            val uploadDir = File("uploads/cars")
            if (!uploadDir.exists()) {
                uploadDir.mkdirs()
            }

            allCars.forEach { carId ->
                try {
                    val numberOfImages = (1..2).random()

                    val selectedUrls = carImageUrls.shuffled().take(numberOfImages)

                    println("🎯 Downloading $numberOfImages images vor car ID: $carId")

                    val fileNames = mutableListOf<String>()

                    selectedUrls.forEach { url ->
                        try {
                            val response: HttpResponse = client.get(url)
                            val bytes = response.readBytes()

                            val uniqueFileName = "${UUID.randomUUID()}.jpg"
                            val file = File(uploadDir, uniqueFileName)

                            file.writeBytes(bytes)
                            fileNames.add(uniqueFileName)

                            println("  ✅ Downloaded: $uniqueFileName")

                            delay(300)

                        } catch (e: Exception) {
                            println("  ⚠️ error bij downloding from URL: ${e.message}")
                        }
                    }

                    if (fileNames.isNotEmpty()) {
                        carRepository.addImages(carId, fileNames)
                        println("  ✅ ${fileNames.size} images create to car $carId")
                    }

                } catch (e: Exception) {
                    println("⚠️ error for car $carId: ${e.message}")
                }
            }

            client.close()
            println("✅ CarImageSeeder ready.")
        } else {
            println("ℹ️ CarImages table contens al data from seeder")
        }
    }
}