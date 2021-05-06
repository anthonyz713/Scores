package com.bignerdranch.android.scores

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class ScoresViewModel : ViewModel() {

    val scoresLiveData: LiveData<List<GameEvent>>

    private val scoresFetcher = ScoresFetchr()

    init {
        scoresLiveData = scoresFetcher.fetchScores()
    }


}