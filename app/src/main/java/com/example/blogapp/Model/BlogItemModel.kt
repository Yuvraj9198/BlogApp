package com.example.blogapp.Model

import android.os.Parcel
import android.os.Parcelable

data class BlogItemModel(
    var heading: String? = null,
    val userName: String? = null,
    val date: String? = null,
    var post: String? = null,
    var likeCount: Int = 0,
    val userId: String? = null,
    val profileImage: String? = null,
    var isSaved: Boolean = false,
    var postId: String? = null,
    val likeBy: MutableList<String>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.createStringArrayList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(heading)
        parcel.writeString(userName)
        parcel.writeString(date)
        parcel.writeString(post)
        parcel.writeString(userId)
        parcel.writeInt(likeCount)
        parcel.writeString(profileImage)
        parcel.writeByte(if (isSaved) 1 else 0)
        parcel.writeString(postId)
        parcel.writeStringList(likeBy)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BlogItemModel> {
        override fun createFromParcel(parcel: Parcel): BlogItemModel {
            return BlogItemModel(parcel)
        }

        override fun newArray(size: Int): Array<BlogItemModel?> {
            return arrayOfNulls(size)
        }
    }
}
