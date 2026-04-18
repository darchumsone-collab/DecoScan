package com.darchums.decoscan.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.darchums.decoscan.domain.model.EcoStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val LOGGED_IN_USER = stringPreferencesKey("logged_in_user")
        
        private fun userKey(prefix: String, username: String) = "${prefix}_$username"
        
        const val PWD_PREFIX = "user_pwd"
        const val ECO_SCORE_PREFIX = "user_eco_score"
        const val CO2_SAVED_PREFIX = "user_co2_saved"
        const val TOTAL_SCANS_PREFIX = "user_scans"
        const val PLASTIC_PREFIX = "user_plastic"
        const val GLASS_PREFIX = "user_glass"
        const val PAPER_PREFIX = "user_paper"
        const val METAL_PREFIX = "user_metal"
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }

    val loggedInUser: Flow<String?> = context.dataStore.data.map { it[LOGGED_IN_USER] }

    suspend fun setLoggedInUser(username: String?) {
        context.dataStore.edit { 
            if (username == null) it.remove(LOGGED_IN_USER) else it[LOGGED_IN_USER] = username 
        }
    }

    suspend fun saveUser(username: String, passwordHash: String) {
        context.dataStore.edit { it[stringPreferencesKey(userKey(PWD_PREFIX, username))] = passwordHash }
    }

    fun getPasswordHash(username: String): Flow<String?> = context.dataStore.data.map { it[stringPreferencesKey(userKey(PWD_PREFIX, username))] }

    fun getEcoStats(username: String): Flow<EcoStats> = context.dataStore.data.map { prefs ->
        EcoStats(
            totalScans = prefs[intPreferencesKey(userKey(TOTAL_SCANS_PREFIX, username))] ?: 0,
            ecoScore = prefs[floatPreferencesKey(userKey(ECO_SCORE_PREFIX, username))] ?: 0f,
            co2Saved = prefs[floatPreferencesKey(userKey(CO2_SAVED_PREFIX, username))] ?: 0f,
            plasticCount = prefs[intPreferencesKey(userKey(PLASTIC_PREFIX, username))] ?: 0,
            glassCount = prefs[intPreferencesKey(userKey(GLASS_PREFIX, username))] ?: 0,
            paperCount = prefs[intPreferencesKey(userKey(PAPER_PREFIX, username))] ?: 0,
            metalCount = prefs[intPreferencesKey(userKey(METAL_PREFIX, username))] ?: 0
        )
    }

    suspend fun updateEcoStats(username: String, stats: EcoStats) {
        context.dataStore.edit { prefs ->
            prefs[intPreferencesKey(userKey(TOTAL_SCANS_PREFIX, username))] = stats.totalScans
            prefs[floatPreferencesKey(userKey(ECO_SCORE_PREFIX, username))] = stats.ecoScore
            prefs[floatPreferencesKey(userKey(CO2_SAVED_PREFIX, username))] = stats.co2Saved
            prefs[intPreferencesKey(userKey(PLASTIC_PREFIX, username))] = stats.plasticCount
            prefs[intPreferencesKey(userKey(GLASS_PREFIX, username))] = stats.glassCount
            prefs[intPreferencesKey(userKey(PAPER_PREFIX, username))] = stats.paperCount
            prefs[intPreferencesKey(userKey(METAL_PREFIX, username))] = stats.metalCount
        }
    }

    // Fallback/Legacy support for original material incrementing
    suspend fun incrementMaterialCount(username: String, material: String) {
        context.dataStore.edit { prefs ->
            val key = intPreferencesKey("user_${material.lowercase()}_$username")
            val current = prefs[key] ?: 0
            prefs[key] = current + 1
        }
    }
}
