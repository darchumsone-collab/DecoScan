package com.darchums.decoscan.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val LOGGED_IN_USER = stringPreferencesKey("logged_in_user")
        val USER_PASSWORD_PREFIX = "user_pwd_"
        val USER_ECO_SCORE_PREFIX = "user_score_"
        val USER_SCANS_PREFIX = "user_scans_"
        
        // Material breakdown keys
        val USER_PLASTIC_COUNT = "user_plastic_"
        val USER_GLASS_COUNT = "user_glass_"
        val USER_PAPER_COUNT = "user_paper_"
        val USER_METAL_COUNT = "user_metal_"
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    val loggedInUser: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[LOGGED_IN_USER]
        }

    suspend fun setLoggedInUser(username: String?) {
        context.dataStore.edit { preferences ->
            if (username == null) {
                preferences.remove(LOGGED_IN_USER)
            } else {
                preferences[LOGGED_IN_USER] = username
            }
        }
    }

    suspend fun saveUser(username: String, passwordHash: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(USER_PASSWORD_PREFIX + username)] = passwordHash
        }
    }

    fun getPasswordHash(username: String): Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey(USER_PASSWORD_PREFIX + username)]
        }

    fun getEcoScore(username: String): Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[intPreferencesKey(USER_ECO_SCORE_PREFIX + username)] ?: 0
        }

    suspend fun updateEcoScore(username: String, points: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[intPreferencesKey(USER_ECO_SCORE_PREFIX + username)] ?: 0
            preferences[intPreferencesKey(USER_ECO_SCORE_PREFIX + username)] = (current + points).coerceIn(0, 100)
        }
    }

    fun getMaterialCount(username: String, material: String): Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[intPreferencesKey("user_${material.lowercase()}_$username")] ?: 0
        }

    suspend fun incrementMaterialCount(username: String, material: String) {
        context.dataStore.edit { preferences ->
            val key = intPreferencesKey("user_${material.lowercase()}_$username")
            val current = preferences[key] ?: 0
            preferences[key] = current + 1
            
            val totalKey = intPreferencesKey(USER_SCANS_PREFIX + username)
            val currentTotal = preferences[totalKey] ?: 0
            preferences[totalKey] = currentTotal + 1
        }
    }

    fun getTotalScans(username: String): Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[intPreferencesKey(USER_SCANS_PREFIX + username)] ?: 0
        }
}
