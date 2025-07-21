package ru.rcfh.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class ReferenceWithMatchInfo(
    @Embedded
    val reference: ReferenceEntity,
    @ColumnInfo("matchInfo")
    val matchInfo: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReferenceWithMatchInfo

        if (reference != other.reference) return false
        if (!matchInfo.contentEquals(other.matchInfo)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = reference.hashCode()
        result = 31 * result + matchInfo.contentHashCode()
        return result
    }

    val rank: Double
        get() = calculateScore(matchInfo)
}

private fun calculateScore(matchInfo: ByteArray): Double {
    val info = matchInfo.toIntArray()

    val numPhrases = info[0]
    val numColumns = info[1]

    var score = 0.0
    for (phrase in 0 until numPhrases) {
        val offset = 2 + phrase * numColumns * 3
        for (column in 0 until numColumns) {
            val numHitsInRow = info[offset + 3 * column]
            val numHitsInAllRows = info[offset + 3 * column + 1]
            if (numHitsInAllRows > 0) {
                score += numHitsInRow.toDouble() / numHitsInAllRows.toDouble()
            }
        }
    }

    return score
}

private fun ByteArray.toIntArray(skipSize: Int = 4): IntArray {
    val cleanedArr = IntArray(this.size / skipSize)
    for ((pointer, i) in (this.indices step skipSize).withIndex()) {
        cleanedArr[pointer] = this[i].toInt()
    }

    return cleanedArr
}