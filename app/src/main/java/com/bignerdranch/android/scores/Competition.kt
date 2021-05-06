package com.bignerdranch.android.scores

import com.google.gson.annotations.SerializedName

class Competition {

    @SerializedName("competitors")
    lateinit var teams: List<TeamInfo>

}
