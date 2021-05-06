package com.bignerdranch.android.scores

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ScoreApi {

    @GET("basketball/nba/scoreboard")
    fun fetchScores(): Call<ScoreResponse>

    @GET("basketball/nba/scoreboard")
    fun fetchScoresSpecificDay(
            @Query("dates") date: String
    ): Call<ScoreResponse>
}