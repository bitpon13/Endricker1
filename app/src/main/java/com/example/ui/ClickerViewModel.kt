package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.GameState
import com.example.data.GameStateRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

// Dynamic visual click particle
data class ClickParticle(
    val id: String = UUID.randomUUID().toString(),
    val x: Float,
    val y: Float,
    val text: String,
    val emoji: String = "⚽",
    val createdAt: Long = System.currentTimeMillis()
)

// Definition of each Football Upgrade
data class SoccerUpgrade(
    val id: String,
    val name: String,
    val description: String,
    val baseCost: Double,
    val clickIncrease: Double = 0.0,
    val passiveIncrease: Double = 0.0,
    val icon: String, // Emoji representation
    val costMultiplier: Double = 1.15
) {
    fun getCurrentCost(qty: Int): Double {
        return baseCost * Math.pow(costMultiplier, qty.toDouble())
    }
}

class ClickerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameStateRepository
    
    // In-memory GameState for high frequency lag-free updates (60fps)
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Active click particles currently shown on screen
    private val _particles = MutableStateFlow<List<ClickParticle>>(emptyList())
    val particles: StateFlow<List<ClickParticle>> = _particles.asStateFlow()

    // Upgrades catalog
    val upgrades = listOf(
        SoccerUpgrade(
            id = "chute_ouro",
            name = "Chute de Ouro",
            description = "Melhora a precisão do chute. +1 gol por clique.",
            baseCost = 15.0,
            clickIncrease = 1.0,
            icon = "⚽"
        ),
        SoccerUpgrade(
            id = "treino_int",
            name = "Treino de Arrancada",
            description = "Foco na velocidade. +1 gol passivo por segundo.",
            baseCost = 100.0,
            passiveIncrease = 1.0,
            icon = "🏃‍♂️"
        ),
        SoccerUpgrade(
            id = "chuteira_ouro",
            name = "Chuteira de Ouro",
            description = "Chuteira canônica brasileira. +8 gols por clique.",
            baseCost = 500.0,
            clickIncrease = 8.0,
            icon = "👟"
        ),
        SoccerUpgrade(
            id = "patrocinio_cbf",
            name = "Patrocínio Oficial",
            description = "Suporte de marca nacional. +15 gols por segundo.",
            baseCost = 3000.0,
            passiveIncrease = 15.0,
            icon = "💰"
        ),
        SoccerUpgrade(
            id = "contrato_eur",
            name = "Contrato na Europa",
            description = "Assinatura milionária. +80 gols por segundo.",
            baseCost = 15000.0,
            passiveIncrease = 80.0,
            icon = "🇪🇺"
        ),
        SoccerUpgrade(
            id = "parceria_ney",
            name = "Parceria de Camisa",
            description = "Dupla dinâmica em campo. +250 gols por clique.",
            baseCost = 80000.0,
            clickIncrease = 250.0,
            icon = "🤙"
        ),
        SoccerUpgrade(
            id = "camisa_9",
            name = "Camisa 9 Canarinha",
            description = "O manto sagrado de atacante. +1.200 gols por segundo.",
            baseCost = 400000.0,
            passiveIncrease = 1200.0,
            icon = "👕"
        ),
        SoccerUpgrade(
            id = "copa_mundo",
            name = "Glória na Copa",
            description = "O troféu mais cobiçado. +10.000 gols por segundo.",
            baseCost = 2500000.0,
            passiveIncrease = 10000.0,
            icon = "🏆"
        ),
        SoccerUpgrade(
            id = "bola_ouro",
            name = "Bola de Ouro",
            description = "Melhor jogador do mundo. +50.000 gols por clique.",
            baseCost = 15000000.0,
            clickIncrease = 50000.0,
            icon = "🟡"
        ),
        SoccerUpgrade(
            id = "lenda_eterna",
            name = "Lenda Eterna",
            description = "Eternizado na Calçada da Fama. +500.000 gols por segundo.",
            baseCost = 100000000.0,
            passiveIncrease = 500000.0,
            icon = "👑"
        )
    )

    private var passiveLoopJob: Job? = null
    private var databaseSyncJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GameStateRepository(database.gameStateDao())

        // Load the state from Room database on startup
        viewModelScope.launch {
            val dbState = repository.gameStateFlow.first()
            _gameState.value = dbState

            startGameplayLoops()
        }
    }

    private fun startGameplayLoops() {
        // High-frequency UI tick for passive goals (every 50ms) to make progress smooth
        passiveLoopJob = viewModelScope.launch {
            val tickRateMs = 50L
            while (true) {
                delay(tickRateMs)
                val gps = _gameState.value.goalsPerSecond
                if (gps > 0) {
                    val gain = gps * (tickRateMs / 1000.0)
                    _gameState.update { it.copy(totalGoals = it.totalGoals + gain) }
                }
            }
        }

        // Periodic Room sync (every 2 seconds) to keep DB up to date in the background
        databaseSyncJob = viewModelScope.launch {
            while (true) {
                delay(2000L)
                syncToDatabase()
            }
        }

        // Particle cleanup loop (remove particles older than 800ms)
        viewModelScope.launch {
            while (true) {
                delay(200L)
                val now = System.currentTimeMillis()
                _particles.update { current ->
                    current.filter { now - it.createdAt < 800 }
                }
            }
        }
    }

    private suspend fun syncToDatabase() {
        val current = _gameState.value
        repository.saveState(current)
    }

    fun clickEndrick(x: Float, y: Float) {
        val state = _gameState.value
        val clickVal = state.goalsPerClick
        
        // Add to goals and total clicks
        _gameState.update {
            it.copy(
                totalGoals = it.totalGoals + clickVal,
                totalClicks = it.totalClicks + 1
            )
        }

        // Add a rising particle for "+X"
        val displayVal = if (clickVal % 1.0 == 0.0) {
            "+${clickVal.toLong()}"
        } else {
            "+${String.format("%.1f", clickVal)}"
        }

        val newParticle = ClickParticle(
            x = x,
            y = y,
            text = displayVal
        )
        _particles.update { it + newParticle }
    }

    fun buyUpgrade(upgrade: SoccerUpgrade) {
        val state = _gameState.value
        val currentQty = getUpgradeQty(upgrade.id, state)
        val cost = upgrade.getCurrentCost(currentQty)

        if (state.totalGoals >= cost) {
            _gameState.update { current ->
                val newGoals = current.totalGoals - cost
                val newState = updateUpgradeQty(upgrade.id, currentQty + 1, current)
                newState.copy(totalGoals = newGoals)
            }
            
            // Force save to database immediately on purchase
            viewModelScope.launch {
                syncToDatabase()
            }
        }
    }

    fun getUpgradeQty(id: String, state: GameState): Int {
        return when (id) {
            "chute_ouro" -> state.chuteDeOuroQty
            "treino_int" -> state.treinoIntensivoQty
            "chuteira_ouro" -> state.chuteiraDeOuroQty
            "patrocinio_cbf" -> state.patrocinioCbfQty
            "contrato_eur" -> state.contratoEuropeuQty
            "parceria_ney" -> state.parceriaNeymarQty
            "camisa_9" -> state.camisa9LendariaQty
            "copa_mundo" -> state.copaDoMundoQty
            "bola_ouro" -> state.bolaDeOuroQty
            "lenda_eterna" -> state.lendaEternaQty
            else -> 0
        }
    }

    private fun updateUpgradeQty(id: String, newQty: Int, state: GameState): GameState {
        return when (id) {
            "chute_ouro" -> state.copy(chuteDeOuroQty = newQty)
            "treino_int" -> state.copy(treinoIntensivoQty = newQty)
            "chuteira_ouro" -> state.copy(chuteiraDeOuroQty = newQty)
            "patrocinio_cbf" -> state.copy(patrocinioCbfQty = newQty)
            "contrato_eur" -> state.copy(contratoEuropeuQty = newQty)
            "parceria_ney" -> state.copy(parceriaNeymarQty = newQty)
            "camisa_9" -> state.copy(camisa9LendariaQty = newQty)
            "copa_mundo" -> state.copy(copaDoMundoQty = newQty)
            "bola_ouro" -> state.copy(bolaDeOuroQty = newQty)
            "lenda_eterna" -> state.copy(lendaEternaQty = newQty)
            else -> state
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            repository.resetGame()
            _gameState.value = GameState()
            _particles.value = emptyList()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Final sync attempt to save state
        viewModelScope.launch {
            syncToDatabase()
        }
        passiveLoopJob?.cancel()
        databaseSyncJob?.cancel()
    }
}
