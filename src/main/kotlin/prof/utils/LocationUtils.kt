package prof.utils

import kotlin.math.*

object LocationUtils {

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)

        val a = sin(dLat / 2).pow(2) +
                sin(dLon / 2).pow(2) * cos(lat1Rad) * cos(lat2Rad)
        val c = 2 * asin(sqrt(a))

        return earthRadiusKm * c
    }

    fun parseCoordinates(location: String?): Pair<Double, Double>? {
        if (location.isNullOrBlank()) return null

        return try {
            val parts = location.split(",").map { it.trim() }
            if (parts.size != 2) return null

            val lat = parts[0].toDoubleOrNull() ?: return null
            val lon = parts[1].toDoubleOrNull() ?: return null

            if (lat < -90 || lat > 90 || lon < -180 || lon > 180) return null

            Pair(lat, lon)
        } catch (e: Exception) {
            null
        }
    }
}