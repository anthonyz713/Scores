package com.bignerdranch.android.scores

import android.util.Log
import com.google.gson.annotations.SerializedName

data class StatusType(
        @SerializedName("id")
        var statusID: Int,
        @SerializedName("name")
        var statusName: String,
        var completed: Boolean
) : Comparable<StatusType> {
        override fun compareTo(other: StatusType): Int {
                //Log.d("StatusType", "$completed ${other.completed}")
                val x = when{
                        completed && other.completed -> 0
                        completed -> 1
                        other.completed -> -1
                        else -> 0
                }
                //Log.d("StatusType", x.toString())
                return x
        }
}
