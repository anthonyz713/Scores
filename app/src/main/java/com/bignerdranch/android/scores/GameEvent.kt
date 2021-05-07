package com.bignerdranch.android.scores

class GameEvent {
    lateinit var date: String
    lateinit var competitions: List<Competition>
    lateinit var links: List<Link>
    lateinit var status: GameStatus
}
