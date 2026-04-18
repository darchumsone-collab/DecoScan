package com.darchums.decoscan.domain.model

data class EcoStats(
    val totalScans: Int = 0,
    val ecoScore: Float = 0f,
    val co2Saved: Float = 0f, // in grams
    val plasticCount: Int = 0,
    val glassCount: Int = 0,
    val paperCount: Int = 0,
    val metalCount: Int = 0
) {
    fun getLevel(): String {
        return when {
            ecoScore <= 20 -> "Beginner Recycler"
            ecoScore <= 50 -> "Eco Starter"
            ecoScore <= 80 -> "Green Warrior"
            else -> "Planet Guardian"
        }
    }
}
