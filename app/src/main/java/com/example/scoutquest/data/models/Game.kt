package com.example.scoutquest.data.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Game(
    @DocumentId
    val gameId: String = "",
    val creatorId: String = "",
    val creatorEmail: String = "",
    val name: String = "",
    val description: String = "",
    val tasks: List<Task> = emptyList(),
    val isPublic: Boolean = false,

    //to implement in future:
    val playCount: Int? = null,
    val rating:@RawValue Rating? = null
): Parcelable
