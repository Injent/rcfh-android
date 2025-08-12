package ru.rcfh.blank.ui.rule

import android.content.res.Resources
import ru.rcfh.glpm.feature.blank.R
import java.util.Locale

sealed interface Rule {
    fun Resources.message(s: String): String

    fun isMet(s: String): Boolean

    data class Required(private val message: String? = null) : Rule {
        override fun Resources.message(s: String): String {
            return message ?: getString(R.string.feature_blank_error_required)
        }

        override fun isMet(s: String): Boolean {
            return s.isNotBlank()
        }
    }

    data class DigitFormat(
        val decimals: Int,
        val precise: Int? = null,
        val message: String? = null
    ) : Rule {
        override fun Resources.message(s: String): String {
            return message ?: run {
                if (precise != null && s.count { it == '.' } == 1) {
                    val (intPart, fracPart) = s.split('.')
                    if (intPart.length > decimals) {
                        return@run getString(R.string.feature_blank_error_digitFormat1, decimals)
                    }
                    if (fracPart.length > precise) {
                        return@run getString(R.string.feature_blank_error_digitFormat2, precise)
                    }
                    getString(R.string.feature_blank_error_digitFormat0)
                } else {
                    if (s.length > decimals) {
                        getString(R.string.feature_blank_error_digitFormat1, decimals)
                    } else {
                        getString(R.string.feature_blank_error_digitFormat0)
                    }
                }
            }
        }

        override fun isMet(s: String): Boolean {
            if (s.isEmpty()) return true
            if (s.any { it.isLetter() }) return false
            if (s.endsWith('.')) return false
            val hasDot = s
                .count { it == '.' }
                .let { dotCount ->
                    if (dotCount > 1) {
                        return false
                    } else {
                        dotCount > 0
                    }
                }

            return if (hasDot) {
                val (decimalPart, precisePart) = s.split('.')
                return (decimalPart.count(Char::isDigit) <= decimals)
                        && (precisePart.count(Char::isDigit) <= (precise ?: Int.MAX_VALUE))
            } else {
                s.count(Char::isDigit) <= decimals
            }
        }
    }

    data class Regex(
        private val regex: kotlin.text.Regex,
        private val message: String
    ) : Rule {
        override fun Resources.message(s: String): String {
            return message
        }

        override fun isMet(s: String): Boolean {
            return regex.matches(s)
        }
    }

    data class StrictInput(
        private val regex: kotlin.text.Regex,
    ) : Rule {
        override fun Resources.message(s: String): String {
            return ""
        }

        override fun isMet(s: String): Boolean {
            return regex.matches(s)
        }
    }

    data class Range(
        private val min: Double,
        private val max: Double,
        val message: String? = null,
    ) : Rule {
        override fun Resources.message(s: String): String {
            return message ?: run {
                s.toDoubleOrNull()?.let { number ->
                    when {
                        number < min -> getString(R.string.feature_blank_error_range0, min.shortFormat())
                        number > max -> getString(R.string.feature_blank_error_range1, max.shortFormat())
                        else -> ""
                    }
                } ?: ""
            }
        }

        override fun isMet(s: String): Boolean {
            val number = s.toDoubleOrNull() ?: return true
            return number in min..max
        }
    }
}

private fun Double.shortFormat(): String {
    return if (this % 1 == 0.0) {
        String.format(Locale.ENGLISH, "%.0f", this)
    } else {
        String.format(Locale.ENGLISH, "%.3f", this).trimEnd('0')
    }
}
