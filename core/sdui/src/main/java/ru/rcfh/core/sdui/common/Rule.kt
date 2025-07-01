package ru.rcfh.core.sdui.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
@Polymorphic
sealed interface Rule {
    val message: String
    fun isMet(s: String): Boolean

    @Serializable
    @SerialName("required")
    data class Required(override val message: String) : Rule {
        override fun isMet(s: String): Boolean {
            return s.isNotBlank()
        }
    }

    @Serializable
    @SerialName("digit_format")
    data class DigitFormat(
        val decimalSize: Int,
        val precise: Int? = null,
        override val message: String,
    ) : Rule {
        override fun isMet(s: String): Boolean {
            if (s.isEmpty()) return true
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
                return (decimalPart.count(Char::isDigit) <= decimalSize)
                        && (precisePart.count(Char::isDigit) <= (precise ?: Int.MAX_VALUE))
            } else {
                s.count(Char::isDigit) <= decimalSize
            }
        }
    }

    @Serializable
    @SerialName("regex")
    data class Regex(
        @Serializable(RegexSerializer::class)
        private val regex: kotlin.text.Regex,
        override val message: String
    ) : Rule {
        override fun isMet(s: String): Boolean {
            return regex.matches(s)
        }
    }

    @Serializable
    @SerialName("strict_input")
    data class StrictInput(
        @Serializable(RegexSerializer::class)
        private val regex: kotlin.text.Regex,
    ) : Rule {
        @Transient
        override val message: String = ""

        override fun isMet(s: String): Boolean {
            return regex.matches(s)
        }
    }
}

object RegexSerializer : KSerializer<Regex> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Regex", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Regex) {
        encoder.encodeString(value.pattern)
    }

    override fun deserialize(decoder: Decoder): Regex {
        val pattern = decoder.decodeString()
        return Regex(pattern)
    }
}