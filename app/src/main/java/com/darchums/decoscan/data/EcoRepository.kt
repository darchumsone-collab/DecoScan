package com.darchums.decoscan.data

import com.darchums.decoscan.domain.model.EcoStats
import kotlinx.coroutines.flow.Flow

class EcoRepository(private val preferenceManager: PreferenceManager) {

    fun getEcoStats(username: String): Flow<EcoStats> {
        return preferenceManager.getEcoStats(username)
    }

    suspend fun updateEcoStats(username: String, stats: EcoStats) {
        preferenceManager.updateEcoStats(username, stats)
    }

    fun getLoggedInUser(): Flow<String?> {
        return preferenceManager.loggedInUser
    }
}
