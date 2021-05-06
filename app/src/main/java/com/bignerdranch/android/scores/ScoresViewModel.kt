package com.bignerdranch.android.scores

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import java.util.*

class ScoresViewModel : ViewModel() {

    var scoresLiveData: LiveData<List<GameEvent>>

    private val scoresFetcher = ScoresFetcher()

    fun updateScoresSpecificDate(date: String) {
        scoresLiveData = scoresFetcher.fetchScoresSpecificDay(date)
    }

    init {
        scoresLiveData = scoresFetcher.fetchScores()
    }


}