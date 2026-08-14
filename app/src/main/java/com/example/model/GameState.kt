package com.example.model

enum class ScreenState {
    PROLOGUE_CINEMATIC,
    MAIN_MENU,
    STAGE_SELECT,
    UPGRADE_SHOP,
    GAMEPLAY,
    GAME_OVER,
    VICTORY_STAGE,
    ENDING_CINEMATIC
}

data class StageDefinition(
    val id: Int,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val bossNameAr: String,
    val bossNameEn: String,
    val bossSymbol: String,
    val rewardCoins: Int,
    val stageWidth: Float = 3200f,
    val themeColor: Long = 0xFF7C4DFF
)

val GAME_STAGES = listOf(
    StageDefinition(
        id = 1,
        titleAr = "المرحلة 1: بداية الخيانة",
        titleEn = "Stage 1: Dawn of Betrayal",
        descriptionAr = "يتعلم الجندي مهارات القتال بعد خيانة الملك وموت رفاقه في أرض المعركة.",
        descriptionEn = "The Pawn learns combat and survival after the King's brutal sacrifice.",
        bossNameAr = "قائد الجنود السود",
        bossNameEn = "Black Pawn Captain",
        bossSymbol = "♟️",
        rewardCoins = 150,
        stageWidth = 2800f,
        themeColor = 0xFF5C6BC0
    ),
    StageDefinition(
        id = 2,
        titleAr = "المرحلة 2: الغابة السوداء",
        titleEn = "Stage 2: The Dark Forest",
        descriptionAr = "منطقة خطرة مليئة بالكمائن والرماة والفرسان سريعي الحركة.",
        descriptionEn = "A hazardous woods packed with ambushes, archers, and swift knight cavalry.",
        bossNameAr = "الفارس الأسود الملكي",
        bossNameEn = "The Royal Black Knight",
        bossSymbol = "♞",
        rewardCoins = 300,
        stageWidth = 3400f,
        themeColor = 0xFF2E7D32
    ),
    StageDefinition(
        id = 3,
        titleAr = "المرحلة 3: قلعة الرخ",
        titleEn = "Stage 3: Fortress of the Rook",
        descriptionAr = "حصن عتيد يضم جسورًا معلقة وأفخاخًا والرخ المدرع الذي لا يتزحزح.",
        descriptionEn = "A colossal stone fortress with traps, drawbridges, and the unyielding Iron Rook.",
        bossNameAr = "الرخ الحديدي العظيم",
        bossNameEn = "The Iron Rook Bastion",
        bossSymbol = "♜",
        rewardCoins = 500,
        stageWidth = 3800f,
        themeColor = 0xFF607D8B
    ),
    StageDefinition(
        id = 4,
        titleAr = "المرحلة 4: مدينة الفيل",
        titleEn = "Stage 4: Realm of the Bishop",
        descriptionAr = "أزقة حجرية وسحر أسود. الفيل يستخدم ضربات ليزر قطرية بعيدة المدى.",
        descriptionEn = "Stone alleys imbued with dark magic. The Bishop attacks with deadly diagonal laser barrages.",
        bossNameAr = "الفيل الأسود الساحر",
        bossNameEn = "The Grand Arch-Bishop",
        bossSymbol = "♝",
        rewardCoins = 750,
        stageWidth = 4000f,
        themeColor = 0xFF8E24AA
    ),
    StageDefinition(
        id = 5,
        titleAr = "المرحلة 5: فرسان الملك",
        titleEn = "Stage 5: The King's Vanguard",
        descriptionAr = "طريق قاعة العرش المحمي بأعتى فرسان المملكة الشطرنجية.",
        descriptionEn = "The golden path to the palace guarded by twin royal champion knights.",
        bossNameAr = "فرسان الحرس الملكي (التوأم)",
        bossNameEn = "Royal Twin Knights",
        bossSymbol = "♞♞",
        rewardCoins = 1000,
        stageWidth = 4200f,
        themeColor = 0xFFD84315
    ),
    StageDefinition(
        id = 6,
        titleAr = "المرحلة 6: عرش الملك الخائن",
        titleEn = "Stage 6: The Betrayer King's Throne",
        descriptionAr = "المواجهة الحاسمة مع الملك الأبيض الذي ضحى برعاياه لحماية عرشه.",
        descriptionEn = "The final confrontation with the White King who sacrificed his faithful pawns.",
        bossNameAr = "الملك الأبيض الخائن",
        bossNameEn = "The Betrayer White King",
        bossSymbol = "👑",
        rewardCoins = 2500,
        stageWidth = 4600f,
        themeColor = 0xFFFFD700
    )
)

data class PlayerUpgrades(
    val swordLevel: Int = 1,     // 1 to 5: Base attack 20, 35, 50, 70, 100
    val shieldLevel: Int = 1,    // 1 to 5: Block % 40%, 55%, 70%, 85%, 95%
    val healthLevel: Int = 1,    // 1 to 5: Max HP 100, 150, 200, 260, 350
    val speedLevel: Int = 1,     // 1 to 5: Speed multiplier 1.0f to 1.5f
    val fireSkillUnlocked: Boolean = false,
    val lightningSkillUnlocked: Boolean = false,
    val ultimateSkillUnlocked: Boolean = false,
    val coins: Int = 0,
    val unlockedStage: Int = 1,
    val completedStages: Set<Int> = emptySet(),
    val totalKills: Int = 0
)

enum class EnemyType {
    BLACK_PAWN,
    ARCHER_PAWN,
    SHADOW_GUARD,
    BLACK_KNIGHT_BOSS,
    IRON_ROOK_BOSS,
    GRAND_BISHOP_BOSS,
    TWIN_KNIGHT_BOSS,
    KING_BOSS,
    QUEEN_SHADOW_MINION
}

data class Enemy(
    val id: Long,
    val type: EnemyType,
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var health: Float,
    val maxHealth: Float,
    val isBoss: Boolean = false,
    var facingRight: Boolean = false,
    var stateTime: Float = 0f,
    var attackCooldown: Float = 0f,
    var isAttacking: Boolean = false,
    var hitStunTimer: Float = 0f,
    var isDead: Boolean = false,
    var deathAnimationTime: Float = 0f,
    val nameAr: String,
    val nameEn: String,
    val symbol: String
)

data class Projectile(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val damage: Float,
    val isPlayerOwned: Boolean,
    val type: ProjectileType,
    var lifetime: Float = 3f
)

enum class ProjectileType {
    FIRE_WAVE,
    LIGHTNING_ORB,
    ENEMY_ARROW,
    BISHOP_LASER_BEAM,
    KING_GOLDEN_SWORD_BEAM,
    ROOK_SHOCKWAVE
}

data class Particle(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var alpha: Float = 1f,
    val color: Long,
    val size: Float,
    val maxLife: Float = 0.6f,
    var life: Float = 0f
)

data class FloatingCombatText(
    val id: Long,
    var x: Float,
    var y: Float,
    val text: String,
    val color: Long,
    var life: Float = 0f,
    val maxLife: Float = 0.8f,
    val isCrit: Boolean = false
)

data class CoinDrop(
    val id: Long,
    var x: Float,
    var y: Float,
    val value: Int,
    var isCollected: Boolean = false,
    var vy: Float = -4f
)

data class StoryDialogue(
    val speakerAr: String,
    val speakerEn: String,
    val textAr: String,
    val textEn: String,
    val symbol: String,
    val imageResId: Int? = null,
    val durationSeconds: Float = 4.5f
)
