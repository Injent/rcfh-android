package ru.rcfh.feature.forms.util

import java.util.Stack

internal fun String.extractFormulaIds(): List<String> {
    val regex = Regex("\\{(.*?)\\}")
    return regex.findAll(this).map { it.groupValues[1] }.toList()
}

fun evaluateSimpleFormula(formula: String, values: Map<String, Float>): Float {
    var processedFormula = formula
    val varPattern = Regex("\\{(\\w+)\\}")
    processedFormula = varPattern.replace(processedFormula) { matchResult ->
        values[matchResult.groupValues[1]]?.toString() ?: "0"
    }
    return calculateExpression(processedFormula)
}

private fun calculateExpression(expression: String): Float {
    val postfix = infixToPostfix(expression)
    return evaluatePostfix(postfix)
}

// Функция преобразования инфиксного выражения (обычная математическая запись) в постфиксную запись (обратная польская нотация)
private fun infixToPostfix(expression: String): List<String> {
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
private fun evaluatePostfix(postfix: List<String>): Float {
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

private fun applyOperator(left: Float, right: Float, op: Char): Float {
    return when (op) {
        '+' -> left + right
        '-' -> left - right
        '*' -> left * right
        '/' -> left / right
        else -> throw IllegalArgumentException("Неизвестная операция: $op")
    }
}

private fun extractBracesContent(input: String): List<String> {
    val regex = "\\{([^{}]*)\\}".toRegex()
    return regex.findAll(input).map { it.groupValues[1] }.toList()
}