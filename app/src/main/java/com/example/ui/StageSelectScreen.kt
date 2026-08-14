package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import com.example.model.GAME_STAGES
import com.example.model.PlayerUpgrades
import com.example.model.StageDefinition
import com.example.ui.theme.ChessBoardDark
import com.example.ui.theme.ChessBoardTile
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@Composable
fun StageSelectScreen(
    upgrades: PlayerUpgrades,
    isArabic: Boolean,
    onSelectStage: (StageDefinition) -> Unit,
    onOpenShop: () -> Unit,
    onPlayCinematic: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .testTag("stage_select_screen")
    ) {
        // Background Artwork
        Image(
            painter = painterResource(id = R.drawable.img_castle_rooks),
            contentDescription = "Chessboard background",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.25f),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.5f), CircleShape)
                        .testTag("stage_select_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }

                Text(
                    text = if (isArabic) "خريطة رقعة الشطرنج" else "Chess Realm Map",
                    color = GoldPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Gold Coins Counter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .border(1.dp, GoldPrimary, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "💰", fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${upgrades.coins}",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Nav Bar (Upgrades & Cinematic)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenShop,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("stage_select_shop_button")
                ) {
                    Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Shop", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isArabic) "متجر التطوير" else "Armory Shop", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onPlayCinematic,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldDark, contentColor = TextGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("stage_select_cinematic_button")
                ) {
                    Icon(imageVector = Icons.Default.Videocam, contentDescription = "Prologue", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isArabic) "مشاهدة القصة" else "Story Movie", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stages List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(GAME_STAGES) { stage ->
                    val isUnlocked = stage.id <= upgrades.unlockedStage
                    val isCompleted = upgrades.completedStages.contains(stage.id)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isUnlocked) { onSelectStage(stage) }
                            .testTag("stage_card_${stage.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) ChessBoardDark else Color.Black.copy(alpha = 0.5f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isCompleted) GoldPrimary
                            else if (isUnlocked) Color(stage.themeColor.toULong())
                            else Color.DarkGray
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Boss Symbol & Stage Number
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isUnlocked) Color(stage.themeColor.toULong()).copy(alpha = 0.25f)
                                        else Color.DarkGray.copy(alpha = 0.3f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isUnlocked) Color(stage.themeColor.toULong()) else Color.Gray,
                                        RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isUnlocked) {
                                    Text(text = stage.bossSymbol, fontSize = 26.sp)
                                } else {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = Color.Gray)
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Stage Details
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isArabic) stage.titleAr else stage.titleEn,
                                        color = if (isUnlocked) TextGold else Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    if (isCompleted) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Completed",
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (isArabic) stage.descriptionAr else stage.descriptionEn,
                                    color = if (isUnlocked) TextMuted else Color.DarkGray,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "⚔️ ${if (isArabic) "الزعيم:" else "Boss:"} ${if (isArabic) stage.bossNameAr else stage.bossNameEn}",
                                        color = if (isUnlocked) Color(0xFFFF8A80) else Color.Gray,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "💰 +${stage.rewardCoins}",
                                        color = if (isUnlocked) GoldPrimary else Color.Gray,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Action Icon
                            if (isUnlocked) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(GoldPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = ObsidianDark,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
