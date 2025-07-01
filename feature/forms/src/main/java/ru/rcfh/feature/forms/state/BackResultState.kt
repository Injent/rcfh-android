package ru.rcfh.feature.forms.state

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.SavedStateHandle

data class BackResultState(
    private val resultKey: String,
    private val value: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        resultKey = parcel.readString() ?: "",
        value = parcel.readString() ?: ""
    )

    fun get(key: String): String? {
        return if (resultKey == key) value
        else null
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(resultKey)
        parcel.writeString(value)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BackResultState> {
        override fun createFromParcel(parcel: Parcel): BackResultState {
            return BackResultState(parcel)
        }

        override fun newArray(size: Int): Array<BackResultState?> {
            return arrayOfNulls(size)
        }
    }
}

val LocalSavedStateHandle = staticCompositionLocalOf<SavedStateHandle> {
    error("SavedStateHandle not provided")
}