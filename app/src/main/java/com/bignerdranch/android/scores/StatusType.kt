package com.bignerdranch.android.scores

import com.google.gson.annotations.SerializedName

data class StatusType(
        @SerializedName("id")
        var statusID: Int,
        @SerializedName("name")
        var statusName: String,
        var completed: Boolean
)
