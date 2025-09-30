package prof.security

// Data class om registratieverzoek te modelleren (de JSON input die de client stuurt)
data class RegisterRequest(
    val username: String,
    val password: String
)

// Data class om een gebruiker op te slaan (met gehashed wachtwoord)
data class User(
    val username: String,
    val passwordHash: String
)