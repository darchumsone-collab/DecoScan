package com.darchums.decoscan.domain.usecase

import com.darchums.decoscan.data.EcoRepository
import com.darchums.decoscan.domain.model.EcoStats
import kotlinx.coroutines.flow.first

class UpdateEcoScoreUseCase(private val repository: EcoRepository) {

    suspend operator fun invoke(username: String, material: String, confidence: Float) {
        val currentStats = repository.getEcoStats(username).first()
        
        val weight = when (material.lowercase()) {
            "plastic" -> 1.0f
            "paper" -> 1.5f
            "glass" -> 2.0f
            "metal" -> 2.5f
            else -> 0.0f
        }

        val co2Grams = when (material.lowercase()) {
            "plastic" -> 6.0f
            "paper" -> 4.0f
            "glass" -> 8.0f
            "metal" -> 10.0f
            else -> 0.0f
        }

        val pointsEarned = weight * confidence
        val co2Saved = co2Grams * confidence

        val newStats = currentStats.copy(
            totalScans = currentStats.totalScans + 1,
            ecoScore = (currentStats.ecoScore + pointsEarned).coerceIn(0f, 100f),
            co2Saved = currentStats.co2Saved + co2Saved,
            plasticCount = if (material.lowercase() == "plastic") currentStats.plasticCount + 1 else currentStats.plasticCount,
            glassCount = if (material.lowercase() == "glass") currentStats.glassCount + 1 else currentStats.glassCount,
            paperCount = if (material.lowercase() == "paper") currentStats.paperCount + 1 else currentStats.paperCount,
            metalCount = if (material.lowercase() == "metal") currentStats.metalCount + 1 else currentStats.metalCount
        )

        repository.updateEcoStats(username, newStats)
    }
}
