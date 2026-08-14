package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameEngine
import com.example.model.Enemy
import com.example.model.EnemyType
import com.example.model.PlayerUpgrades
import com.example.model.Projectile
import com.example.model.ProjectileType
import com.example.model.StageDefinition
import com.example.ui.theme.BloodRed
import com.example.ui.theme.BossHealthRed
import com.example.ui.theme.ChessBoardDark
import com.example.ui.theme.ChessBoardTile
import com.example.ui.theme.FireOrange
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.HealthGreen
import com.example.ui.theme.LightningCyan
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.ShieldCyan
import com.example.ui.theme.StaminaBlue
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun GameplayScreen(
    stage: StageDefinition,
    upgrades: PlayerUpgrades,
    isArabic: Boolean,
    onStageCleared: (rewardCoins: Int) -> Unit,
    onExitToStages: () -> Unit
) {
    var isPaused by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var isVictory by remember { mutableStateOf(false) }
    var coinsWon by remember { mutableStateOf(0) }

    // Touch controls state
    var moveLeftPressed by remember { mutableStateOf(false) }
    var moveRightPressed by remember { mutableStateOf(false) }
    var jumpPressed by remember { mutableStateOf(false) }
    var attackPressed by remember { mutableStateOf(false) }
    var blockPressed by remember { mutableStateOf(false) }
    var dashPressed by remember { mutableStateOf(false) }
    var fireSkillPressed by remember { mutableStateOf(false) }
    var lightningSkillPressed by remember { mutableStateOf(false) }
    var ultimateSkillPressed by remember { mutableStateOf(false) }

    val gameEngine = remember(stage.id) {
        GameEngine(
            stage = stage,
            upgrades = upgrades,
            onStageCleared = { reward ->
                coinsWon = reward
                isVictory = true
            },
            onGameOver = {
                isGameOver = true
            }
        )
    }

    // 60FPS Game Loop
    LaunchedEffect(isPaused, isGameOver, isVictory) {
        var lastTime = System.nanoTime()
        while (!isPaused && !isGameOver && !isVictory) {
            withFrameNanos { now ->
                val dt = ((now - lastTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastTime = now

                gameEngine.handleInput(
                    moveLeft = moveLeftPressed,
                    moveRight = moveRightPressed,
                    jumpTriggered = jumpPressed,
                    attackTriggered = attackPressed,
                    blockActive = blockPressed,
                    dashTriggered = dashPressed,
                    fireSkillTriggered = fireSkillPressed,
                    lightningSkillTriggered = lightningSkillPressed,
                    ultimateSkillTriggered = ultimateSkillPressed
                )

                // Reset instantaneous triggers
                jumpPressed = false
                attackPressed = false
                dashPressed = false
                fireSkillPressed = false
                lightningSkillPressed = false
                ultimateSkillPressed = false

                gameEngine.update(dt)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .testTag("gameplay_screen")
    ) {
        // Main Game Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("game_canvas")
        ) {
            val canvasW = size.width
            val canvasH = size.height
            val scale = (canvasH / 720f).coerceAtLeast(0.8f)

            val shakeX = if (gameEngine.screenShake > 0) (Math.random() * gameEngine.screenShake - gameEngine.screenShake / 2).toFloat() else 0f
            val shakeY = if (gameEngine.screenShake > 0) (Math.random() * gameEngine.screenShake - gameEngine.screenShake / 2).toFloat() else 0f

            val camX = gameEngine.cameraX - shakeX

            // 1. Draw Chessboard Atmospheric Background & Parallax Tiles
            drawChessboardBackground(camX, canvasW, canvasH, stage)

            // 2. Draw Floor (Chessboard ground line)
            val groundScreenY = gameEngine.groundY * scale + shakeY
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2E1A47), Color(0xFF140924)),
                    startY = groundScreenY,
                    endY = canvasH
                ),
                topLeft = Offset(0f, groundScreenY),
                size = Size(canvasW, canvasH - groundScreenY)
            )
            // Golden chessboard edge line
            drawLine(
                color = GoldPrimary.copy(alpha = 0.8f),
                start = Offset(0f, groundScreenY),
                end = Offset(canvasW, groundScreenY),
                strokeWidth = 4f
            )

            // 3. Draw Coins on ground
            gameEngine.coins.forEach { coin ->
                val cx = (coin.x - camX) * scale
                val cy = coin.y * scale + shakeY
                drawCircle(
                    color = GoldPrimary,
                    radius = 8f * scale,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f * scale,
                    center = Offset(cx - 2f, cy - 2f)
                )
            }

            // 4. Draw Projectiles
            gameEngine.projectiles.forEach { p ->
                drawProjectile(p, camX, scale, shakeY)
            }

            // 5. Draw Enemies
            gameEngine.enemies.forEach { enemy ->
                if (!enemy.isDead) {
                    drawEnemyPiece(enemy, camX, scale, shakeY)
                }
            }

            // 6. Draw Hero Pawn Player
            drawHeroPawn(gameEngine, camX, scale, shakeY)

            // 7. Draw Particles
            gameEngine.particles.forEach { pt ->
                val px = (pt.x - camX) * scale
                val py = pt.y * scale + shakeY
                drawCircle(
                    color = Color(pt.color.toULong()).copy(alpha = pt.alpha),
                    radius = pt.size * scale,
                    center = Offset(px, py)
                )
            }

            // 8. Draw Floating Combat Text
            gameEngine.combatTexts.forEach { ct ->
                val tx = (ct.x - camX) * scale
                val ty = ct.y * scale + shakeY
                val paint = android.graphics.Paint().apply {
                    color = ct.color.toInt()
                    textSize = (if (ct.isCrit) 32f else 22f) * scale
                    isFakeBoldText = true
                    setShadowLayer(6f, 0f, 0f, android.graphics.Color.BLACK)
                }
                drawContext.canvas.nativeCanvas.drawText(ct.text, tx, ty, paint)
            }
        }

        // Top HUD (Health Bar, Stamina, Coins, Combo, Pause button)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player Health & Stamina Bars
                Column(modifier = Modifier.width(180.dp)) {
                    // Health Bar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "❤️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        ) {
                            val hpRatio = (gameEngine.currentHealth / gameEngine.maxHealth).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(hpRatio)
                                    .height(12.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(HealthGreen, Color(0xFF69F0AE))
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Stamina Bar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⚡", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        ) {
                            val staminaRatio = (gameEngine.currentStamina / gameEngine.maxStamina).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(staminaRatio)
                                    .height(8.dp)
                                    .background(StaminaBlue)
                            )
                        }
                    }
                }

                // Coins Collected
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "💰", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${gameEngine.coinsCollectedThisStage}",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                // Stage progress & Pause Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) stage.titleAr.take(12) else stage.titleEn.take(12),
                        color = TextLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { isPaused = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                            .testTag("pause_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Boss Health Bar (appears when boss is active)
            gameEngine.bossEnemy?.let { boss ->
                if (!boss.isDead) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .border(1.5.dp, BossHealthRed.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${boss.symbol} ${if (isArabic) boss.nameAr else boss.nameEn}",
                                color = TextGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${boss.health.toInt()} / ${boss.maxHealth.toInt()} HP",
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val bossRatio = (boss.health / boss.maxHealth).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { bossRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = BossHealthRed,
                            trackColor = Color(0xFF3E1212)
                        )
                    }
                }
            }

            // Hit Combo Counter
            if (gameEngine.comboHits > 1) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(FireOrange.copy(alpha = 0.25f))
                        .border(1.dp, FireOrange, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🔥 ${gameEngine.comboHits} COMBO!",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // On-Screen Touch Controls (Virtual Joystick / Buttons)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Left & Right D-Pad Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Move Left Button
                    TouchControlButton(
                        symbol = "◀",
                        label = if (isArabic) "يسار" else "Left",
                        isPressed = moveLeftPressed,
                        onPressStateChange = { moveLeftPressed = it },
                        modifier = Modifier.size(62.dp).testTag("btn_left")
                    )

                    // Move Right Button
                    TouchControlButton(
                        symbol = "▶",
                        label = if (isArabic) "يمين" else "Right",
                        isPressed = moveRightPressed,
                        onPressStateChange = { moveRightPressed = it },
                        modifier = Modifier.size(62.dp).testTag("btn_right")
                    )
                }

                // Right Combat Action Buttons (Attack, Block, Jump, Dash, Skills)
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Skills Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Fire Skill
                        if (upgrades.fireSkillUnlocked) {
                            SkillActionButton(
                                iconSymbol = "🔥",
                                cooldown = gameEngine.fireCooldown,
                                maxCooldown = 6f,
                                activeColor = FireOrange,
                                onClick = { fireSkillPressed = true },
                                testTag = "btn_fire_skill"
                            )
                        }

                        // Lightning Skill
                        if (upgrades.lightningSkillUnlocked) {
                            SkillActionButton(
                                iconSymbol = "⚡",
                                cooldown = gameEngine.lightningCooldown,
                                maxCooldown = 10f,
                                activeColor = LightningCyan,
                                onClick = { lightningSkillPressed = true },
                                testTag = "btn_lightning_skill"
                            )
                        }

                        // Ultimate Skill
                        if (upgrades.ultimateSkillUnlocked) {
                            SkillActionButton(
                                iconSymbol = "👑",
                                cooldown = gameEngine.ultimateCooldown,
                                maxCooldown = 20f,
                                activeColor = GoldPrimary,
                                onClick = { ultimateSkillPressed = true },
                                testTag = "btn_ultimate_skill"
                            )
                        }
                    }

                    // Main Action Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dash Button
                        TouchControlButton(
                            symbol = "💨",
                            label = if (isArabic) "مراوغة" else "Dash",
                            isPressed = dashPressed,
                            onPressStateChange = { if (it) dashPressed = true },
                            modifier = Modifier.size(54.dp).testTag("btn_dash")
                        )

                        // Shield Block Button (Held)
                        TouchControlButton(
                            symbol = "🛡️",
                            label = if (isArabic) "صد" else "Shield",
                            isPressed = blockPressed,
                            onPressStateChange = { blockPressed = it },
                            modifier = Modifier.size(54.dp).testTag("btn_block"),
                            accentColor = ShieldCyan
                        )

                        // Jump Button
                        TouchControlButton(
                            symbol = "▲",
                            label = if (isArabic) "قفز" else "Jump",
                            isPressed = jumpPressed,
                            onPressStateChange = { if (it) jumpPressed = true },
                            modifier = Modifier.size(58.dp).testTag("btn_jump")
                        )

                        // Sword Attack Button (Primary big action button)
                        TouchControlButton(
                            symbol = "⚔️",
                            label = if (isArabic) "هجوم" else "Attack",
                            isPressed = attackPressed,
                            onPressStateChange = { if (it) attackPressed = true },
                            modifier = Modifier.size(72.dp).testTag("btn_attack"),
                            accentColor = GoldPrimary,
                            isPrimary = true
                        )
                    }
                }
            }
        }

        // Pause Menu Dialog
        if (isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ChessBoardDark),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isArabic) "اللعبة متوقفة" else "Game Paused",
                            color = GoldPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { isPaused = false },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text(text = if (isArabic) "متابعة اللعب" else "Resume", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onExitToStages,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text(text = if (isArabic) "العودة للمراحل" else "Stages Map")
                        }
                    }
                }
            }
        }

        // Game Over Overlay
        if (isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.88f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ChessBoardDark),
                    border = androidx.compose.foundation.BorderStroke(2.dp, BloodRed),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "♟️ 💀",
                            fontSize = 42.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isArabic) "سقط الجندي في المعركة" else "The Pawn Has Fallen",
                            color = BloodRed,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isArabic) "لا تستسلم! طوّر سيفك ودرعك في المتجر وحاول مجددًا." else "Do not surrender! Upgrade your gear in the shop and avenge your comrades.",
                            color = TextLight,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onExitToStages,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("game_over_retry")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = if (isArabic) "إعادة المحاولة / ترقية" else "Retry / Upgrade", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Victory Overlay
        if (isVictory) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.88f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ChessBoardDark),
                    border = androidx.compose.foundation.BorderStroke(2.dp, GoldPrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🏆 ⚔️ 👑",
                            fontSize = 38.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isArabic) "تم تحرير المرحلة بنجاح!" else "Stage Cleared!",
                            color = GoldPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${if (isArabic) "المكافأة المكتسبة:" else "Reward:"} 💰 $coinsWon",
                            color = TextGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { onStageCleared(coinsWon) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("stage_victory_continue")
                        ) {
                            Text(text = if (isArabic) "المتابعة والمرحلة التالية" else "Continue to Next Stage", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TouchControlButton(
    symbol: String,
    label: String,
    isPressed: Boolean,
    onPressStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = Color.White,
    isPrimary: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                if (isPressed) accentColor.copy(alpha = 0.5f)
                else if (isPrimary) GoldPrimary.copy(alpha = 0.35f)
                else Color.Black.copy(alpha = 0.6f)
            )
            .border(
                if (isPrimary) 2.5.dp else 1.5.dp,
                if (isPressed) Color.White else if (isPrimary) GoldPrimary else accentColor.copy(alpha = 0.5f),
                CircleShape
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPressStateChange(true)
                        tryAwaitRelease()
                        onPressStateChange(false)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = symbol,
                color = if (isPrimary) GoldPrimary else Color.White,
                fontSize = if (isPrimary) 24.sp else 18.sp,
                fontWeight = FontWeight.Bold
            )
            if (!isPrimary && label.isNotEmpty()) {
                Text(
                    text = label,
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SkillActionButton(
    iconSymbol: String,
    cooldown: Float,
    maxCooldown: Float,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    val isReady = cooldown <= 0f
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isReady) activeColor.copy(alpha = 0.25f) else Color.DarkGray.copy(alpha = 0.4f))
            .border(1.5.dp, if (isReady) activeColor else Color.Gray.copy(alpha = 0.4f), CircleShape)
            .clickable(enabled = isReady, onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (isReady) {
            Text(text = iconSymbol, fontSize = 20.sp)
        } else {
            Text(
                text = "${cooldown.toInt() + 1}s",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Canvas Drawing Helpers
private fun DrawScope.drawChessboardBackground(
    camX: Float,
    canvasW: Float,
    canvasH: Float,
    stage: StageDefinition
) {
    // Atmospheric gradient sky
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0D0817),
                Color(stage.themeColor.toULong()).copy(alpha = 0.3f),
                Color(0xFF1B1429)
            )
        ),
        size = Size(canvasW, canvasH)
    )

    // Parallax chess tile silhouettes
    val tileSize = 80f
    val startCol = ((camX * 0.3f) / tileSize).toInt()
    val endCol = startCol + (canvasW / tileSize).toInt() + 2

    for (col in startCol..endCol) {
        for (row in 0..7) {
            val isDark = (col + row) % 2 == 0
            val tileX = (col * tileSize) - (camX * 0.3f)
            val tileY = row * (canvasH / 8f)

            if (isDark) {
                drawRect(
                    color = Color(0xFF281E3D).copy(alpha = 0.18f),
                    topLeft = Offset(tileX, tileY),
                    size = Size(tileSize, canvasH / 8f)
                )
            }
        }
    }
}

private fun DrawScope.drawHeroPawn(
    engine: GameEngine,
    camX: Float,
    scale: Float,
    shakeY: Float
) {
    val px = (engine.playerX - camX) * scale
    val py = engine.playerY * scale + shakeY

    val pawnColor = if (engine.invulnerableTimer > 0f && (System.currentTimeMillis() / 80) % 2 == 0L) {
        Color.Transparent
    } else {
        GoldPrimary
    }

    if (pawnColor == Color.Transparent) return

    // Draw Pawn Base & Body
    val baseWidth = 36f * scale
    val baseHeight = 12f * scale
    drawRoundRect(
        color = GoldDark,
        topLeft = Offset(px - baseWidth / 2, py - baseHeight),
        size = Size(baseWidth, baseHeight),
        cornerRadius = CornerRadius(4f, 4f)
    )

    // Mid Stem
    val stemPath = Path().apply {
        moveTo(px - 14f * scale, py - baseHeight)
        lineTo(px - 8f * scale, py - 40f * scale)
        lineTo(px + 8f * scale, py - 40f * scale)
        lineTo(px + 14f * scale, py - baseHeight)
        close()
    }
    drawPath(stemPath, color = GoldPrimary)

    // Pawn Head Sphere
    drawCircle(
        color = GoldPrimary,
        radius = 16f * scale,
        center = Offset(px, py - 48f * scale)
    )
    // Head Highlight
    drawCircle(
        color = Color.White.copy(alpha = 0.6f),
        radius = 5f * scale,
        center = Offset(px - 4f * scale, py - 52f * scale)
    )

    // Glowing Eyes
    val eyeDir = if (engine.facingRight) 4f * scale else -4f * scale
    drawCircle(
        color = FireOrange,
        radius = 3f * scale,
        center = Offset(px + eyeDir, py - 48f * scale)
    )

    // Shield (if blocking)
    if (engine.isBlocking) {
        val shieldX = if (engine.facingRight) px + 18f * scale else px - 28f * scale
        drawRoundRect(
            color = ShieldCyan,
            topLeft = Offset(shieldX, py - 46f * scale),
            size = Size(12f * scale, 34f * scale),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Shield Glow
        drawRoundRect(
            color = Color.White.copy(alpha = 0.7f),
            topLeft = Offset(shieldX + 2f, py - 44f * scale),
            size = Size(4f * scale, 30f * scale),
            cornerRadius = CornerRadius(4f, 4f)
        )
    }

    // Sword (with slashing trail during attack)
    val swordDir = if (engine.facingRight) 1f else -1f
    if (engine.isAttacking) {
        // Slashing blade arc
        val arcPath = Path().apply {
            moveTo(px, py - 30f * scale)
            lineTo(px + swordDir * 60f * scale, py - 55f * scale)
            lineTo(px + swordDir * 65f * scale, py - 10f * scale)
            close()
        }
        drawPath(
            arcPath,
            brush = Brush.radialGradient(
                colors = listOf(Color.White, GoldPrimary, Color.Transparent),
                center = Offset(px + swordDir * 40f * scale, py - 30f * scale),
                radius = 50f * scale
            )
        )
    } else if (!engine.isBlocking) {
        // Idle held sword
        val swordBaseX = px + swordDir * 14f * scale
        val swordBaseY = py - 30f * scale
        drawLine(
            color = Color.LightGray,
            start = Offset(swordBaseX, swordBaseY),
            end = Offset(swordBaseX + swordDir * 24f * scale, swordBaseY - 24f * scale),
            strokeWidth = 4f * scale
        )
        // Golden Hilt
        drawCircle(
            color = GoldPrimary,
            radius = 4f * scale,
            center = Offset(swordBaseX, swordBaseY)
        )
    }
}

private fun DrawScope.drawEnemyPiece(
    enemy: Enemy,
    camX: Float,
    scale: Float,
    shakeY: Float
) {
    val ex = (enemy.x - camX) * scale
    val ey = enemy.y * scale + shakeY

    val isBoss = enemy.isBoss
    val pieceColor = if (isBoss) Color(0xFFE53935) else Color(0xFF37474F)

    // Base
    val bw = (if (isBoss) 54f else 32f) * scale
    val bh = (if (isBoss) 16f else 10f) * scale
    drawRoundRect(
        color = if (isBoss) Color(0xFF880E4F) else Color(0xFF263238),
        topLeft = Offset(ex - bw / 2, ey - bh),
        size = Size(bw, bh),
        cornerRadius = CornerRadius(4f, 4f)
    )

    // Piece Body Representation
    when (enemy.type) {
        EnemyType.BLACK_KNIGHT_BOSS, EnemyType.TWIN_KNIGHT_BOSS -> {
            // Knight Horse Head
            val kPath = Path().apply {
                moveTo(ex - 20f * scale, ey - bh)
                lineTo(ex - 15f * scale, ey - 65f * scale)
                lineTo(ex + 18f * scale, ey - 75f * scale)
                lineTo(ex + 25f * scale, ey - 50f * scale)
                lineTo(ex + 10f * scale, ey - bh)
                close()
            }
            drawPath(kPath, color = pieceColor)
            // Glowing Red Eye
            drawCircle(
                color = Color.Red,
                radius = 4f * scale,
                center = Offset(ex + (if (enemy.facingRight) 12f else -12f) * scale, ey - 62f * scale)
            )
        }

        EnemyType.IRON_ROOK_BOSS -> {
            // Massive Stone Rook Bastion
            drawRoundRect(
                color = pieceColor,
                topLeft = Offset(ex - 26f * scale, ey - 80f * scale),
                size = Size(52f * scale, 70f * scale),
                cornerRadius = CornerRadius(6f, 6f)
            )
            // Battlements teeth
            drawRect(color = Color(0xFF212121), topLeft = Offset(ex - 10f * scale, ey - 80f * scale), size = Size(20f * scale, 15f * scale))
        }

        EnemyType.GRAND_BISHOP_BOSS -> {
            // Bishop Mitre & Staff
            drawCircle(color = pieceColor, radius = 22f * scale, center = Offset(ex, ey - 60f * scale))
            drawLine(
                color = RoyalPurple,
                start = Offset(ex, ey - 90f * scale),
                end = Offset(ex, ey - 30f * scale),
                strokeWidth = 5f * scale
            )
        }

        EnemyType.KING_BOSS -> {
            // Final Boss King with Royal Golden Crown & Red Mantle
            drawCircle(color = Color(0xFFFFF9C4), radius = 26f * scale, center = Offset(ex, ey - 75f * scale))
            // Golden Royal Crown
            val crownPath = Path().apply {
                moveTo(ex - 24f * scale, ey - 85f * scale)
                lineTo(ex - 24f * scale, ey - 105f * scale)
                lineTo(ex - 12f * scale, ey - 95f * scale)
                lineTo(ex, ey - 110f * scale)
                lineTo(ex + 12f * scale, ey - 95f * scale)
                lineTo(ex + 24f * scale, ey - 105f * scale)
                lineTo(ex + 24f * scale, ey - 85f * scale)
                close()
            }
            drawPath(crownPath, color = GoldPrimary)
            // Royal Robe
            drawRoundRect(
                color = Color(0xFFC2185B),
                topLeft = Offset(ex - 24f * scale, ey - 70f * scale),
                size = Size(48f * scale, 60f * scale),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }

        else -> {
            // Standard Minion Pawn / Archer
            drawCircle(
                color = pieceColor,
                radius = 12f * scale,
                center = Offset(ex, ey - 36f * scale)
            )
            drawRoundRect(
                color = pieceColor,
                topLeft = Offset(ex - 10f * scale, ey - 28f * scale),
                size = Size(20f * scale, 20f * scale),
                cornerRadius = CornerRadius(3f, 3f)
            )
            // Glowing evil eye
            val eDir = if (enemy.facingRight) 3f * scale else -3f * scale
            drawCircle(color = Color.Red, radius = 2f * scale, center = Offset(ex + eDir, ey - 36f * scale))
        }
    }

    // Mini Health Bar for non-boss enemies
    if (!isBoss) {
        val hpW = 30f * scale
        val hpH = 4f * scale
        val hpRatio = (enemy.health / enemy.maxHealth).coerceIn(0f, 1f)
        drawRect(color = Color.Black, topLeft = Offset(ex - hpW / 2, ey - 55f * scale), size = Size(hpW, hpH))
        drawRect(color = BloodRed, topLeft = Offset(ex - hpW / 2, ey - 55f * scale), size = Size(hpW * hpRatio, hpH))
    }
}

private fun DrawScope.drawProjectile(
    p: Projectile,
    camX: Float,
    scale: Float,
    shakeY: Float
) {
    val px = (p.x - camX) * scale
    val py = p.y * scale + shakeY

    when (p.type) {
        ProjectileType.FIRE_WAVE -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, FireOrange, Color.Transparent),
                    center = Offset(px, py),
                    radius = 24f * scale
                ),
                radius = 24f * scale,
                center = Offset(px, py)
            )
        }
        ProjectileType.ENEMY_ARROW -> {
            drawLine(
                color = Color(0xFFFFD54F),
                start = Offset(px - 14f * scale, py),
                end = Offset(px + 14f * scale, py),
                strokeWidth = 3f * scale
            )
        }
        ProjectileType.BISHOP_LASER_BEAM -> {
            drawCircle(
                color = RoyalPurple,
                radius = 16f * scale,
                center = Offset(px, py)
            )
            drawCircle(
                color = Color.White,
                radius = 7f * scale,
                center = Offset(px, py)
            )
        }
        ProjectileType.KING_GOLDEN_SWORD_BEAM -> {
            drawRoundRect(
                color = GoldPrimary,
                topLeft = Offset(px - 25f * scale, py - 10f * scale),
                size = Size(50f * scale, 20f * scale),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }
        ProjectileType.ROOK_SHOCKWAVE -> {
            drawCircle(
                color = Color(0xFF78909C),
                radius = 22f * scale,
                center = Offset(px, py)
            )
        }
        else -> {
            drawCircle(color = Color.Cyan, radius = 10f * scale, center = Offset(px, py))
        }
    }
}
