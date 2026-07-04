package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_state")
data class GameState(
    @PrimaryKey val id: Int = 1,
    val totalGoals: Double = 0.0,
    val totalClicks: Long = 0,
    // Upgrades quantities
    val chuteDeOuroQty: Int = 0,       // +1 goal per click
    val treinoIntensivoQty: Int = 0,    // +1 goal per second (passive)
    val chuteiraDeOuroQty: Int = 0,     // +8 goals per click
    val patrocinioCbfQty: Int = 0,      // +15 goals per second (passive)
    val contratoEuropeuQty: Int = 0,    // +80 goals per second (passive)
    val parceriaNeymarQty: Int = 0,     // +250 goals per click
    val camisa9LendariaQty: Int = 0,    // +1,200 goals per second (passive)
    val copaDoMundoQty: Int = 0,        // +10,000 goals per second (passive)
    val bolaDeOuroQty: Int = 0,         // +50,000 goals per click
    val lendaEternaQty: Int = 0         // +500,000 goals per second (passive)
) {
    // Calculated values based on upgrades
    val goalsPerClick: Double
        get() = 1.0 + 
                (chuteDeOuroQty * 1.0) + 
                (chuteiraDeOuroQty * 8.0) + 
                (parceriaNeymarQty * 250.0) + 
                (bolaDeOuroQty * 50000.0)

    val goalsPerSecond: Double
        get() = (treinoIntensivoQty * 1.0) + 
                (patrocinioCbfQty * 15.0) + 
                (contratoEuropeuQty * 80.0) + 
                (camisa9LendariaQty * 1200.0) + 
                (copaDoMundoQty * 10000.0) + 
                (lendaEternaQty * 500000.0)
}
