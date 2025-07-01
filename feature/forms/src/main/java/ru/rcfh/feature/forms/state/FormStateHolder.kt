package ru.rcfh.feature.forms.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ru.rcfh.core.sdui.model.FormTemplate
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.state.FormState
import ru.rcfh.core.sdui.state.TableState
import ru.rcfh.core.sdui.storage.FormStateManager
import java.util.Stack
import kotlin.math.round

class FormStateHolder(
    private val documentId: Int,
    private val form: FormTemplate,
    private val formStateManager: FormStateManager,
    private val scope: CoroutineScope,
) {
    private val states = mutableStateMapOf<String, FormState>()

    internal suspend fun load() {
        form.templates.forEach { template ->
            val state = formStateManager.getState(
                documentId = documentId,
                formId = form.id,
                template = template,
            )
            states[template.id] = state
        }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : FormState> get(templateId: String): T? {
        return states[templateId] as? T
    }

    operator fun set(templateId: String, state: FormState) {
        states[templateId] = state
        scope.launch {
            formStateManager.saveState(documentId, form.id, templateId, state)
        }
    }

    fun setAndRecalculate(templateId: String, state: FormState) {
        states[templateId] = state
        scope.launch {
            formStateManager.saveState(documentId, form.id, templateId, state)
            recalculateDependentCalculated(templateId)
        }
    }

    private suspend fun recalculateDependentCalculated(changedTemplateId: String) {
        form.templates.filterIsInstance<Template.Calculated>().forEach { template ->
            if (changedTemplateId in extractFormulaIds(template.formula)) {
                val result = if (template.isSumFormula()) {
                    val (sumComponentId1, sumComponentId2) = extractBracesContent(template.formula)

                    val tableState = states["${documentId}_${form.id}_${template.id}"] as? TableState ?: return
                    val sumColumn1Idx = form.templates.indexOfFirst { it.id == sumComponentId1 }
                    val sumColumn2Idx = form.templates.indexOfFirst { it.id == sumComponentId2 }
                } else {

                }
//                val newState = CalculatedState(result)
//                states[template.id] = newState
//                formStateManager.saveState(documentId, form.id.toString(), template.id, newState)
            }
        }
    }

    private fun extractFormulaIds(formula: String): List<String> {
        val regex = Regex("\\{([^}]+)}")
        return regex.findAll(formula).map { it.groupValues[1] }.toList()
    }
}

fun evaluateFormula(
    formula: String,
    getVars: (id: String) -> List<Float>,
    getVar: (id: String) -> Float?
): Float {
    var processedFormula = formula

    // Обрабатываем суммы переменных вида {∑n({varX} * {varY})}
    val sumPattern = Regex("\\{∑n\\(\\{(\\w+)\\}\\s*\\*\\s*\\{(\\w+)\\}\\)\\}")
    processedFormula = sumPattern.replace(processedFormula) { matchResult ->
        val first = getVars(matchResult.groupValues[1])
        val second = getVars(matchResult.groupValues[2])
        first.zip(second).sumOf { (a, b) -> a * b.toDouble() }.toString()
    }

    // Заменяем обычные переменные вида {varX} на их значения
    val varPattern = Regex("\\{(\\w+)\\}")
    processedFormula = varPattern.replace(processedFormula) { matchResult ->
        getVar(matchResult.groupValues[1]).toString()
    }

    // Вычисляем выражение
    return calculateExpression(processedFormula)
}

@Composable
fun rememberFormStates(
    documentId: Int,
    form: FormTemplate,
): FormStateHolder {
    val scope = rememberCoroutineScope()
    val formStateManager: FormStateManager = koinInject()
    return remember(documentId, form, scope) {
        FormStateHolder(documentId, form, formStateManager, scope).also {
            scope.launch {
                it.load()
            }
        }
    }
}

fun calculateExpression(expression: String): Float {
    val postfix = infixToPostfix(expression)
    return evaluatePostfix(postfix)
}

// Функция преобразования инфиксного выражения (обычная математическая запись) в постфиксную запись (обратная польская нотация)
fun infixToPostfix(expression: String): List<String> {
    val precedence = mapOf('+' to 1, '-' to 1, '*' to 2, '/' to 2, '(' to 0)
    val output = mutableListOf<String>()
    val operators = Stack<Char>()

    // Используем регулярное выражение для корректного разделения токенов
    val tokenPattern = Regex("\\d+(\\.\\d+)?|[()+\\-*/]")
    val tokens = tokenPattern.findAll(expression).map { it.value }.toList()

    if (tokens.isEmpty()) {
        throw IllegalArgumentException("Ошибка: пустое выражение")
    }

    for (token in tokens) {
        when {
            token.toFloatOrNull() != null -> output.add(token) // Число добавляем в выходную строку
            token == "(" -> operators.push(token[0]) // Открывающая скобка
            token == ")" -> { // Закрывающая скобка
                while (operators.isNotEmpty() && operators.peek() != '(') {
                    output.add(operators.pop().toString())
                }
                if (operators.isNotEmpty() && operators.peek() == '(') {
                    operators.pop() // Убираем "("
                } else {
                    throw IllegalArgumentException("Ошибка: несогласованные скобки")
                }
            }
            token in listOf("+", "-", "*", "/") -> { // Оператор
                while (operators.isNotEmpty() && precedence[operators.peek()]!! >= precedence[token[0]]!!) {
                    output.add(operators.pop().toString())
                }
                operators.push(token[0])
            }
            else -> throw IllegalArgumentException("Ошибка: недопустимый символ '$token'")
        }
    }

    while (operators.isNotEmpty()) {
        output.add(operators.pop().toString())
    }

    return output
}

// Функция вычисления выражения в постфиксной записи
fun evaluatePostfix(postfix: List<String>): Float {
    val stack = Stack<Float>()

    for (token in postfix) {
        when {
            token.toFloatOrNull() != null -> stack.push(token.toFloat())
            token in listOf("+", "-", "*", "/") -> {
                val right = stack.pop()
                val left = stack.pop()
                stack.push(applyOperator(left, right, token[0]))
            }
        }
    }

    return stack.pop()
}

fun applyOperator(left: Float, right: Float, op: Char): Float {
    return when (op) {
        '+' -> left + right
        '-' -> left - right
        '*' -> left * right
        '/' -> left / right
        else -> throw IllegalArgumentException("Неизвестная операция: $op")
    }
}

fun extractBracesContent(input: String): List<String> {
    val regex = "\\{([^{}]*)\\}".toRegex()
    return regex.findAll(input).map { it.groupValues[1] }.toList()
}

fun Float.roundToDecimals(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}