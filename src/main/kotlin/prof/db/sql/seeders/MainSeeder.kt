package prof.db.sql.seeders

import kotlinx.coroutines.runBlocking

class MainSeeder {
    fun run() {
        println("🚀 Start seeding database...")

        runBlocking {
            UserSeeder().run()
            TermSeeder().run()
        }

        println("✅ Seeding compleet.")
    }
}