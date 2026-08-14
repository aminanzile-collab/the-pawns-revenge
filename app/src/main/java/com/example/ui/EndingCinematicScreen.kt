package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.GameSoundEngine
import com.example.ui.theme.BloodRed
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
fun EndingCinematicScreen(
    isArabic: Boolean,
    onReturnHome: () -> Unit
) {
    var endingStep by remember { mutableIntStateOf(0) }
    val fadeAnim = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "endingGlow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(endingStep) {
        fadeAnim.snapTo(0f)
        fadeAnim.animateTo(1f, animationSpec = tween(800))

        when (endingStep) {
            0 -> {
                GameSoundEngine.playDramaticThunder()
                delay(4000)
                endingStep = 1
            }
            1 -> {
                delay(4500)
                endingStep = 2
            }
            2 -> {
                GameSoundEngine.playDramaticThunder()
                delay(4500)
                endingStep = 3
            }
            3 -> {
                GameSoundEngine.playBossDefeated()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .testTag("ending_cinematic_screen")
    ) {
        // Dynamic artwork
        Image(
            painter = painterResource(id = R.drawable.img_castle_rooks),
            contentDescription = "Ending Realm",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.35f),
            contentScale = ContentScale.Crop
        )

        // Dark Vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, ObsidianDark.copy(alpha = 0.95f)),
                        radius = 1200f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (endingStep) {
                0 -> {
                    // Step 0: King Falls
                    Text(text = "👑 💥 ♟️", fontSize = 42.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, BloodRed),
                        modifier = Modifier.fillMaxWidth().alpha(fadeAnim.value)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "👑 ${if (isArabic) "سقوط الملك الخائن:" else "The King Falls:"}",
                                color = BloodRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isArabic) "\"كيف... كيف لجندي حقير... أن يسقط تاجي...؟!\""
                                else "\"How... how could a mere pawn... bring down my crown...?!\"",
                                color = TextGold,
                                fontSize = 17.sp,
                                textAlign = TextAlign.Center,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isArabic) "♟️ الجندي: \"لقد نسيت أن أي جندي يصل لنهاية الرقعة... يمتلك قوة إسقاط الملوك!\""
                                else "♟️ The Pawn: \"You forgot that any pawn reaching the end of the board holds the power to topple kings!\"",
                                color = Color.White,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                1 -> {
                    // Step 1: The Board Transforms
                    Text(text = "⚡ 🌌 ♟️", fontSize = 42.sp, modifier = Modifier.scale(pulseScale))
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.85f)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, RoyalPurple),
                        modifier = Modifier.fillMaxWidth().alpha(fadeAnim.value)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isArabic) "زلزال رقعة الشطرنج!" else "The Chessboard Trembles!",
                                color = RoyalPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isArabic) "بعد سقوط الملك، تهتز الأرض وتتحول خطوط الرقعة بأكملها...\nتظهر بوابات ضخمة تتصاعد منها أضواء مجهولة!"
                                else "Upon the King's defeat, the entire board shakes violently...\nColossal ancient rifts open, glowing with mysterious power!",
                                color = TextLight,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                2 -> {
                    // Step 2: The New King Descends
                    Text(text = "👑 ✨ ⚔️", fontSize = 46.sp, modifier = Modifier.scale(pulseScale))
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.85f)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, GoldPrimary),
                        modifier = Modifier.fillMaxWidth().alpha(fadeAnim.value)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isArabic) "ظهور ملك جديد..." else "A New King Emerges...",
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isArabic) "\"انتهت حرب الملك... ولكن بدأت حرب أخرى.\""
                                else "\"The King's war has ended... but another war has begun.\"",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                3 -> {
                    // Step 3: Sequel Teaser & Victory Screen
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().alpha(fadeAnim.value)
                    ) {
                        Text(text = "🏆", fontSize = 54.sp, modifier = Modifier.scale(pulseScale))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isArabic) "انتقام الجندي — اكتملت القصة" else "The Pawn's Revenge — Completed",
                            color = GoldPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isArabic) "يتبع في الجزء الثاني... 🔥" else "To be continued in Part 2... 🔥",
                            color = FireOrange,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Button(
                            onClick = onReturnHome,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = ObsidianDark
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(54.dp)
                                .testTag("ending_return_home_button")
                        ) {
                            Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "العودة للقائمة الرئيسية" else "Return to Main Menu",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
