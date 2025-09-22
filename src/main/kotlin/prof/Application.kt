package prof

import io.ktor.server.application.*
import io.ktor.server.netty.*
import prof.plugins.configureHTTP
import prof.plugins.configureRouting
import prof.plugins.configureSecurity
import prof.plugins.configureSerialization

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
