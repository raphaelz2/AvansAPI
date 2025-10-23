package prof.db.sql.seeders

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import prof.Requests.CreateTermRequest
import prof.db.sql.SqlTermRepository
import prof.db.sql.migrations.Terms
import prof.db.sql.migrations.Users

class TermSeeder (
    private val termRepository: SqlTermRepository = SqlTermRepository()
) {
    suspend fun run() {
        if (Terms.selectAll().empty()) {
            println("🌱 TermSeeder gestart...")

            val users = transaction { Users.selectAll().map { it[Users.id] } }

            val terms = listOf(
                CreateTermRequest(
                    title = "Verzekering en Aansprakelijkheid",
                    content = """
                        De huurder is verplicht het voertuig te gebruiken in overeenstemming met de verzekeringsovereenkomst die van toepassing is. 
                        Eventuele schade aan het voertuig of derden die ontstaat tijdens de huurperiode valt onder de verantwoordelijkheid van de huurder, tenzij de schade gedekt wordt door de verzekering. 
                        De huurder is verplicht alle schade direct te melden en samen te werken bij het invullen van schadeformulieren. 
                        Verzekeringsfraude of het opzettelijk veroorzaken van schade kan leiden tot juridische stappen en volledige aansprakelijkheid voor de kosten.
                        """.trimIndent(),
                    active = true,
                ),
                CreateTermRequest(
                    title = "Annulering en Wijziging van de Reservering",
                    content = """
                        Annuleringen of wijzigingen van een reservering moeten tijdig worden doorgegeven volgens de geldende annuleringsvoorwaarden. 
                        Afhankelijk van het moment van annulering kan een deel van de betaalde huurprijs worden ingehouden als annuleringskosten. 
                        Wijzigingen in de huurperiode of het type voertuig zijn afhankelijk van beschikbaarheid en moeten door de verhuurder worden bevestigd. 
                        De huurder is verantwoordelijk voor het verifiëren van de bevestigde wijziging en het tijdig nakomen van de nieuwe afspraken.
                         """.trimIndent(),
                    active = true,
                ),
            )

            users.forEach { userId ->
                terms.forEach { term ->
                    try {
                        termRepository.create(term,userId )
                        println("✅ Term '${term.title}' toegevoegd voor gebruiker $userId")
                    } catch (e: Exception) {
                        println("⚠️ Kon Term '${term.title}' niet toevoegen voor gebruiker $userId: ${e.message}")
                    }
                }
            }

            println("✅ TermSeeder klaar.")
        }
    }
}