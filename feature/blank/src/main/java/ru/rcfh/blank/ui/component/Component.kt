package ru.rcfh.blank.ui.component

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.blank.ui.queryapi.queryOrDefault
import ru.rcfh.blank.ui.rule.Rule
import ru.rcfh.blank.ui.state.DocumentScope
import ru.rcfh.blank.ui.state.Element

abstract class Component(
    val target: String
) : KoinComponent {
    var errorMsg: String? by mutableStateOf(null)
    var enabled: Boolean by mutableStateOf(true)
    open val rules: List<Rule> = listOf()

    protected val context: Context
        get() = get<Context>()

    fun init(documentScope: DocumentScope) {
        documentScope.listen { state ->
            calculate(documentScope, state)
            enabled = isEnabled(state)
            findError(
                state = state,
                s = provideValidationValue(state)
            )
        }
    }

    open fun calculate(documentScope: DocumentScope, state: Element) {}

    open fun findError(state: Element, s: String?) {
        if (!enabled) {
            errorMsg = null
            return
        }
        if (s == null) return
        val failedRule = rules.firstOrNull { rule ->
            if (rule is Rule.StrictInput) return@firstOrNull false
            !rule.isMet(s)
        }
        errorMsg = failedRule?.run {
            with(context.resources) { this.message(s) }
        }
    }

    open fun isEnabled(state: Element): Boolean = true

    open fun isMetStrictRules(s: String): Boolean {
        if (s.isEmpty()) return true
        return rules
            .filterIsInstance<Rule.StrictInput>()
            .all { it.isMet(s) }
    }

    @Composable
    abstract fun Content(
        state: Element,
        navigator: ViewerNavigator,
        documentScope: DocumentScope
    )

    private fun provideValidationValue(state: Element): String? {
        if (target.isEmpty()) return null
        return state.queryOrDefault(target, "")
    }
}
