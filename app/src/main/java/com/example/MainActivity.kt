package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.audio.GameSoundEngine
import com.example.data.GameRepository
import com.example.model.GAME_STAGES
import com.example.model.ScreenState
import com.example.model.StageDefinition
import com.example.ui.CinematicPrologueScreen
import com.example.ui.EndingCinematicScreen
import com.example.ui.GameplayScreen
import com.example.ui.MainMenuScreen
import com.example.ui.StageSelectScreen
import com.example.ui.UpgradeShopScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianDark

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = GameRepository(applicationContext)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ObsidianDark
                ) {
                    PawnRevengeApp(repository = repository)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GameSoundEngine.stopBattleMusic()
    }
}

@Composable
fun PawnRevengeApp(repository: GameRepository) {
    val upgrades by repository.upgrades.collectAsState()
    val isArabic by repository.isArabic.collectAsState()

    var currentScreen by remember { mutableStateOf(ScreenState.PROLOGUE_CINEMATIC) }
    var selectedStage by remember { mutableStateOf(GAME_STAGES[0]) }

    when (currentScreen) {
        ScreenState.PROLOGUE_CINEMATIC -> {
            CinematicPrologueScreen(
                isArabic = isArabic,
                onStartGame = {
                    val stageToPlay = GAME_STAGES.firstOrNull { it.id == upgrades.unlockedStage } ?: GAME_STAGES[0]
                    selectedStage = stageToPlay
                    currentScreen = ScreenState.GAMEPLAY
                }
            )
        }

        ScreenState.MAIN_MENU -> {
            MainMenuScreen(
                upgrades = upgrades,
                isArabic = isArabic,
                onStartStory = {
                    val stageToPlay = GAME_STAGES.firstOrNull { it.id == upgrades.unlockedStage } ?: GAME_STAGES[0]
                    selectedStage = stageToPlay
                    currentScreen = ScreenState.GAMEPLAY
                },
                onOpenStages = { currentScreen = ScreenState.STAGE_SELECT },
                onOpenShop = { currentScreen = ScreenState.UPGRADE_SHOP },
                onPlayCinematic = { currentScreen = ScreenState.PROLOGUE_CINEMATIC },
                onToggleLanguage = { repository.setLanguageArabic(!isArabic) }
            )
        }

        ScreenState.STAGE_SELECT -> {
            StageSelectScreen(
                upgrades = upgrades,
                isArabic = isArabic,
                onSelectStage = { stage ->
                    selectedStage = stage
                    currentScreen = ScreenState.GAMEPLAY
                },
                onOpenShop = { currentScreen = ScreenState.UPGRADE_SHOP },
                onPlayCinematic = { currentScreen = ScreenState.PROLOGUE_CINEMATIC },
                onBack = { currentScreen = ScreenState.MAIN_MENU }
            )
        }

        ScreenState.UPGRADE_SHOP -> {
            UpgradeShopScreen(
                upgrades = upgrades,
                isArabic = isArabic,
                onSaveUpgrades = { updated -> repository.saveUpgrades(updated) },
                onBack = { currentScreen = ScreenState.STAGE_SELECT }
            )
        }

        ScreenState.GAMEPLAY -> {
            GameplayScreen(
                stage = selectedStage,
                upgrades = upgrades,
                isArabic = isArabic,
                onStageCleared = { reward ->
                    repository.completeStage(selectedStage.id, reward)
                    if (selectedStage.id == 6) {
                        // Reached ending!
                        currentScreen = ScreenState.ENDING_CINEMATIC
                    } else {
                        currentScreen = ScreenState.STAGE_SELECT
                    }
                },
                onExitToStages = {
                    currentScreen = ScreenState.STAGE_SELECT
                }
            )
        }

        ScreenState.ENDING_CINEMATIC -> {
            EndingCinematicScreen(
                isArabic = isArabic,
                onReturnHome = {
                    currentScreen = ScreenState.MAIN_MENU
                }
            )
        }

        else -> {
            MainMenuScreen(
                upgrades = upgrades,
                isArabic = isArabic,
                onStartStory = { currentScreen = ScreenState.GAMEPLAY },
                onOpenStages = { currentScreen = ScreenState.STAGE_SELECT },
                onOpenShop = { currentScreen = ScreenState.UPGRADE_SHOP },
                onPlayCinematic = { currentScreen = ScreenState.PROLOGUE_CINEMATIC },
                onToggleLanguage = { repository.setLanguageArabic(!isArabic) }
            )
        }
    }
}
