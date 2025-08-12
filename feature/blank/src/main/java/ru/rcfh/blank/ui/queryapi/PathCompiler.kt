package ru.rcfh.blank.ui.queryapi

object PathCompiler {
    fun compile(path: String): List<Token> {
        if (!path.startsWith("$")) return emptyList()
        
        val tokens = mutableListOf<Token>()
        var current = path.substring(1)
        
        while (current.isNotEmpty()) {
            when {
                current.startsWith(".") -> {
                    current = current.substring(1)
                    val key = current.takeWhile { it.isLetterOrDigit() || it == '_' }
                    if (key.isNotEmpty()) {
                        tokens.add(ObjectToken(key))
                        current = current.substring(key.length)
                    }
                }
                current.startsWith("[") -> {
                    val endBracket = current.indexOf("]")
                    if (endBracket != -1) {
                        val indexStr = current.substring(1, endBracket)
                        val index = indexStr.toIntOrNull()
                        if (index != null) {
                            tokens.add(ArrayToken(index))
                            current = current.substring(endBracket + 1)
                        } else break
                    } else break
                }
                else -> break
            }
        }
        
        return tokens
    }
}