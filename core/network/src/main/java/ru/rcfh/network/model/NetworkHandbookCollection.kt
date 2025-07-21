package ru.rcfh.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkHandbookCollection(
    @SerialName("version")
    val version: Int,
    @SerialName("handbooks")
    val handbooks: List<Handbook>
) {
    @Serializable
    data class Handbook(
        @SerialName("id")
        val id: Int,
        @SerialName("name")
        val name: String,
        @SerialName("references")
        val references: List<Reference>
    ) {
        @Serializable
        data class Reference(
            @SerialName("id")
            val id: Int,
            @SerialName("name")
            val name: String,
            @SerialName("description")
            val description: String? = null,
            @SerialName("sign_codes")
            val signCodes: List<Int>? = null,
            @SerialName("code")
            val code: Int? = null
        )
    }
}