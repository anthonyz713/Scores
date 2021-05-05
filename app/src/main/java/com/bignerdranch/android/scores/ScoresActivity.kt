package com.bignerdranch.android.scores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

private const val TAG = "ScoresActivity"

class ScoresActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scores)

        val currentFragment =
                supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = ScoresFragment.newInstance()
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit()
        }
    }

}