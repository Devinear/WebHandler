package com.shining.nbottombar.SavedState

import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * ItemSavedState.kt
 * WebHandler
 *
 * Created by Chahyung-Lee on 2021/10/19.
 * Copyright © 2021 NHN COMMERCE Corp. All rights reserved.
 */
class ItemSavedState : View.BaseSavedState {

    lateinit var text: String
    var padding = 0f
    var iconNormal = 0
    var iconSelected = 0
    var textSize = 0f
    var isSelected = false

    constructor(superState: Parcelable?) : super(superState)

    constructor(parcel: Parcel) : super(parcel) {
        text = parcel.readString().toString()
        padding = parcel.readFloat()
        iconNormal = parcel.readInt()
        iconSelected = parcel.readInt()
        textSize = parcel.readFloat()
        isSelected = parcel.readByte() != 0.toByte()
    }

    companion object CREATOR : Parcelable.Creator<ItemSavedState> {
        override fun createFromParcel(parcel: Parcel): ItemSavedState {
            return ItemSavedState(parcel)
        }

        override fun newArray(size: Int): Array<ItemSavedState?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(text)
        parcel.writeFloat(padding)
        parcel.writeInt(iconNormal)
        parcel.writeInt(iconSelected)
        parcel.writeFloat(textSize)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int = 0

}