package prof.db.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseFactory {
    fun init(environment: ApplicationEnvironment) {
        val config = environment.config
        val jdbcUrl = config.propertyOrNull("db.jdbcUrl")?.getString() ?: "jdbc:sqlite:data/app.db"
        val driver = config.propertyOrNull("db.driver")?.getString() ?: "org.sqlite.JDBC"
        if (jdbcUrl.startsWith("jdbc:sqlite:")) {
            val path = jdbcUrl.removePrefix("jdbc:sqlite:")
            File(path).parentFile?.mkdirs()
        }

        val hikariConfig = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.driverClassName = driver
            maximumPoolSize = 5
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_SERIALIZABLE"
            validate()
        }
        val dataSource = HikariDataSource(hikariConfig)

        Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
            .migrate()

        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(Users, Cars, Reservations, CarImages, TelemetryLogs, Terms)

            //seeders
            prof.db.sql.seeders.MainSeeder().run()
        }
    }
}
