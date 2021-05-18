package com.bignerdranch.android.scores

import android.util.Log

class GameEvent{
    lateinit var date: String
    lateinit var shortName: String
    lateinit var competitions: List<Competition>
    lateinit var links: List<Link>
    lateinit var status: GameStatus
}
