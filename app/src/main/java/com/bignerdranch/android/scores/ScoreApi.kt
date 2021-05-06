package com.bignerdranch.android.scores

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface ScoreApi {

    @GET("basketball/nba/scoreboard")
    fun fetchScores(): Call<ScoreResponse>

}