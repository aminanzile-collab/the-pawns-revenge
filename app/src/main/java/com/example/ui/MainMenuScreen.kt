package com.example.ui

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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.model.PlayerUpgrades
import com.example.ui.theme.ChessBoardDark
import com.example.ui.theme.FireOrange
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@Composable
fun MainMenuScreen(
    upgrades: PlayerUpgrades,
    isArabic: Boolean,
    onStartStory: () -> Unit,
    onOpenStages: () -> Unit,
    onOpenShop: () -> Unit,
    onPlayCinematic: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    var soundOn by remember { mutableStateOf(GameSoundEngine.isSoundEnabled) }
    var musicOn by remember { mutableStateOf(GameSoundEngine.isMusicEnabled) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .testTag("main_menu_screen")
    ) {
        // Hero Background Artwork
        Image(
            painter = painterResource(id = R.drawable.img_pawn_hero),
            contentDescription = "Pawn Hero Background",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.55f),
            contentScale = ContentScale.Crop
        )

        // Gradient Vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ObsidianDark.copy(alpha = 0.7f),
                            ObsidianDark.copy(alpha = 0.4f),
                            ObsidianDark.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Settings Bar (Coins, Sound, Language)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gold Coins
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .border(1.dp, GoldPrimary, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "💰", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${upgrades.coins}",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Controls: SFX, Music, Language
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            soundOn = !soundOn
                            GameSoundEngine.isSoundEnabled = soundOn
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .testTag("toggle_sound_button")
                    ) {
                        Icon(
                            imageVector = if (soundOn) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Sound",
                            tint = if (soundOn) GoldPrimary else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            musicOn = !musicOn
                            GameSoundEngine.isMusicEnabled = musicOn
                            if (!musicOn) GameSoundEngine.stopBattleMusic()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .testTag("toggle_music_button")
                    ) {
                        Icon(
                            imageVector = if (musicOn) Icons.Default.MusicNote else Icons.Default.MusicOff,
                            contentDescription = "Music",
                            tint = if (musicOn) GoldPrimary else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = onToggleLanguage,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGold),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(GoldPrimary, RoyalPurple))
                        ),
                        modifier = Modifier.testTag("toggle_language_button")
                    ) {
                        Icon(imageVector = Icons.Default.Language, contentDescription = "Lang", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isArabic) "EN" else "عربي", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Center Title & Icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text(
                    text = "♟️",
                    fontSize = 58.sp,
                    modifier = Modifier.scale(pulse)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isArabic) "انتقام الجندي" else "Revenge of the Pawn",
                    color = GoldPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )

                Text(
                    text = if (isArabic) "THE PAWN'S REVENGE" else "معركة الشطرنج الملحمية",
                    color = TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(RoyalPurple.copy(alpha = 0.25f))
                        .border(1.dp, RoyalPurple.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⚔️ ${if (isArabic) "المرحلة المفتوحة:" else "Current Stage:"} ${upgrades.unlockedStage} / 6",
                        color = TextLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Bottom Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Primary Play Button
                Button(
                    onClick = onStartStory,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = ObsidianDark
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("menu_play_button")
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "ابدأ المعركة والانتقام" else "Play Battle",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Stages Map
                    Button(
                        onClick = onOpenStages,
                        colors = ButtonDefaults.buttonColors(containerColor = ChessBoardDark, contentColor = TextGold),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldDark),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("menu_stages_button")
                    ) {
                        Icon(imageVector = Icons.Default.Map, contentDescription = "Stages", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isArabic) "المراحل" else "Stages", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    // Upgrades Shop
                    Button(
                        onClick = onOpenShop,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple, contentColor = Color.White),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("menu_shop_button")
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Shop", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isArabic) "الحدادة والتطوير" else "Armory", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Prologue Movie Button
                OutlinedButton(
                    onClick = onPlayCinematic,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGold),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(GoldPrimary, FireOrange))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("menu_cinematic_button")
                ) {
                    Icon(imageVector = Icons.Default.Videocam, contentDescription = "Movie", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (isArabic) "مشاهدة قصة الخيانة (Prologue)" else "Watch Story Prologue", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
