package com.dissy.lizkitchen.model

import android.os.Parcel
import android.os.Parcelable

data class User(
    val alamat: String? = "Belum diisi",
    val email: String? = "",
    val password: String? = "",
    val phoneNumber: String? = "",
    val userId: String? = "",
    val username: String? = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(alamat)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(phoneNumber)
        parcel.writeString(userId)
        parcel.writeString(username)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}