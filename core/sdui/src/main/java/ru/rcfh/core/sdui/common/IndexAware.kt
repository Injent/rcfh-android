package ru.rcfh.core.sdui.common

interface IndexAware {
    val mIndex: Int
    fun updateIndex(index: Int)
}