package ru.rcfh.blank.ui.queryapi

interface Token

data class ObjectToken(val key: String) : Token

data class ArrayToken(val index: Int) : Token