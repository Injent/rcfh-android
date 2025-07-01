package ru.rcfh.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

class ColorScheme internal constructor(
    // Background Surfaces

    /** Brand color background Eg. button, indicator **/
    val backgroundBrand: Color,

    /** Brand background pressed Eg. button is pressed **/
    val backgroundPressed: Color,

    /** Primary content Eg. Mail, card, list, calendar, and etc. **/
    val background1: Color,

    /** Secondary surface. Primary contents sit in this surface as well as List **/
    val background2: Color,

    /** Tertiary surface, Mostly main navigation Eg. suite header, app bar. **/
    val background3: Color,

    /** Quaternary surface. For apps needing more surface hierarchy **/
    val background4: Color,

    /** Background button for button's container color **/
    val backgroundTouchable: Color,

    /** Inverted background surface Eg. inverted Toast notification. **/
    val backgroundToast: Color,

    /** Background surface for disabled components **/
    val backgroundDisabled: Color,

    // Foreground surfaces

    /** Brand foreground Eg. clickable text, text on button **/
    val foreground: Color,

    /** Brand foreground pressed **/
    val link: Color,

    /** Primary foreground color Eg. text/icons on buttons **/
    val foreground1: Color,

    /** Secondary foreground color **/
    val foreground2: Color,

    /** Tertiary foreground color small headlines, categories in spinner **/
    val foreground3: Color,

    /** Quaternary foreground color Eg. placeholder text **/
    val foreground4: Color,

    /** Foreground surface on brand colors **/
    val foregroundOnBrand: Color,

    /** Foreground for error text and icons **/
    val foregroundError: Color,

    /** Foreground for warning text and icons **/
    val foregroundWarning: Color,

    // Stroke (Dividers/Border)

    /** Primary stroke color eg. primary button border, input field border **/
    val stroke1: Color,

    /** Secondary stroke color eg. page dividers **/
    val stroke2: Color,

    /** Stroke color for additional contrast, used in radio and checkboxes components. **/
    val strokeAccessible: Color,

    /** Stroke Disabled **/
    val strokeDisabled: Color,

    /** Brand Stroke **/
    val brandStroke: Color,

    val statusSuccess: Color,

    val tableHeader: Color,

    val paleRed: Color,
    val paleRedBorder: Color,
    val paleYellow: Color,
    val paleYellowBorder: Color,

    internal val isDarkTheme: Boolean
)

internal fun lightColorScheme() = ColorScheme(
    backgroundBrand = Color(0xFF363636), //
    backgroundPressed = Color(0xFF046DD6), //
    background1 = Color.White, //
    background2 = Color(0xFFF2F2F2), //
    background3 = Color.White, //
    background4 = Color(0xFFE5E5E5),
    backgroundToast = Color(0xff495368),
    backgroundDisabled = Color(0xFF9EAAC2), //
    backgroundTouchable = Color.White, //
    foreground = Color(0xFF363636),
    link = Color(0xFF046DD6),
    foreground1 = Color(0xff1d1d1d), //
    foreground2 = Color(0xff566379), //
    foreground3 = Color(0xFF9099B1), //
    foreground4 = Color(0xFF888888), //
    foregroundOnBrand = Color.White, //
    stroke1 = Color(0xFFD1D1D1),
    stroke2 = Color(0xffd5d5d5), //
    strokeAccessible = Color(0xFF808B9F), //
    strokeDisabled = Color(0xFFA8A8A8),
    brandStroke = Color(0xFF363636), //
    foregroundError = Color(0xFFFF3B30), //
    foregroundWarning = Color(0xFFFFB72A),
    statusSuccess = Color(0xFF21a366),
    tableHeader = Color(0xFFfafafb),
    paleRed = Color(0xFFfcefea),
    paleRedBorder = Color(0xFFe5cdc4),
    paleYellow = Color(0xFFfff6ea),
    paleYellowBorder = Color(0xFFeee2ba),
    isDarkTheme = false
)

internal val LocalAppColorScheme = staticCompositionLocalOf<ColorScheme> {
    error("ColorScheme not provided")
}