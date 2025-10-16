package prof.security

import org.mindrot.jbcrypt.BCrypt

object Passwords {
    /** Hash a plain-text password for storage. */
    fun hash(plain: String): String =
        BCrypt.hashpw(plain, BCrypt.gensalt(12)) // cost 12 is good locally; 10–12 typical

    /** Verify a plain-text password against a stored bcrypt hash. */
    fun verify(plain: String, hash: String): Boolean {
        val result = BCrypt.checkpw(plain, hash)
        println("Password match: $result") // log eventueel het resultaat
        return result
    }
}
