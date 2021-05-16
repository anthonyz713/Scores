package com.bignerdranch.android.scores

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "ScoresFetcher"

class ScoresFetcher {

    private val scoreApi: ScoreApi

    init {
        val client = OkHttpClient.Builder()
                .build()

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://site.api.espn.com/apis/site/v2/sports/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        scoreApi = retrofit.create(ScoreApi::class.java)
    }

    fun fetchScoresRequest(): Call<ScoreResponse> {
        return scoreApi.fetchScores()
    }

    fun fetchScores(): LiveData<List<GameEvent>> {
        return fetchScoreMetadata(fetchScoresRequest())
    }

    fun fetchScoresSpecificDayRequest(date: String): Call<ScoreResponse> {
        return scoreApi.fetchScoresSpecificDay(date)
    }

    fun fetchScoresSpecificDay(date: String): LiveData<List<GameEvent>> {
        return fetchScoreMetadata(fetchScoresSpecificDayRequest(date))
    }

    private fun fetchScoreMetadata(scoreRequest: Call<ScoreResponse>)
            : LiveData<List<GameEvent>> {
        val responseLiveData: MutableLiveData<List<GameEvent>> = MutableLiveData()
        Log.d(TAG, scoreRequest.request().url().toString())

        scoreRequest.enqueue(object : Callback<ScoreResponse> {

            override fun onFailure(call: Call<ScoreResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch scores", t)
            }

            override fun onResponse(
                call: Call<ScoreResponse>,
                response: Response<ScoreResponse>
            ) {
                Log.d(TAG, "Response received")
                val scoreResponse: ScoreResponse? = response.body()
                var scores: List<GameEvent> = scoreResponse?.events
                    ?: mutableListOf()
                scores.sortedBy {
                    it.status.type
                }

                responseLiveData.value = scores
            }
        })
        return responseLiveData
    }

}