package com.shining.nbottombar.state

import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * SavedState.kt
 * WebHandler
 */
class BarSavedState : View.BaseSavedState {

    var prePosition = -1
    var position = 0

    constructor(superState: Parcelable?) : super(superState)

    private constructor(parcel: Parcel) : super(parcel) {
        prePosition = parcel.readInt()
        position = parcel.readInt()
    }

    companion object CREATOR : Parcelable.Creator<BarSavedState> {
        override fun createFromParcel(parcel: Parcel): BarSavedState {
            return BarSavedState(parcel)
        }

        override fun newArray(size: Int): Array<BarSavedState?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(prePosition)
        parcel.writeInt(position)
    }

    override fun describeContents(): Int = 0

}