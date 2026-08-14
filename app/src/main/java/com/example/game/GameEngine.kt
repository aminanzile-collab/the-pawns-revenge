package com.example.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.audio.GameSoundEngine
import com.example.model.CoinDrop
import com.example.model.Enemy
import com.example.model.EnemyType
import com.example.model.FloatingCombatText
import com.example.model.GAME_STAGES
import com.example.model.Particle
import com.example.model.PlayerUpgrades
import com.example.model.Projectile
import com.example.model.ProjectileType
import com.example.model.StageDefinition
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class GameEngine(
    val stage: StageDefinition,
    val upgrades: PlayerUpgrades,
    val onStageCleared: (coinsEarned: Int) -> Unit,
    val onGameOver: () -> Unit
) {
    // Stage bounds & ground
    val groundY = 600f
    val stageWidth = stage.stageWidth

    // Camera
    var cameraX by mutableFloatStateOf(0f)
    var screenShake by mutableFloatStateOf(0f)

    // Player State
    var playerX by mutableFloatStateOf(100f)
    var playerY by mutableFloatStateOf(groundY)
    var playerVx by mutableFloatStateOf(0f)
    var playerVy by mutableFloatStateOf(0f)
    var isGrounded by mutableStateOf(true)
    var jumpsRemaining by mutableIntStateOf(2)
    var facingRight by mutableStateOf(true)

    // Health & Stamina
    val maxHealth = when (upgrades.healthLevel) {
        1 -> 100f
        2 -> 150f
        3 -> 200f
        4 -> 260f
        else -> 350f
    }
    var currentHealth by mutableFloatStateOf(maxHealth)

    val maxStamina = 100f
    var currentStamina by mutableFloatStateOf(maxStamina)

    // Player Combat State
    var isAttacking by mutableStateOf(false)
    var attackTimer by mutableFloatStateOf(0f)
    var attackCombo by mutableIntStateOf(0)
    var isBlocking by mutableStateOf(false)
    var isDashing by mutableStateOf(false)
    var dashTimer by mutableFloatStateOf(0f)
    var invulnerableTimer by mutableFloatStateOf(0f)

    // Skill Cooldowns
    var fireCooldown by mutableFloatStateOf(0f)
    var lightningCooldown by mutableFloatStateOf(0f)
    var ultimateCooldown by mutableFloatStateOf(0f)

    // Boss & Wave Info
    var bossSpawned by mutableStateOf(false)
    var bossEnemy: Enemy? by mutableStateOf(null)
    var stageCompleted by mutableStateOf(false)
    var coinsCollectedThisStage by mutableIntStateOf(0)
    var comboHits by mutableIntStateOf(0)
    var comboTimer by mutableFloatStateOf(0f)

    // Entities Lists
    val enemies = mutableStateListOf<Enemy>()
    val projectiles = mutableStateListOf<Projectile>()
    val particles = mutableStateListOf<Particle>()
    val combatTexts = mutableStateListOf<FloatingCombatText>()
    val coins = mutableStateListOf<CoinDrop>()

    private var nextEntityId = 1L
    private var timeElapsed = 0f

    // Base Player Stats calculated from upgrades
    val baseDamage = when (upgrades.swordLevel) {
        1 -> 25f
        2 -> 40f
        3 -> 60f
        4 -> 85f
        else -> 120f
    }
    val damageBlockMultiplier = when (upgrades.shieldLevel) {
        1 -> 0.45f
        2 -> 0.30f
        3 -> 0.20f
        4 -> 0.12f
        else -> 0.05f
    }
    val moveSpeedMultiplier = 1f + (upgrades.speedLevel - 1) * 0.12f

    init {
        spawnInitialStageEnemies()
        GameSoundEngine.startBattleMusic()
    }

    private fun spawnInitialStageEnemies() {
        val count = when (stage.id) {
            1 -> 6
            2 -> 8
            3 -> 10
            4 -> 12
            5 -> 14
            else -> 16
        }

        for (i in 0 until count) {
            val ex = 600f + i * (stageWidth - 1000f) / count + Random.nextFloat() * 100f
            val type = when (stage.id) {
                1 -> if (i % 3 == 0) EnemyType.SHADOW_GUARD else EnemyType.BLACK_PAWN
                2 -> if (i % 2 == 0) EnemyType.ARCHER_PAWN else EnemyType.BLACK_PAWN
                3 -> if (i % 3 == 0) EnemyType.SHADOW_GUARD else EnemyType.BLACK_PAWN
                4 -> if (i % 2 == 0) EnemyType.ARCHER_PAWN else EnemyType.SHADOW_GUARD
                5 -> if (i % 3 == 0) EnemyType.ARCHER_PAWN else EnemyType.SHADOW_GUARD
                else -> if (i % 2 == 0) EnemyType.SHADOW_GUARD else EnemyType.BLACK_PAWN
            }

            val hp = when (type) {
                EnemyType.BLACK_PAWN -> 50f + stage.id * 15f
                EnemyType.ARCHER_PAWN -> 40f + stage.id * 12f
                EnemyType.SHADOW_GUARD -> 80f + stage.id * 20f
                else -> 60f
            }

            val (nameAr, nameEn, sym) = when (type) {
                EnemyType.BLACK_PAWN -> Triple("جندي أسود", "Black Pawn", "♟️")
                EnemyType.ARCHER_PAWN -> Triple("رامي السهام", "Pawn Archer", "🏹")
                EnemyType.SHADOW_GUARD -> Triple("حارس الظل", "Shadow Guard", "🛡️")
                else -> Triple("عدو", "Enemy", "♟️")
            }

            enemies.add(
                Enemy(
                    id = nextEntityId++,
                    type = type,
                    x = ex,
                    y = groundY,
                    health = hp,
                    maxHealth = hp,
                    nameAr = nameAr,
                    nameEn = nameEn,
                    symbol = sym
                )
            )
        }
    }

    private fun spawnBoss() {
        bossSpawned = true
        val bossX = stageWidth - 450f

        val boss = when (stage.id) {
            1 -> Enemy(
                id = nextEntityId++,
                type = EnemyType.BLACK_PAWN,
                x = bossX,
                y = groundY,
                health = 350f,
                maxHealth = 350f,
                isBoss = true,
                nameAr = "قائد الجنود السود",
                nameEn = "Black Pawn Captain",
                symbol = "♟️"
            )
            2 -> Enemy(
                id = nextEntityId++,
                type = EnemyType.BLACK_KNIGHT_BOSS,
                x = bossX,
                y = groundY,
                health = 650f,
                maxHealth = 650f,
                isBoss = true,
                nameAr = "الفارس الأسود الملكي",
                nameEn = "The Royal Black Knight",
                symbol = "♞"
            )
            3 -> Enemy(
                id = nextEntityId++,
                type = EnemyType.IRON_ROOK_BOSS,
                x = bossX,
                y = groundY,
                health = 1100f,
                maxHealth = 1100f,
                isBoss = true,
                nameAr = "الرخ الحديدي العظيم",
                nameEn = "The Iron Rook Bastion",
                symbol = "♜"
            )
            4 -> Enemy(
                id = nextEntityId++,
                type = EnemyType.GRAND_BISHOP_BOSS,
                x = bossX,
                y = groundY,
                health = 1500f,
                maxHealth = 1500f,
                isBoss = true,
                nameAr = "الفيل الأسود الساحر",
                nameEn = "The Grand Arch-Bishop",
                symbol = "♝"
            )
            5 -> Enemy(
                id = nextEntityId++,
                type = EnemyType.TWIN_KNIGHT_BOSS,
                x = bossX,
                y = groundY,
                health = 2200f,
                maxHealth = 2200f,
                isBoss = true,
                nameAr = "فرسان الحرس الملكي (التوأم)",
                nameEn = "Royal Twin Knights",
                symbol = "♞♞"
            )
            else -> Enemy(
                id = nextEntityId++,
                type = EnemyType.KING_BOSS,
                x = bossX,
                y = groundY,
                health = 3500f,
                maxHealth = 3500f,
                isBoss = true,
                nameAr = "الملك الأبيض الخائن",
                nameEn = "The Betrayer White King",
                symbol = "👑"
            )
        }

        enemies.add(boss)
        bossEnemy = boss
        GameSoundEngine.playDramaticThunder()
    }

    fun handleInput(
        moveLeft: Boolean,
        moveRight: Boolean,
        jumpTriggered: Boolean,
        attackTriggered: Boolean,
        blockActive: Boolean,
        dashTriggered: Boolean,
        fireSkillTriggered: Boolean,
        lightningSkillTriggered: Boolean,
        ultimateSkillTriggered: Boolean
    ) {
        if (stageCompleted || currentHealth <= 0) return

        // Dash action
        if (dashTriggered && dashTimer <= 0f && currentStamina >= 25f && !isDashing) {
            isDashing = true
            dashTimer = 0.35f
            invulnerableTimer = 0.35f
            currentStamina -= 25f
            val dashDir = if (facingRight) 1f else -1f
            playerVx = dashDir * 750f * moveSpeedMultiplier
            GameSoundEngine.playDash()
            spawnDashParticles()
        }

        // Horizontal Movement
        if (!isDashing) {
            val speed = 280f * moveSpeedMultiplier * (if (isBlocking) 0.4f else 1f)
            if (moveLeft && !moveRight) {
                playerVx = -speed
                facingRight = false
            } else if (moveRight && !moveLeft) {
                playerVx = speed
                facingRight = true
            } else {
                playerVx *= 0.7f
                if (abs(playerVx) < 10f) playerVx = 0f
            }
        }

        // Jump
        if (jumpTriggered && jumpsRemaining > 0 && !isBlocking) {
            playerVy = -580f
            isGrounded = false
            jumpsRemaining--
            GameSoundEngine.playJump()
            spawnJumpParticles()
        }

        // Blocking
        isBlocking = blockActive && currentStamina > 5f

        // Melee Attack
        if (attackTriggered && attackTimer <= 0f && !isBlocking && !isDashing) {
            executeAttack()
        }

        // Skills
        if (fireSkillTriggered && fireCooldown <= 0f && upgrades.fireSkillUnlocked && currentStamina >= 30f) {
            executeFireSkill()
        }
        if (lightningSkillTriggered && lightningCooldown <= 0f && upgrades.lightningSkillUnlocked && currentStamina >= 45f) {
            executeLightningSkill()
        }
        if (ultimateSkillTriggered && ultimateCooldown <= 0f && upgrades.ultimateSkillUnlocked && currentStamina >= 60f) {
            executeUltimateSkill()
        }
    }

    private fun executeAttack() {
        isAttacking = true
        attackTimer = 0.28f
        attackCombo = (attackCombo % 3) + 1
        currentStamina = (currentStamina - 8f).coerceAtLeast(0f)
        GameSoundEngine.playSlash()

        // Attack Hitbox check
        val attackReach = 110f
        val attackX = if (facingRight) playerX + 30f else playerX - 30f - attackReach
        val damage = baseDamage * (1f + (attackCombo - 1) * 0.3f)
        val isCrit = Random.nextFloat() < 0.25f
        val finalDamage = if (isCrit) damage * 1.8f else damage

        var hitAny = false
        enemies.forEach { enemy ->
            if (!enemy.isDead && abs(enemy.y - playerY) < 90f) {
                val enemyBoxLeft = enemy.x - 30f
                val enemyBoxRight = enemy.x + 30f
                val attackBoxLeft = if (facingRight) playerX else playerX - attackReach
                val attackBoxRight = if (facingRight) playerX + attackReach else playerX

                if (enemyBoxRight >= attackBoxLeft && enemyBoxLeft <= attackBoxRight) {
                    hitEnemy(enemy, finalDamage, isCrit)
                    hitAny = true
                }
            }
        }

        if (hitAny) {
            comboHits++
            comboTimer = 2.5f
            screenShake = if (isCrit) 14f else 6f
        }
    }

    private fun executeFireSkill() {
        fireCooldown = 6f
        currentStamina -= 30f
        GameSoundEngine.playFireSkill()
        screenShake = 10f

        val dir = if (facingRight) 1f else -1f
        projectiles.add(
            Projectile(
                id = nextEntityId++,
                x = playerX + dir * 30f,
                y = playerY - 30f,
                vx = dir * 650f,
                vy = 0f,
                damage = baseDamage * 2.2f,
                isPlayerOwned = true,
                type = ProjectileType.FIRE_WAVE
            )
        )
        spawnSkillBurstParticles(0xFFFF6D00)
    }

    private fun executeLightningSkill() {
        lightningCooldown = 10f
        currentStamina -= 45f
        GameSoundEngine.playLightningSkill()
        screenShake = 18f

        // Strikes all enemies within camera
        val visibleLeft = cameraX - 100f
        val visibleRight = cameraX + 900f

        enemies.filter { !it.isDead && it.x in visibleLeft..visibleRight }.forEach { enemy ->
            hitEnemy(enemy, baseDamage * 2.8f, isCrit = true)
            for (p in 0 until 12) {
                particles.add(
                    Particle(
                        id = nextEntityId++,
                        x = enemy.x + (Random.nextFloat() * 40 - 20),
                        y = enemy.y - 80f + Random.nextFloat() * 100f,
                        vx = (Random.nextFloat() * 200 - 100),
                        vy = (Random.nextFloat() * 200 - 100),
                        color = 0xFF00E5FF,
                        size = Random.nextFloat() * 8f + 4f
                    )
                )
            }
        }
        spawnSkillBurstParticles(0xFF00E5FF)
    }

    private fun executeUltimateSkill() {
        ultimateCooldown = 20f
        currentStamina -= 60f
        GameSoundEngine.playDramaticThunder()
        screenShake = 25f

        // Pawn spirits storm across the screen
        for (i in 0 until 6) {
            val offset = i * 60f
            projectiles.add(
                Projectile(
                    id = nextEntityId++,
                    x = playerX - 150f + offset,
                    y = playerY - 40f + (Random.nextFloat() * 60 - 30),
                    vx = (if (facingRight) 1f else -1f) * (700f + i * 40f),
                    vy = (Random.nextFloat() * 100 - 50),
                    damage = baseDamage * 3.5f,
                    isPlayerOwned = true,
                    type = ProjectileType.KING_GOLDEN_SWORD_BEAM
                )
            )
        }
        spawnSkillBurstParticles(0xFFFFD700)
    }

    private fun hitEnemy(enemy: Enemy, damage: Float, isCrit: Boolean) {
        enemy.health -= damage
        enemy.hitStunTimer = 0.2f
        GameSoundEngine.playHit()

        // Floating combat text
        combatTexts.add(
            FloatingCombatText(
                id = nextEntityId++,
                x = enemy.x + (Random.nextFloat() * 20 - 10),
                y = enemy.y - 60f,
                text = "${damage.toInt()}${if (isCrit) "!" else ""}",
                color = if (isCrit) 0xFFFFD700 else 0xFFFFFFFF,
                isCrit = isCrit
            )
        )

        // Hit spark particles
        for (i in 0 until 6) {
            particles.add(
                Particle(
                    id = nextEntityId++,
                    x = enemy.x,
                    y = enemy.y - 30f,
                    vx = (Random.nextFloat() * 300 - 150),
                    vy = (Random.nextFloat() * 300 - 150),
                    color = if (isCrit) 0xFFFFD700 else 0xFFFF5252,
                    size = Random.nextFloat() * 6f + 3f
                )
            )
        }

        if (enemy.health <= 0f && !enemy.isDead) {
            enemy.isDead = true
            enemy.health = 0f
            onEnemyDefeated(enemy)
        }
    }

    private fun onEnemyDefeated(enemy: Enemy) {
        val coinCount = if (enemy.isBoss) 20 else Random.nextInt(2, 6)
        val coinVal = if (enemy.isBoss) stage.rewardCoins / 4 else 10

        for (c in 0 until coinCount) {
            coins.add(
                CoinDrop(
                    id = nextEntityId++,
                    x = enemy.x + (Random.nextFloat() * 60 - 30),
                    y = enemy.y - 20f,
                    value = coinVal,
                    vy = -Random.nextFloat() * 5f - 3f
                )
            )
        }

        if (enemy.isBoss) {
            GameSoundEngine.playBossDefeated()
            stageCompleted = true
            onStageCleared(stage.rewardCoins + coinsCollectedThisStage)
        }
    }

    fun update(deltaTime: Float) {
        if (stageCompleted || currentHealth <= 0) return
        timeElapsed += deltaTime

        // Screen Shake decay
        if (screenShake > 0f) {
            screenShake = max(0f, screenShake - deltaTime * 30f)
        }

        // Combo timer
        if (comboTimer > 0f) {
            comboTimer -= deltaTime
            if (comboTimer <= 0f) comboHits = 0
        }

        // Cooldowns
        if (attackTimer > 0f) {
            attackTimer -= deltaTime
            if (attackTimer <= 0f) isAttacking = false
        }
        if (dashTimer > 0f) {
            dashTimer -= deltaTime
            if (dashTimer <= 0f) isDashing = false
        }
        if (invulnerableTimer > 0f) invulnerableTimer -= deltaTime
        if (fireCooldown > 0f) fireCooldown -= deltaTime
        if (lightningCooldown > 0f) lightningCooldown -= deltaTime
        if (ultimateCooldown > 0f) ultimateCooldown -= deltaTime

        // Stamina Regen
        if (!isBlocking && !isDashing) {
            currentStamina = min(maxStamina, currentStamina + 22f * deltaTime)
        }

        // Player Physics
        val gravity = 1350f
        playerVy += gravity * deltaTime
        playerX += playerVx * deltaTime
        playerY += playerVy * deltaTime

        // Clamp to stage bounds
        playerX = playerX.coerceIn(50f, stageWidth - 50f)
        if (playerY >= groundY) {
            playerY = groundY
            playerVy = 0f
            isGrounded = true
            jumpsRemaining = 2
        }

        // Camera Tracking
        val targetCameraX = playerX - 350f
        cameraX = cameraX + (targetCameraX.coerceIn(0f, stageWidth - 750f) - cameraX) * 0.12f

        // Spawn Boss when reaching near end
        if (!bossSpawned && playerX > stageWidth - 900f) {
            spawnBoss()
        }

        // Update Projectiles
        updateProjectiles(deltaTime)

        // Update Enemies AI
        updateEnemies(deltaTime)

        // Update Coins & Magnet
        updateCoins(deltaTime)

        // Update Particles & Floating Text
        updateParticlesAndText(deltaTime)
    }

    private fun updateProjectiles(deltaTime: Float) {
        val iter = projectiles.iterator()
        while (iter.hasNext()) {
            val p = iter.next()
            p.x += p.vx * deltaTime
            p.y += p.vy * deltaTime
            p.lifetime -= deltaTime

            // Player projectile hits enemies
            if (p.isPlayerOwned) {
                enemies.forEach { enemy ->
                    if (!enemy.isDead && abs(enemy.x - p.x) < 40f && abs(enemy.y - p.y) < 60f) {
                        hitEnemy(enemy, p.damage, isCrit = true)
                        p.lifetime = 0f
                    }
                }
            } else {
                // Enemy projectile hits player
                if (abs(playerX - p.x) < 35f && abs(playerY - p.y) < 55f && invulnerableTimer <= 0f) {
                    damagePlayer(p.damage)
                    p.lifetime = 0f
                }
            }

            if (p.lifetime <= 0f || p.x < cameraX - 200f || p.x > cameraX + 1200f) {
                iter.remove()
            }
        }
    }

    private fun updateEnemies(deltaTime: Float) {
        enemies.forEach { enemy ->
            if (enemy.isDead) return@forEach

            enemy.stateTime += deltaTime
            if (enemy.hitStunTimer > 0f) {
                enemy.hitStunTimer -= deltaTime
                return@forEach
            }

            val distToPlayer = playerX - enemy.x
            enemy.facingRight = distToPlayer > 0

            // Specific Enemy AI behavior
            when (enemy.type) {
                EnemyType.BLACK_PAWN -> {
                    val speed = 110f
                    if (abs(distToPlayer) > 60f) {
                        enemy.x += (if (enemy.facingRight) 1f else -1f) * speed * deltaTime
                    } else {
                        // Melee attack
                        enemy.attackCooldown -= deltaTime
                        if (enemy.attackCooldown <= 0f) {
                            enemy.attackCooldown = 1.4f
                            if (invulnerableTimer <= 0f && abs(distToPlayer) < 70f) {
                                damagePlayer(if (enemy.isBoss) 30f else 15f)
                            }
                        }
                    }
                }

                EnemyType.ARCHER_PAWN -> {
                    val speed = 90f
                    if (abs(distToPlayer) < 250f) {
                        enemy.x -= (if (enemy.facingRight) 1f else -1f) * speed * deltaTime
                    } else if (abs(distToPlayer) > 400f) {
                        enemy.x += (if (enemy.facingRight) 1f else -1f) * speed * deltaTime
                    }

                    enemy.attackCooldown -= deltaTime
                    if (enemy.attackCooldown <= 0f) {
                        enemy.attackCooldown = 2.2f
                        projectiles.add(
                            Projectile(
                                id = nextEntityId++,
                                x = enemy.x,
                                y = enemy.y - 25f,
                                vx = (if (enemy.facingRight) 1f else -1f) * 450f,
                                vy = 0f,
                                damage = 18f,
                                isPlayerOwned = false,
                                type = ProjectileType.ENEMY_ARROW
                            )
                        )
                    }
                }

                EnemyType.SHADOW_GUARD -> {
                    val speed = 140f
                    if (abs(distToPlayer) > 65f) {
                        enemy.x += (if (enemy.facingRight) 1f else -1f) * speed * deltaTime
                    } else {
                        enemy.attackCooldown -= deltaTime
                        if (enemy.attackCooldown <= 0f) {
                            enemy.attackCooldown = 1.6f
                            if (invulnerableTimer <= 0f && abs(distToPlayer) < 75f) {
                                damagePlayer(25f)
                            }
                        }
                    }
                }

                EnemyType.BLACK_KNIGHT_BOSS -> {
                    // Jumps, rushes, ground stomps
                    enemy.attackCooldown -= deltaTime
                    if (enemy.attackCooldown <= 0f) {
                        enemy.attackCooldown = 2.5f
                        val jumpDir = if (enemy.facingRight) 1f else -1f
                        enemy.x += jumpDir * 180f
                        GameSoundEngine.playHit()
                        screenShake = 12f
                        if (abs(distToPlayer) < 140f && invulnerableTimer <= 0f) {
                            damagePlayer(35f)
                        }
                    } else {
                        val speed = 130f
                        if (abs(distToPlayer) > 80f) {
                            enemy.x += (if (enemy.facingRight) 1f else -1f) * speed * deltaTime
                        }
                    }
                }

                EnemyType.IRON_ROOK_BOSS -> {
                    enemy.attackCooldown -= deltaTime
                    if (enemy.attackCooldown <= 0f) {
                        enemy.attackCooldown = 3.2f
                        // Rook Ram Charge!
                        val chargeDir = if (enemy.facingRight) 1f else -1f
                        enemy.x += chargeDir * 250f
                        projectiles.add(
                            Projectile(
                                id = nextEntityId++,
                                x = enemy.x,
                                y = groundY - 15f,
                                vx = chargeDir * 350f,
                                vy = 0f,
                                damage = 35f,
                                isPlayerOwned = false,
                                type = ProjectileType.ROOK_SHOCKWAVE
                            )
                        )
                        screenShake = 15f
                    } else {
                        enemy.x += (if (enemy.facingRight) 1f else -1f) * 70f * deltaTime
                    }
                }

                EnemyType.GRAND_BISHOP_BOSS -> {
                    enemy.attackCooldown -= deltaTime
                    if (enemy.attackCooldown <= 0f) {
                        enemy.attackCooldown = 2.8f
                        // Shoots 2 diagonal lasers
                        val dir = if (enemy.facingRight) 1f else -1f
                        projectiles.add(
                            Projectile(
                                id = nextEntityId++,
                                x = enemy.x,
                                y = enemy.y - 40f,
                                vx = dir * 420f,
                                vy = 120f,
                                damage = 40f,
                                isPlayerOwned = false,
                                type = ProjectileType.BISHOP_LASER_BEAM
                            )
                        )
                        projectiles.add(
                            Projectile(
                                id = nextEntityId++,
                                x = enemy.x,
                                y = enemy.y - 40f,
                                vx = dir * 420f,
                                vy = -120f,
                                damage = 40f,
                                isPlayerOwned = false,
                                type = ProjectileType.BISHOP_LASER_BEAM
                            )
                        )
                    }
                }

                EnemyType.TWIN_KNIGHT_BOSS -> {
                    enemy.attackCooldown -= deltaTime
                    val speed = 160f
                    if (abs(distToPlayer) > 70f) {
                        enemy.x += (if (enemy.facingRight) 1f else -1f) * speed * deltaTime
                    } else if (enemy.attackCooldown <= 0f) {
                        enemy.attackCooldown = 1.8f
                        damagePlayer(35f)
                    }
                }

                EnemyType.KING_BOSS -> {
                    // Final Boss King AI with phase 1 & phase 2
                    enemy.attackCooldown -= deltaTime
                    val isPhase2 = enemy.health < enemy.maxHealth * 0.5f

                    if (enemy.attackCooldown <= 0f) {
                        enemy.attackCooldown = if (isPhase2) 2.0f else 3.0f
                        val dir = if (enemy.facingRight) 1f else -1f

                        // King Golden Sword Beam
                        projectiles.add(
                            Projectile(
                                id = nextEntityId++,
                                x = enemy.x,
                                y = enemy.y - 35f,
                                vx = dir * 550f,
                                vy = 0f,
                                damage = 45f,
                                isPlayerOwned = false,
                                type = ProjectileType.KING_GOLDEN_SWORD_BEAM
                            )
                        )
                        if (isPhase2) {
                            screenShake = 16f
                        }
                    } else {
                        val speed = if (isPhase2) 150f else 90f
                        if (abs(distToPlayer) > 80f) {
                            enemy.x += (if (enemy.facingRight) 1f else -1f) * speed * deltaTime
                        }
                    }
                }

                else -> {}
            }
        }
    }

    private fun damagePlayer(rawDamage: Float) {
        if (invulnerableTimer > 0f) return

        if (isBlocking) {
            val blockedDamage = rawDamage * damageBlockMultiplier
            currentHealth -= blockedDamage
            currentStamina = (currentStamina - 20f).coerceAtLeast(0f)
            invulnerableTimer = 0.4f
            GameSoundEngine.playShieldBlock()
            screenShake = 5f
            combatTexts.add(
                FloatingCombatText(
                    id = nextEntityId++,
                    x = playerX,
                    y = playerY - 50f,
                    text = "BLOCKED -${blockedDamage.toInt()}",
                    color = 0xFF00B0FF
                )
            )
        } else {
            currentHealth -= rawDamage
            invulnerableTimer = 0.6f
            GameSoundEngine.playHit()
            screenShake = 14f
            combatTexts.add(
                FloatingCombatText(
                    id = nextEntityId++,
                    x = playerX,
                    y = playerY - 50f,
                    text = "-${rawDamage.toInt()}",
                    color = 0xFFFF1744
                )
            )
        }

        if (currentHealth <= 0f) {
            currentHealth = 0f
            GameSoundEngine.stopBattleMusic()
            onGameOver()
        }
    }

    private fun updateCoins(deltaTime: Float) {
        val iter = coins.iterator()
        while (iter.hasNext()) {
            val coin = iter.next()
            coin.y += coin.vy
            coin.vy += 9.8f * deltaTime

            if (coin.y >= groundY) {
                coin.y = groundY
                coin.vy = 0f
            }

            // Magnet towards player
            val dx = playerX - coin.x
            val dy = (playerY - 20f) - coin.y
            val dist = kotlin.math.sqrt(dx * dx + dy * dy)

            if (dist < 180f) {
                coin.x += (dx / dist) * 350f * deltaTime
                coin.y += (dy / dist) * 350f * deltaTime
            }

            if (dist < 35f) {
                coinsCollectedThisStage += coin.value
                GameSoundEngine.playCoin()
                iter.remove()
            }
        }
    }

    private fun updateParticlesAndText(deltaTime: Float) {
        val pIter = particles.iterator()
        while (pIter.hasNext()) {
            val p = pIter.next()
            p.x += p.vx * deltaTime
            p.y += p.vy * deltaTime
            p.life += deltaTime
            p.alpha = 1f - (p.life / p.maxLife)
            if (p.life >= p.maxLife) pIter.remove()
        }

        val tIter = combatTexts.iterator()
        while (tIter.hasNext()) {
            val t = tIter.next()
            t.y -= 45f * deltaTime
            t.life += deltaTime
            if (t.life >= t.maxLife) tIter.remove()
        }
    }

    private fun spawnDashParticles() {
        for (i in 0 until 8) {
            particles.add(
                Particle(
                    id = nextEntityId++,
                    x = playerX + (Random.nextFloat() * 20 - 10),
                    y = playerY - 30f + (Random.nextFloat() * 40 - 20),
                    vx = (if (facingRight) -1f else 1f) * (Random.nextFloat() * 200 + 100),
                    vy = (Random.nextFloat() * 60 - 30),
                    color = 0xFF7C4DFF,
                    size = Random.nextFloat() * 7f + 3f
                )
            )
        }
    }

    private fun spawnJumpParticles() {
        for (i in 0 until 6) {
            particles.add(
                Particle(
                    id = nextEntityId++,
                    x = playerX + (Random.nextFloat() * 30 - 15),
                    y = groundY,
                    vx = (Random.nextFloat() * 120 - 60),
                    vy = -Random.nextFloat() * 80,
                    color = 0xFFD4C29A,
                    size = Random.nextFloat() * 5f + 2f
                )
            )
        }
    }

    private fun spawnSkillBurstParticles(color: Long) {
        for (i in 0 until 16) {
            val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
            val speed = Random.nextFloat() * 250f + 100f
            particles.add(
                Particle(
                    id = nextEntityId++,
                    x = playerX,
                    y = playerY - 30f,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = color,
                    size = Random.nextFloat() * 9f + 4f
                )
            )
        }
    }
}
