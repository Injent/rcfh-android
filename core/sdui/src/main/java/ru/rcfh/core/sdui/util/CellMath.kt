package ru.rcfh.core.sdui.util

import java.util.Locale
import java.util.Stack

fun Float.format(places: Int): String {
    return "%.${places}f".format(Locale.US, this)
}

fun evaluateDotProductFormula(productA: List<Float>, productB: List<Float>): String {
    if (productA.size != productB.size) {
        return "ERROR"
    }

    val dotSum = productA.zip(productB).sumOf { (a, b) -> a * b.toDouble() }
    return (dotSum / 10f).toFloat().format(2)
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

private fun infixToPostfix(expression: String): List<String> {
    val precedence = mapOf('+' to 1, '-' to 1, '*' to 2, '/' to 2, '(' to 0)
    val output = mutableListOf<String>()
    val operators = Stack<Char>()

    val tokenPattern = Regex("\\d+(\\.\\d+)?|[()+\\-*/]")
    val tokens = tokenPattern.findAll(expression).map { it.value }.toList()

    if (tokens.isEmpty()) {
        throw IllegalArgumentException("Ошибка: пустое выражение")
    }

    for (token in tokens) {
        when {
            token.toFloatOrNull() != null -> output.add(token)
            token == "(" -> operators.push(token[0])
            token == ")" -> {
                while (operators.isNotEmpty() && operators.peek() != '(') {
                    output.add(operators.pop().toString())
                }
                if (operators.isNotEmpty() && operators.peek() == '(') {
                    operators.pop()
                } else {
                    throw IllegalArgumentException("Ошибка: несогласованные скобки")
                }
            }
            token in listOf("+", "-", "*", "/") -> {
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