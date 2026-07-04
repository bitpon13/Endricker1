package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GameStateRepository(private val gameStateDao: GameStateDao) {

    // Emits the current state or a default state if it's not created yet
    val gameStateFlow: Flow<GameState> = gameStateDao.getGameStateFlow().map { state ->
        state ?: GameState()
    }

    suspend fun getOrCreateState(): GameState = withContext(Dispatchers.IO) {
        gameStateDao.getGameState() ?: GameState()
    }

    suspend fun saveState(state: GameState) = withContext(Dispatchers.IO) {
        gameStateDao.insertOrUpdate(state)
    }

    suspend fun addClick(clickValue: Double) = withContext(Dispatchers.IO) {
        val currentState = getOrCreateState()
        val updated = currentState.copy(
            totalGoals = currentState.totalGoals + clickValue,
            totalClicks = currentState.totalClicks + 1
        )
        gameStateDao.insertOrUpdate(updated)
    }

    suspend fun addPassiveGoals(amount: Double) = withContext(Dispatchers.IO) {
        val currentState = getOrCreateState()
        val updated = currentState.copy(
            totalGoals = currentState.totalGoals + amount
        )
        gameStateDao.insertOrUpdate(updated)
    }

    suspend fun resetGame() = withContext(Dispatchers.IO) {
        gameStateDao.clearState()
        gameStateDao.insertOrUpdate(GameState())
    }
}
