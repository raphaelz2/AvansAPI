package prof.plugins

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.routing.*

// OpenAPI + Swagger UI (smiley4)


fun Application.configureDocs() {
    // Generate OpenAPI from your existing Ktor routes
    install(OpenApi) {
         info {
             title = "Avans API"
             version = "1.0.0"
             description = "Car rental API"
         }
    }

    // Expose the spec and the Swagger UI
    routing {
        route("/api.json") { openApi() }           // OpenAPI JSON
        route("/swagger")  { swaggerUI("/api.json") }  // Swagger UI
    }
}
