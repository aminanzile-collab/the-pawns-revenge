package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.GameSoundEngine
import com.example.model.PlayerUpgrades
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
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@Composable
fun UpgradeShopScreen(
    upgrades: PlayerUpgrades,
    isArabic: Boolean,
    onSaveUpgrades: (PlayerUpgrades) -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .testTag("upgrade_shop_screen")
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_pawn_hero),
            contentDescription = "Armory Hero",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.18f),
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
                        .testTag("shop_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }

                Text(
                    text = if (isArabic) "حدادة وترقية الجندي" else "Pawn's Armory & Forge",
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

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                // 1. Sword Upgrade
                item {
                    val cost = upgrades.swordLevel * 120
                    val isMax = upgrades.swordLevel >= 5
                    val canAfford = upgrades.coins >= cost && !isMax

                    UpgradeItemCard(
                        title = if (isArabic) "⚔️ حدة السيف (Sword Attack)" else "⚔️ Sword Sharpness",
                        description = if (isArabic) "يزيد الضرر الأساسي والضربات الحرجة (${25 + (upgrades.swordLevel - 1) * 25} dmg)" else "Increases base attack and critical hit damage",
                        level = upgrades.swordLevel,
                        maxLevel = 5,
                        cost = cost,
                        isMax = isMax,
                        canAfford = canAfford,
                        accentColor = GoldPrimary,
                        isArabic = isArabic,
                        onUpgrade = {
                            if (canAfford) {
                                GameSoundEngine.playSlash()
                                onSaveUpgrades(
                                    upgrades.copy(
                                        swordLevel = upgrades.swordLevel + 1,
                                        coins = upgrades.coins - cost
                                    )
                                )
                            }
                        },
                        testTag = "upgrade_sword"
                    )
                }

                // 2. Shield Upgrade
                item {
                    val cost = upgrades.shieldLevel * 100
                    val isMax = upgrades.shieldLevel >= 5
                    val canAfford = upgrades.coins >= cost && !isMax

                    UpgradeItemCard(
                        title = if (isArabic) "🛡️ متانة الدرع (Shield Defense)" else "🛡️ Shield Reinforcement",
                        description = if (isArabic) "يقلل الضرر المتلقى أثناء الصد (${50 + upgrades.shieldLevel * 10}%)" else "Increases damage reduction when holding block",
                        level = upgrades.shieldLevel,
                        maxLevel = 5,
                        cost = cost,
                        isMax = isMax,
                        canAfford = canAfford,
                        accentColor = ShieldCyan,
                        isArabic = isArabic,
                        onUpgrade = {
                            if (canAfford) {
                                GameSoundEngine.playShieldBlock()
                                onSaveUpgrades(
                                    upgrades.copy(
                                        shieldLevel = upgrades.shieldLevel + 1,
                                        coins = upgrades.coins - cost
                                    )
                                )
                            }
                        },
                        testTag = "upgrade_shield"
                    )
                }

                // 3. Health Upgrade
                item {
                    val cost = upgrades.healthLevel * 110
                    val isMax = upgrades.healthLevel >= 5
                    val canAfford = upgrades.coins >= cost && !isMax

                    UpgradeItemCard(
                        title = if (isArabic) "❤️ حيوية الجندي (Max Health)" else "❤️ Vitality & Health",
                        description = if (isArabic) "يزيد الحد الأقصى للصحة حتى 350 نقطة" else "Increases maximum hit points to withstand heavy boss hits",
                        level = upgrades.healthLevel,
                        maxLevel = 5,
                        cost = cost,
                        isMax = isMax,
                        canAfford = canAfford,
                        accentColor = HealthGreen,
                        isArabic = isArabic,
                        onUpgrade = {
                            if (canAfford) {
                                GameSoundEngine.playCoin()
                                onSaveUpgrades(
                                    upgrades.copy(
                                        healthLevel = upgrades.healthLevel + 1,
                                        coins = upgrades.coins - cost
                                    )
                                )
                            }
                        },
                        testTag = "upgrade_health"
                    )
                }

                // 4. Speed & Agility Upgrade
                item {
                    val cost = upgrades.speedLevel * 90
                    val isMax = upgrades.speedLevel >= 5
                    val canAfford = upgrades.coins >= cost && !isMax

                    UpgradeItemCard(
                        title = if (isArabic) "🏃 سرعة الحركة والمراوغة" else "🏃 Speed & Dash Agility",
                        description = if (isArabic) "حركة أسرع ومسافة مراوغة أبعد في ساحة المعركة" else "Faster movement and extended dash distance",
                        level = upgrades.speedLevel,
                        maxLevel = 5,
                        cost = cost,
                        isMax = isMax,
                        canAfford = canAfford,
                        accentColor = FireOrange,
                        isArabic = isArabic,
                        onUpgrade = {
                            if (canAfford) {
                                GameSoundEngine.playDash()
                                onSaveUpgrades(
                                    upgrades.copy(
                                        speedLevel = upgrades.speedLevel + 1,
                                        coins = upgrades.coins - cost
                                    )
                                )
                            }
                        },
                        testTag = "upgrade_speed"
                    )
                }

                // 5. Fire Wave Skill Unlock
                item {
                    val cost = 250
                    val isUnlocked = upgrades.fireSkillUnlocked
                    val canAfford = upgrades.coins >= cost && !isUnlocked

                    SkillUnlockCard(
                        icon = "🔥",
                        title = if (isArabic) "مهارة: الضربة النارية (Fire Slash)" else "Skill: Fire Wave Slash",
                        description = if (isArabic) "إطلاق موجة نار حارقة تخترق خطوط الأعداء من مسافة بعيدة." else "Launches a piercing flaming blade wave through enemy ranks.",
                        cost = cost,
                        isUnlocked = isUnlocked,
                        canAfford = canAfford,
                        accentColor = FireOrange,
                        isArabic = isArabic,
                        onUnlock = {
                            if (canAfford) {
                                GameSoundEngine.playFireSkill()
                                onSaveUpgrades(
                                    upgrades.copy(
                                        fireSkillUnlocked = true,
                                        coins = upgrades.coins - cost
                                    )
                                )
                            }
                        },
                        testTag = "unlock_fire_skill"
                    )
                }

                // 6. Lightning Strike Skill Unlock
                item {
                    val cost = 450
                    val isUnlocked = upgrades.lightningSkillUnlocked
                    val canAfford = upgrades.coins >= cost && !isUnlocked

                    SkillUnlockCard(
                        icon = "⚡",
                        title = if (isArabic) "مهارة: ضربة البرق (Thunder Wrath)" else "Skill: Lightning Strike",
                        description = if (isArabic) "استدعاء صواعق برق تضرب جميع الأعداء في الشاشة بصعقات قاضية." else "Calls down thunderbolts to strike all visible enemies on screen.",
                        cost = cost,
                        isUnlocked = isUnlocked,
                        canAfford = canAfford,
                        accentColor = LightningCyan,
                        isArabic = isArabic,
                        onUnlock = {
                            if (canAfford) {
                                GameSoundEngine.playLightningSkill()
                                onSaveUpgrades(
                                    upgrades.copy(
                                        lightningSkillUnlocked = true,
                                        coins = upgrades.coins - cost
                                    )
                                )
                            }
                        },
                        testTag = "unlock_lightning_skill"
                    )
                }

                // 7. Ultimate Skill: Pawn Awakening
                item {
                    val cost = 800
                    val isUnlocked = upgrades.ultimateSkillUnlocked
                    val canAfford = upgrades.coins >= cost && !isUnlocked

                    SkillUnlockCard(
                        icon = "👑",
                        title = if (isArabic) "الضربة القاضية: غضب أرواح الجنود" else "Ultimate: Pawn's Awakening",
                        description = if (isArabic) "إطلاق عاصفة من أرواح الجنود الراحلين تسحق أقوى الزعماء فورًا." else "Unleashes a barrage of fallen pawn spirits to crush any boss.",
                        cost = cost,
                        isUnlocked = isUnlocked,
                        canAfford = canAfford,
                        accentColor = GoldPrimary,
                        isArabic = isArabic,
                        onUnlock = {
                            if (canAfford) {
                                GameSoundEngine.playDramaticThunder()
                                onSaveUpgrades(
                                    upgrades.copy(
                                        ultimateSkillUnlocked = true,
                                        coins = upgrades.coins - cost
                                    )
                                )
                            }
                        },
                        testTag = "unlock_ultimate_skill"
                    )
                }
            }
        }
    }
}

