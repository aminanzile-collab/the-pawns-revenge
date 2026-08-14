package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.PlayerUpgrades
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("pawn_revenge_prefs", Context.MODE_PRIVATE)

    private val _upgrades = MutableStateFlow(loadUpgrades())
    val upgrades: StateFlow<PlayerUpgrades> = _upgrades.asStateFlow()

    private val _isArabic = MutableStateFlow(prefs.getBoolean("pref_arabic", true))
    val isArabic: StateFlow<Boolean> = _isArabic.asStateFlow()

    private fun loadUpgrades(): PlayerUpgrades {
        val completedStr = prefs.getString("completed_stages", "") ?: ""
        val completedSet = if (completedStr.isEmpty()) emptySet() else completedStr.split(",").mapNotNull { it.toIntOrNull() }.toSet()

        return PlayerUpgrades(
            swordLevel = prefs.getInt("sword_level", 1),
            shieldLevel = prefs.getInt("shield_level", 1),
            healthLevel = prefs.getInt("health_level", 1),
            speedLevel = prefs.getInt("speed_level", 1),
            fireSkillUnlocked = prefs.getBoolean("fire_skill", false),
            lightningSkillUnlocked = prefs.getBoolean("lightning_skill", false),
            ultimateSkillUnlocked = prefs.getBoolean("ultimate_skill", false),
            coins = prefs.getInt("coins", 50), // Starting bonus
            unlockedStage = prefs.getInt("unlocked_stage", 1),
            completedStages = completedSet,
            totalKills = prefs.getInt("total_kills", 0)
        )
    }

    fun saveUpgrades(upgrades: PlayerUpgrades) {
        _upgrades.value = upgrades
        prefs.edit().apply {
            putInt("sword_level", upgrades.swordLevel)
            putInt("shield_level", upgrades.shieldLevel)
            putInt("health_level", upgrades.healthLevel)
            putInt("speed_level", upgrades.speedLevel)
            putBoolean("fire_skill", upgrades.fireSkillUnlocked)
            putBoolean("lightning_skill", upgrades.lightningSkillUnlocked)
            putBoolean("ultimate_skill", upgrades.ultimateSkillUnlocked)
            putInt("coins", upgrades.coins)
            putInt("unlocked_stage", upgrades.unlockedStage)
            putString("completed_stages", upgrades.completedStages.joinToString(","))
            putInt("total_kills", upgrades.totalKills)
            apply()
        }
    }

    fun addCoins(amount: Int) {
        val current = _upgrades.value
        val updated = current.copy(coins = current.coins + amount)
        saveUpgrades(updated)
    }

    fun completeStage(stageId: Int, rewardCoins: Int) {
        val current = _upgrades.value
        val nextStage = if (stageId >= current.unlockedStage) (stageId + 1).coerceAtMost(6) else current.unlockedStage
        val updatedCompleted = current.completedStages + stageId
        val updated = current.copy(
            unlockedStage = nextStage,
            completedStages = updatedCompleted,
            coins = current.coins + rewardCoins
        )
        saveUpgrades(updated)
    }

    fun setLanguageArabic(isArabic: Boolean) {
        _isArabic.value = isArabic
        prefs.edit().putBoolean("pref_arabic", isArabic).apply()
    }

    fun resetProgress() {
        val fresh = PlayerUpgrades(coins = 50, unlockedStage = 1)
        saveUpgrades(fresh)
    }
}
