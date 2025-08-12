package ru.rcfh.blank.ui.queryapi

import ru.rcfh.blank.ui.state.ArrayElement
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.blank.ui.state.NullElement
import ru.rcfh.blank.ui.state.ObjectElement
import ru.rcfh.blank.ui.state.TextElement

fun Element.query(path: String): Element? {
    val tokens = PathCompiler.compile(path)
    if (tokens.isEmpty()) return this

    var current = this

    for (token in tokens) {
        when (token) {
            is ObjectToken -> {
                if (current !is ObjectElement) return null
                if (!current.containsKey(token.key)) return null
                current = current[token.key]!!
            }
            is ArrayToken -> {
                if (current !is ArrayElement) return null
                if (token.index >= current.size) return null
                current = current[token.index]
            }
        }
    }

    return current.takeIf { it !is NullElement }
}

fun Element.update(path: String, value: Element?): Boolean {
    val tokens = PathCompiler.compile(path)
    if (tokens.isEmpty()) return false
    
    var current = this
    
    for (i in tokens.indices) {
        val token = tokens[i]
        val isLast = i == tokens.size - 1
        val nextToken = if (i + 1 < tokens.size) tokens[i + 1] else null
        
        when (token) {
            is ObjectToken -> {
                if (current !is ObjectElement) return false
                
                if (isLast) {
                    current[token.key] = value ?: NullElement(documentScope)
                    return true
                } else {
                    if (!current.containsKey(token.key)) {
                        if (nextToken is ArrayToken) {
                            current[token.key] = ArrayElement(documentScope = documentScope)
                        } else {
                            current[token.key] = ObjectElement(documentScope = documentScope)
                        }
                    } else if (nextToken is ArrayToken && current[token.key] !is ArrayElement) {
                        current[token.key] = ArrayElement(documentScope = documentScope)
                    } else if (nextToken is ObjectToken && current[token.key] !is ObjectElement) {
                        current[token.key] = ObjectElement(documentScope = documentScope)
                    }
                    current = current[token.key]!!
                }
            }
            is ArrayToken -> {
                if (current !is ArrayElement) return false
                
                if (isLast) {
                    while (current.size <= token.index) {
                        current.add(NullElement(documentScope = documentScope))
                    }
                    current[token.index] = value ?: NullElement(documentScope = documentScope)
                    return true
                } else {
                    while (current.size <= token.index) {
                        current.add(NullElement(documentScope = documentScope))
                    }
                    if (current[token.index] !is ObjectElement) {
                        current[token.index] = ObjectElement(documentScope = documentScope)
                    }
                    current = current[token.index]
                }
            }
        }
    }
    
    return false
}

fun Element.queryOrCreate(path: String, defaultValue: String): String {
    return queryOrCreate<Element>(path, TextElement(defaultValue, documentScope)).textOrEmpty
}

fun Element.queryOrCreate(path: String, defaultValue: Boolean): Boolean {
    return queryOrCreate(path, TextElement(defaultValue.toString(), documentScope)).textOrEmpty.toBooleanStrictOrNull() ?: false
}

fun Element.queryOrCreate(path: String, defaultValue: Number): Double {
    return queryOrCreate<Element>(path, TextElement(defaultValue.toString(), documentScope)).doubleOrZero
}

inline fun <reified T : Element> Element.queryOrCreate(path: String, defaultValue: T): T {
    val result = query(path)

    return if (result == null) {
        val value = when (defaultValue) {
            is ArrayElement -> defaultValue
            is ObjectElement -> defaultValue
            else -> defaultValue
        }
        update(path, value)
        value
    } else {
        result
    } as T
}

fun Element.queryOrDefault(path: String, defaultValue: String): String {
    return query(path)?.takeIf { it is TextElement }?.textOrEmpty ?: defaultValue
}

fun Element.queryOrDefault(path: String, defaultValue: Number): Double {
    return query(path)?.takeIf { it is TextElement }?.doubleOrZero ?: defaultValue.toDouble()
}

fun Element.update(path: String, value: String): Boolean {
    return update(path, TextElement(value, documentScope))
}

fun Element.update(path: String, value: Number): Boolean {
    return update(path, TextElement(value.toString(), documentScope))
}