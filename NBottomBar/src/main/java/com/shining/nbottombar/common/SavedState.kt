package com.shining.nbottombar.common

import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * SavedState.kt
 * WebHandler
 */
class SavedState : View.BaseSavedState {

    var preIndex = -1
    var curIndex = 0

    constructor(superState: Parcelable) : super(superState)

    private constructor(parcel: Parcel) : super(parcel) {
        preIndex = parcel.readInt()
        curIndex = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(preIndex)
        parcel.writeInt(curIndex)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SavedState> {
        override fun createFromParcel(parcel: Parcel): SavedState {
            return SavedState(parcel)
        }

        override fun newArray(size: Int): Array<SavedState?> {
            return arrayOfNulls(size)
        }
    }
}