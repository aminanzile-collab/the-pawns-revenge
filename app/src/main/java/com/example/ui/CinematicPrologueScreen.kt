package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.GameSoundEngine
import com.example.ui.theme.FireOrange
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.delay

@Composable
fun CinematicPrologueScreen(
    isArabic: Boolean,
    onStartGame: () -> Unit
) {
    var sceneIndex by remember { mutableIntStateOf(0) }
    val fadeInAlpha = remember { Animatable(0f) }

    // Floating embers animation
    val infiniteTransition = rememberInfiniteTransition(label = "embers")
    val emberGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    LaunchedEffect(sceneIndex) {
        fadeInAlpha.snapTo(0f)
        fadeInAlpha.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))

        when (sceneIndex) {
            0 -> {
                GameSoundEngine.playDramaticThunder()
                delay(3200)
                sceneIndex = 1
            }
            1 -> {
                GameSoundEngine.playHit()
                delay(4000)
                sceneIndex = 2
            }
            2 -> {
                GameSoundEngine.playDramaticThunder()
                delay(4500)
                sceneIndex = 3
            }
            3 -> {
                delay(4500)
                sceneIndex = 4
            }
            4 -> {
                GameSoundEngine.playBossDefeated()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .testTag("cinematic_prologue_screen")
    ) {
        // Background visuals depending on scene
        when (sceneIndex) {
            1 -> {
                Image(
                    painter = painterResource(id = R.drawable.img_cinematic_betrayal),
                    contentDescription = "Betrayal Scene",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(fadeInAlpha.value * 0.7f),
                    contentScale = ContentScale.Crop
                )
            }
            2 -> {
                Image(
                    painter = painterResource(id = R.drawable.img_pawn_hero),
                    contentDescription = "Pawn Hero Scene",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(fadeInAlpha.value * 0.8f),
                    contentScale = ContentScale.Crop
                )
            }
            3, 4 -> {
                Image(
                    painter = painterResource(id = R.drawable.img_castle_rooks),
                    contentDescription = "Castle Scene",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(fadeInAlpha.value * 0.5f),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Vignette & gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ObsidianDark.copy(alpha = 0.85f),
                            Color.Transparent,
                            ObsidianDark.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // Top bar with Skip button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🎬 ${if (isArabic) "مشهد البداية" else "Prologue"}",
                    color = GoldPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onStartGame,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextGold
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(listOf(GoldPrimary, FireOrange))
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.testTag("skip_cinematic_button")
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Skip",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isArabic) "تخطي القصة" else "Skip",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Main Cinematic Dialogue Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (sceneIndex) {
                0 -> {
                    // Scene 0: Darkness & sounds
                    Text(
                        text = "⚔️ ♟️ 🛡️",
                        fontSize = 36.sp,
                        modifier = Modifier.alpha(emberGlow)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isArabic) "في ليلة مظلمة داخل مملكة الشطرنج...\nتتعالى صرخات المعركة وأصوات السيوف."
                        else "On a dark night across the Chessboard Kingdom...\nThe cries of battle and clash of steel echo.",
                        color = TextLight,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 28.sp,
                        modifier = Modifier.alpha(fadeInAlpha.value)
                    )
                }

                1 -> {
                    // Scene 1: Betrayal
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.75f)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "👑 ${if (isArabic) "الملك الأبيض:" else "The White King:"}",
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isArabic) "\"ضحّوا بالجنود في المقدمة فورًا! احموا عرشي وقصري بأي ثمن!\""
                                else "\"Sacrifice the front pawns at once! Protect my throne and citadel at all costs!\"",
                                color = Color.White,
                                fontSize = 17.sp,
                                lineHeight = 26.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                2 -> {
                    // Scene 2: Pawn Left Alone
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "♟️ ${if (isArabic) "الجندي الناجي:" else "The Lone Pawn:"}",
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isArabic) "\"ضحّى بنا الملك من أجل أن يبقى على عرشه... سقط جميع رفاقي...\""
                                else "\"The King sacrificed us just so he could remain upon his throne... all my comrades have fallen...\"",
                                color = TextGold,
                                fontSize = 17.sp,
                                lineHeight = 26.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }

                3 -> {
                    // Scene 3: The Vow of Revenge
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.85f)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, FireOrange),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "⚔️ ${if (isArabic) "قسم الانتقام:" else "The Vow:"}",
                                color = FireOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isArabic) "\"سأخترق رقعة الشطرنج كلها... وأصل إلى قاعة عرشك يا ملك... مهما كان الثمن!\""
                                else "\"I will carve my way across this entire chessboard... and reach your throne room, King... whatever the cost!\"",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 28.sp
                            )
                        }
                    }
                }

                4 -> {
                    // Scene 4: Title Card & Start Action
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "♟️",
                            fontSize = 48.sp,
                            modifier = Modifier.scale(emberGlow)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isArabic) "انتقام الجندي" else "Revenge of the Pawn",
                            color = GoldPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (isArabic) "THE PAWN'S REVENGE" else "معركة الشطرنج الملحمية",
                            color = TextMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 3.sp
                        )

                        Spacer(modifier = Modifier.height(26.dp))

                        Button(
                            onClick = onStartGame,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = ObsidianDark
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(56.dp)
                                .testTag("start_game_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "ابدأ المعركة والانتقام" else "Start the Revenge",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
