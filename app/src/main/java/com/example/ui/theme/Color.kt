package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Paper Canvas Backgrounds
val CreamPaper = Color(0xFFFBF8F2)
val WarmKraft = Color(0xFFEFE8DA)
val AgedParchment = Color(0xFFF4EAD4)
val DarkParchment = Color(0xFF1E2124)
val BotanicalLinen = Color(0xFFF3F6F2)
val SoftBlushPaper = Color(0xFFFDF5F2)
val NewspaperSepia = Color(0xFFF2ECE1)
val Y2KPaper = Color(0xFFFFF9FB)

// Ink & Lead Pencils
val InkDark = Color(0xFF2C2523)
val InkSepia = Color(0xFF4A3E38)
val InkFaded = Color(0xFF756A63)
val PencilGrey = Color(0xFF5A5A5A)
val InkMidnight = Color(0xFFE2E4E8)

// Desk Surfaces
val DeskWoodWarm = Color(0xFF3E2D23)
val DeskWoodLight = Color(0xFF6E5646)
val DeskMatLeather = Color(0xFF2B201A)

// Washi Tape Tints
val TapeWarmTerracotta = Color(0xFFE29578)
val TapeSage = Color(0xFF83C5BE)
val TapeMustard = Color(0xFFE9C46A)
val TapeDustyRose = Color(0xFFDDA15E)
val TapeLavender = Color(0xFFB8B8D1)
val TapeSky = Color(0xFFA8DADC)
val TapePlaidBrown = Color(0xFFB08968)

// Sticky Note Tints
val StickyYellow = Color(0xFFFFF9C4)
val StickyRose = Color(0xFFFFCDD2)
val StickyMint = Color(0xFFC8E6C9)
val StickyLavender = Color(0xFFE1BEE7)
val StickyPeach = Color(0xFFFFCCBC)
val StickySky = Color(0xFFB3E5FC)

// Mood Colors (Customizable in journal)
val MoodHappyColor = Color(0xFFFFB703) // Warm Sunshine
val MoodCalmColor = Color(0xFF83C5BE) // Seafoam Sage
val MoodOkayColor = Color(0xFFD4A373) // Warm Sand
val MoodSadColor = Color(0xFF6B9080) // Rainy River
val MoodAngryColor = Color(0xFFE76F51) // Burnt Sienna
val MoodTiredColor = Color(0xFF9E829C) // Dusty Mauve
val MoodExcitedColor = Color(0xFFFB8500) // Vibrant Coral

// Vibrant Palette Theme Colors
val VibrantPaper = Color(0xFFFDFBF7)
val VibrantPaperTint = Color(0xFFF5F2ED)
val VibrantInk = Color(0xFF2D2926)
val VibrantSubText = Color(0xFF5D4037)
val VibrantCoral = Color(0xFFFF8A65)
val VibrantCoralBg = Color(0xFFFFF1ED)
val VibrantSky = Color(0xFF64B5F6)
val VibrantSkyBg = Color(0xFFE3F2FD)
val VibrantViolet = Color(0xFFBA68C8)
val VibrantVioletBg = Color(0xFFF3E5F5)
val VibrantGreen = Color(0xFF81C784)
val VibrantGreenBg = Color(0xFFE8F5E9)
val VibrantLime = Color(0xFFD4E157)
val VibrantYellow = Color(0xFFFFF9C4)
val VibrantTeal = Color(0xFF4DB6AC)
val VibrantLavender = Color(0xFF7986CB)

// Journal Cover Styles
data class JournalThemeConfig(
    val name: String,
    val coverColor: Color,
    val coverAccentColor: Color,
    val spineColor: Color,
    val ribbonColor: Color,
    val paperColor: Color,
    val textColor: Color,
    val secondaryTextColor: Color,
    val washiColors: List<Color>,
    val description: String
)

