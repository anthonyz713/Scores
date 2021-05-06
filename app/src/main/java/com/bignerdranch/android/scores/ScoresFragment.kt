package com.bignerdranch.android.scores

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

private const val TAG = "ScoresFragment"

class ScoresFragment : Fragment(), DatePickerFragment.Callbacks {

    private lateinit var scoreRecyclerView: RecyclerView
    private lateinit var dateButton: Button
    private lateinit var selectedDate: Date

    private var adapter: ScoreAdapter? = ScoreAdapter(emptyList())

    private val scoresViewModel: ScoresViewModel by lazy {
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
        dateButton = view.findViewById(R.id.date_button) as Button

        var calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        selectedDate = calendar.time

        //Log.d(TAG, selectedDate.toString())

        dateButton.setOnClickListener{
            DatePickerFragment.newInstance(selectedDate).apply {
                setTargetFragment(this@ScoresFragment, 1)
                show(this@ScoresFragment.requireFragmentManager(), "hi")
            }
        }
        var c = Calendar.getInstance()
        dateButton.text = "${c.get(Calendar.MONTH) + 1}/${c.get(Calendar.DAY_OF_MONTH)}/${c.get(Calendar.YEAR)}"

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scoresViewModel.scoresLiveData.observe(
            viewLifecycleOwner,
            Observer { scores ->
                Log.d(TAG, "Have gallery items from ViewModel $scores")
                scoreRecyclerView.adapter = ScoreAdapter(scores)
            })
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

        private lateinit var gameEvent: GameEvent

        private val team1TextView: TextView = itemView.findViewById(R.id.team_1)
        private val score1TextView: TextView = itemView.findViewById(R.id.score_1)
        private val team2TextView: TextView = itemView.findViewById(R.id.team_2)
        private val score2TextView: TextView = itemView.findViewById(R.id.score_2)
        private val dateTextView: TextView = itemView.findViewById(R.id.date_text)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(gameEvent: GameEvent) {
            this.gameEvent = gameEvent

            val team1 = this.gameEvent.competitions[0].teams.get(0)
            val team2 = this.gameEvent.competitions[0].teams.get(1)

            val dateString = this.gameEvent.date
            val dateParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
            dateParser.timeZone = TimeZone.getTimeZone("UTC")
            val date = dateParser.parse(dateString)
            val c = Calendar.getInstance()
            c.time = date

            val hour = c.get(Calendar.HOUR)
            val hourString = when(hour){
                0 -> "12"
                else -> hour
            }

            val minute = c.get(Calendar.MINUTE)
            val minuteString = when(minute){
                in 0..9 -> "0$minute"
                else -> minute
            }

            val amPm = c.get(Calendar.AM_PM)
            val amPmString = when(amPm){
                0 -> "AM"
                else -> "PM"
            }

            val link = this.gameEvent.links[0].href
            //Log.d(TAG, link)
            //Log.d(TAG, date.toString())

            team1TextView.text = team1.team.name
            score1TextView.text = team1.score.toString()
            team2TextView.text = team2.team.name
            score2TextView.text = team2.score.toString()
            if (team1.winner)
                team1TextView.setTypeface(team1TextView.typeface, Typeface.BOLD)
            else if(team2.winner)
                team2TextView.setTypeface(team2TextView.typeface, Typeface.BOLD)
            dateTextView.text = "$hourString:$minuteString $amPmString"
        }

        override fun onClick(v: View?) {
        }

        //override fun onClick(v: View) {
          //  callbacks?.onCrimeSelected(crime.id)
        //}

    }

    private inner class ScoreAdapter(var scores: List<GameEvent>)
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

    override fun onDateSelected(date: Date) {

        selectedDate = date
        var calendar = Calendar.getInstance()
        calendar.time = selectedDate
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH) + 1
        var day = calendar.get(Calendar.DAY_OF_MONTH)
        var dateString = year.toString()
        if (month < 10)
            dateString += "0"
        dateString += month
        if (day < 10)
            dateString += "0"
        dateString += day

        dateButton.text = "${month}/${day}/${year}"

        scoresViewModel.updateScoresSpecificDate(dateString)
        scoresViewModel.scoresLiveData.observe(
                viewLifecycleOwner,
                Observer { scores ->
                    Log.d(TAG, "Have gallery items from ViewModel $scores")
                    scoreRecyclerView.adapter = ScoreAdapter(scores)
                })
    }

}

