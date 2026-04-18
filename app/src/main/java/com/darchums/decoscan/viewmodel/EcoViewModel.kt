package com.darchums.decoscan.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.darchums.decoscan.data.EcoRepository
import com.darchums.decoscan.data.PreferenceManager
import com.darchums.decoscan.domain.model.EcoStats
import com.darchums.decoscan.domain.usecase.UpdateEcoScoreUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EcoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EcoRepository(PreferenceManager(application))
    private val updateEcoScoreUseCase = UpdateEcoScoreUseCase(repository)

    private val _ecoStats = MutableStateFlow(EcoStats())
    val ecoStats: StateFlow<EcoStats> = _ecoStats.asStateFlow()

    init {
        repository.getLoggedInUser()
            .filterNotNull()
            .flatMapLatest { username ->
                repository.getEcoStats(username)
            }
            .onEach { _ecoStats.value = it }
            .launchIn(viewModelScope)
    }

    fun updateEcoStats(material: String, confidence: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val username = repository.getLoggedInUser().first()
            if (username != null) {
                updateEcoScoreUseCase(username, material, confidence)
            }
        }
    }
}
