package ru.rcfh.blank.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.blank.ui.queryapi.queryOrCreate
import ru.rcfh.blank.ui.queryapi.update
import ru.rcfh.blank.ui.rule.OnlyDecimal
import ru.rcfh.blank.ui.rule.OnlyNumber
import ru.rcfh.blank.ui.state.DocumentScope
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.glpm.feature.blank.R

enum class InputType {
    Number,
    Decimal,
    Text
}

abstract class BasicInputComponent(
    target: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    inputType: InputType,
    protected val onCard: Boolean = false,
    protected val lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
) : Component(target) {
    abstract val label: String
    open val placeholder: String = when (inputType) {
        InputType.Number -> context.getString(R.string.feature_blank_placeholder_number)
        InputType.Decimal -> context.getString(R.string.feature_blank_placeholder_decimal)
        InputType.Text -> context.getString(R.string.feature_blank_placeholder_text)
    }
    open val unit: String? = null
    protected val keyboardOptions = keyboardOptions.copy(
        keyboardType = if (keyboardOptions.keyboardType == KeyboardType.Unspecified) {
            when (inputType) {
                InputType.Number -> KeyboardType.Number
                InputType.Decimal -> KeyboardType.Decimal
                InputType.Text -> KeyboardType.Text
            }
        } else keyboardOptions.keyboardType,
        capitalization = if (keyboardOptions.capitalization == KeyboardCapitalization.Unspecified) {
            KeyboardCapitalization.Sentences
        } else keyboardOptions.capitalization,
        autoCorrectEnabled = if (keyboardOptions.autoCorrectEnabled == null) {
            true
        } else keyboardOptions.autoCorrectEnabled,
        imeAction = if (keyboardOptions.imeAction == ImeAction.Unspecified) {
            ImeAction.Next
        } else keyboardOptions.imeAction
    )

    override fun isMetStrictRules(s: String): Boolean {
        if (s.isEmpty()) return true
        val isMatchingInputType = when (keyboardOptions.keyboardType) {
            KeyboardType.Decimal -> s.matches(Regex.OnlyDecimal)
            KeyboardType.Number -> s.matches(Regex.OnlyNumber)
            else -> true
        }
        return isMatchingInputType && super.isMetStrictRules(s)
    }

    @Composable
    override fun Content(state: Element, navigator: ViewerNavigator, documentScope: DocumentScope) {
        val bringIntoViewRequester = remember { BringIntoViewRequester() }
        val mutableInteractionSource = remember { MutableInteractionSource() }
        val isFocused by mutableInteractionSource.collectIsFocusedAsState()

        LaunchedEffect(isFocused, errorMsg) {
            if (errorMsg != null && isFocused) {
                bringIntoViewRequester.bringIntoView()
            }
        }

        AppTextField(
            label = label,
            value = state.queryOrCreate(target, ""),
            onValueChange = { newValue ->
                val s = when (keyboardOptions.keyboardType) {
                    KeyboardType.Decimal,
                    KeyboardType.Number -> newValue.replace(',', '.')
                    else -> newValue
                }
                if (isMetStrictRules(s)) {
                    state.update(target, s)
                }
            },
            trailingIcon = {
                unit?.let { Text(text = it) }
            },
            interactionSource = mutableInteractionSource,
            error = errorMsg,
            enabled = enabled,
            placeholder = placeholder,
            onCard = onCard,
            keyboardOptions = keyboardOptions,
            lineLimits = lineLimits,
            modifier = Modifier
                .bringIntoViewRequester(bringIntoViewRequester)
        )
    }
}