@Composable
fun UpgradeItemCard(
    title: String,
    description: String,
    level: Int,
    maxLevel: Int,
    cost: Int,
    isMax: Boolean,
    canAfford: Boolean,
    accentColor: Color,
    isArabic: Boolean,
    onUpgrade: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = ChessBoardDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = "${if (isArabic) "مستوى" else "LV"} $level / $maxLevel",
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, color = TextMuted, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { level.toFloat() / maxLevel },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = Color.DarkGray.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onUpgrade,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = ObsidianDark,
                    disabledContainerColor = Color.DarkGray.copy(alpha = 0.4f),
                    disabledContentColor = Color.Gray
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                if (isMax) {
                    Text(text = if (isArabic) "المستوى الأقصى ✔" else "MAX LEVEL ✔", fontWeight = FontWeight.Bold)
                } else {
                    Text(
                        text = "${if (isArabic) "ترقية مقابل" else "Upgrade for"} 💰 $cost",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SkillUnlockCard(
    icon: String,
    title: String,
    description: String,
    cost: Int,
    isUnlocked: Boolean,
    canAfford: Boolean,
    accentColor: Color,
    isArabic: Boolean,
    onUnlock: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = ChessBoardDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isUnlocked) HealthGreen else accentColor.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.2f))
                    .border(1.dp, accentColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = TextGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isUnlocked) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(HealthGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Unlocked", tint = HealthGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isArabic) "مفعل" else "Active", color = HealthGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onUnlock,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = ObsidianDark,
                        disabledContainerColor = Color.DarkGray.copy(alpha = 0.4f),
                        disabledContentColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = "💰 $cost", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
