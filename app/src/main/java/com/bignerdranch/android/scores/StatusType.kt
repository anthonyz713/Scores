package com.bignerdranch.android.scores

import com.google.gson.annotations.SerializedName

data class StatusType(
        @SerializedName("id")
        var statusID: Int,
        @SerializedName("name")
        var statusName: String,
        var completed: Boolean
) : Comparable<StatusType> {
        override fun compareTo(other: StatusType): Int {
                return when(statusName){
                        "STATUS_FINAL" -> when(other.statusName){
                                "STATUS_FINAL" -> 0
                                else -> 1
                        }
                        else -> when(other.statusName){
                                "STATUS_FINAL" -> -1
                                else -> 0
                        }
                }
        }
}
