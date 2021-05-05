package com.bignerdranch.android.scores

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "CrimeListFragment"

class ScoresFragment : Fragment() {

    private lateinit var scoreRecyclerView: RecyclerView

    private var adapter: ScoreAdapter? = ScoreAdapter(mutableListOf(Score("hi",1,"t2",3)))

    private val crimeListViewModel: ScoresViewModel by lazy {
        ViewModelProviders.of(this).get(ScoresViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scores_list, container, false)

        scoreRecyclerView =
                view.findViewById(R.id.scores_recycler_view) as RecyclerView
        scoreRecyclerView.layoutManager = LinearLayoutManager(context)
        scoreRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }*/

    /*
    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }*/

    private inner class ScoreHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var score: Score

        private val team1TextView: TextView = itemView.findViewById(R.id.team_1)
        private val score1TextView: TextView = itemView.findViewById(R.id.score_1)
        private val team2TextView: TextView = itemView.findViewById(R.id.team_2)
        private val score2TextView: TextView = itemView.findViewById(R.id.score_2)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(score: Score) {
            this.score = score
            team1TextView.text = this.score.team1
            score1TextView.text = this.score.score1.toString()
            team2TextView.text = this.score.team2
            score2TextView.text = this.score.score2.toString()
        }

        override fun onClick(v: View?) {
        }

        //override fun onClick(v: View) {
          //  callbacks?.onCrimeSelected(crime.id)
        //}

    }

    private inner class ScoreAdapter(var scores: List<Score>)
        : RecyclerView.Adapter<ScoreHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : ScoreHolder {
            val view = layoutInflater.inflate(R.layout.list_item_game, parent, false)
            return ScoreHolder(view)
        }

        override fun getItemCount() = scores.size

        override fun onBindViewHolder(holder: ScoreHolder, position: Int) {
            val score = scores[position]
            holder.bind(score)
        }

    }

    companion object {
        fun newInstance(): ScoresFragment {
            return ScoresFragment()
        }
    }

}