val JournalThemes = listOf(
    JournalThemeConfig(
        name = "Vibrant Palette",
        coverColor = Color(0xFF2D2926),
        coverAccentColor = VibrantCoral,
        spineColor = Color(0xFF1E1C1A),
        ribbonColor = VibrantViolet,
        paperColor = VibrantPaper,
        textColor = VibrantInk,
        secondaryTextColor = VibrantSubText,
        washiColors = listOf(
            VibrantLime,
            VibrantCoral,
            VibrantViolet,
            VibrantSky,
            VibrantGreen,
            VibrantTeal
        ),
        description = "Vibrant handmade scrapbook with newspaper masthead, pastel paper washi, and playful color notes"
    ),
    JournalThemeConfig(
        name = "Cozy Scrapbook",
        coverColor = Color(0xFF5C4033),
        coverAccentColor = Color(0xFFD4A373),
        spineColor = Color(0xFF432D24),
        ribbonColor = Color(0xFFB23A2B),
        paperColor = Color(0xFFFBF8F2),
        textColor = Color(0xFF2C2523),
        secondaryTextColor = Color(0xFF756A63),
        washiColors = listOf(TapeWarmTerracotta, TapeSage, TapeMustard),
        description = "Warm kraft textures, dried floral tones, and cozy tea vibes"
    ),
    JournalThemeConfig(
        name = "Vintage Newspaper",
        coverColor = Color(0xFF383735),
        coverAccentColor = Color(0xFFD6CDBF),
        spineColor = Color(0xFF262524),
        ribbonColor = Color(0xFF5A5A5A),
        paperColor = Color(0xFFF2ECE1),
        textColor = Color(0xFF1E1D1B),
        secondaryTextColor = Color(0xFF5C5955),
        washiColors = listOf(Color(0xFFC4B8A5), Color(0xFFAFA290), Color(0xFFD9D0C3)),
        description = "Aged sepia paper, bold editorial titles, and classic type"
    ),
    JournalThemeConfig(
        name = "Botanical",
        coverColor = Color(0xFF2E4032),
        coverAccentColor = Color(0xFF87A987),
        spineColor = Color(0xFF1F2E23),
        ribbonColor = Color(0xFF588157),
        paperColor = Color(0xFFF4F7F2),
        textColor = Color(0xFF1B2E20),
        secondaryTextColor = Color(0xFF526D57),
        washiColors = listOf(Color(0xFF83C5BE), Color(0xFFA3B18A), Color(0xFFCCD5AE)),
        description = "Forest greens, pressed eucalyptus, and herbarium notes"
    ),
    JournalThemeConfig(
        name = "Midnight",
        coverColor = Color(0xFF191D26),
        coverAccentColor = Color(0xFF79889C),
        spineColor = Color(0xFF10131A),
        ribbonColor = Color(0xFF3D4E6B),
        paperColor = Color(0xFF20252E),
        textColor = Color(0xFFECEFF4),
        secondaryTextColor = Color(0xFF9AA5B8),
        washiColors = listOf(Color(0xFF4C566A), Color(0xFF5E81AC), Color(0xFF88C0D0)),
        description = "Starry night sketches, silver ink, and dark paper intimacy"
    ),
    JournalThemeConfig(
        name = "Y2K / Colorful",
        coverColor = Color(0xFFE85D75),
        coverAccentColor = Color(0xFFFFD166),
        spineColor = Color(0xFFC73E56),
        ribbonColor = Color(0xFF06D6A0),
        paperColor = Color(0xFFFFFBFD),
        textColor = Color(0xFF33202A),
        secondaryTextColor = Color(0xFF7D596B),
        washiColors = listOf(Color(0xFFFF99C8), Color(0xFFFCF6BD), Color(0xFFD0F4DE)),
        description = "Playful pastel tape, sticker bomb vibes, and radiant colors"
    ),
    JournalThemeConfig(
        name = "Minimal Paper",
        coverColor = Color(0xFFE6E1DA),
        coverAccentColor = Color(0xFF99938B),
        spineColor = Color(0xFFC8C2B8),
        ribbonColor = Color(0xFF8B857D),
        paperColor = Color(0xFFFAF8F5),
        textColor = Color(0xFF2B2825),
        secondaryTextColor = Color(0xFF7A756F),
        washiColors = listOf(Color(0xFFE2DDD5), Color(0xFFD1CBC0), Color(0xFFC2BAAE)),
        description = "Clean Japanese cotton paper, subtle graphite, and gentle space"
    ),
    JournalThemeConfig(
        name = "Soft Neutral",
        coverColor = Color(0xFF8C7A6B),
        coverAccentColor = Color(0xFFDFD3C3),
        spineColor = Color(0xFF6B5A4D),
        ribbonColor = Color(0xFFC7B198),
        paperColor = Color(0xFFF9F6F0),
        textColor = Color(0xFF3D352E),
        secondaryTextColor = Color(0xFF807469),
        washiColors = listOf(Color(0xFFDDB892), Color(0xFFB08968), Color(0xFFE6CCB2)),
        description = "Warm oatmeal linen, coffee stains, and comforting neutrals"
    ),
    JournalThemeConfig(
        name = "Classic Diary",
        coverColor = Color(0xFF78281F),
        coverAccentColor = Color(0xFFD4AF37),
        spineColor = Color(0xFF561A13),
        ribbonColor = Color(0xFF900C3F),
        paperColor = Color(0xFFFCF8EE),
        textColor = Color(0xFF2A1B18),
        secondaryTextColor = Color(0xFF6D524D),
        washiColors = listOf(Color(0xFFD4AF37), Color(0xFF8B4513), Color(0xFFC0A080)),
        description = "Gilded edges, crimson ribbon, and locked memoirs"
    )
)

fun getThemeByName(name: String): JournalThemeConfig {
    return JournalThemes.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: JournalThemes[0]
}
