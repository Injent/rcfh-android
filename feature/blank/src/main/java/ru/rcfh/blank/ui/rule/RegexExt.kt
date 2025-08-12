package ru.rcfh.blank.ui.rule

val Regex.Companion.OnlyNumber: Regex
    get() = "^-?(0(\\.[0-9]*)?|[1-9][0-9]*(\\.[0-9]+)?|[1-9][0-9]*\\.)\$".toRegex()

val Regex.Companion.OnlyDecimal: Regex
    get() = "^(?:\$|(-?[1-9]\\d*|-?0(?!\\d))\$)".toRegex()