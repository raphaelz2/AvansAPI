package prof.db.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import prof.db.sql.migrations.CarImages
import prof.db.sql.migrations.Cars
import prof.db.sql.migrations.EntityAttributes
import prof.db.sql.migrations.Reservations
import prof.db.sql.migrations.TelemetryLogs
import prof.db.sql.migrations.Terms
import prof.db.sql.migrations.Users
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
            // Create tables and also add new columns when the schema evolves (e.g. Users.disabled)
            SchemaUtils.createMissingTablesAndColumns(Users, Cars, EntityAttributes, Reservations, CarImages, TelemetryLogs, Terms)

            //seeders
            prof.db.sql.seeders.MainSeeder().run()
        }
    }
}
