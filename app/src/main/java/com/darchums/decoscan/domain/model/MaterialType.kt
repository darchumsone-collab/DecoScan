package com.darchums.decoscan.domain.model

enum class MaterialType(val displayName: String, val ecoPoints: Int, val co2Saved: Float) {
    PLASTIC("Plastic", 10, 0.5f),
    GLASS("Glass", 20, 0.8f),
    PAPER("Paper", 15, 0.3f),
    METAL("Metal", 25, 1.2f),
    UNKNOWN("Unknown", 0, 0.0f);

    companion object {
        fun fromString(value: String): MaterialType {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
        }
    }
}
