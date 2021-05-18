package com.bignerdranch.android.scores

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "ScoresFragment"

class ScoresFragment : Fragment(), DatePickerFragment.Callbacks {

    private lateinit var scoreRecyclerView: RecyclerView
    private lateinit var dateButton: Button
    private lateinit var selectedDate: Date
    private lateinit var noGames: TextView
    private val mainHandler: Handler = Handler(Looper.getMainLooper())


    private var adapter: ScoreAdapter? = ScoreAdapter(emptyList())

    private val scoresViewModel: ScoresViewModel by lazy {
        ViewModelProviders.of(this).get(ScoresViewModel::class.java)
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
        noGames = view.findViewById(R.id.no_games)

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
                show(this@ScoresFragment.requireFragmentManager(), TAG)
            }
        }
        var c = Calendar.getInstance()
        dateButton.text = "${c.get(Calendar.MONTH) + 1}/${c.get(Calendar.DAY_OF_MONTH)}/${c.get(Calendar.YEAR)}"

        return view
    }

    override fun onStart() {
        super.onStart()
        mainHandler.post(object : Runnable {
            override fun run() {
                updateScores(selectedDate)
                mainHandler.postDelayed(this, 5000)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        mainHandler.run { removeCallbacksAndMessages(null) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    private inner class ScoreHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var gameEvent: GameEvent

        private val team1TextView: TextView = itemView.findViewById(R.id.team_1)
        private val score1TextView: TextView = itemView.findViewById(R.id.score_1)
        private val team2TextView: TextView = itemView.findViewById(R.id.team_2)
        private val score2TextView: TextView = itemView.findViewById(R.id.score_2)
        private val dateTextView: TextView = itemView.findViewById(R.id.date_text)
        private val scheduleButton: Button = itemView.findViewById(R.id.schedule_button)

        init {
            itemView.setOnClickListener(this)
        }

        @RequiresApi(Build.VERSION_CODES.N)
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

            val hourString = when(val hour = c.get(Calendar.HOUR)){
                0 -> "12"
                else -> hour
            }

            val minuteString = when(val minute = c.get(Calendar.MINUTE)){
                in 0..9 -> "0$minute"
                else -> minute
            }

            val amPmString = when(c.get(Calendar.AM_PM)){
                0 -> "AM"
                else -> "PM"
            }

            //val link = this.gameEvent.links[0].href

            val period = this.gameEvent.status.period
            //val clock = this.gameEvent.status.clock

            val completed = this.gameEvent.status.type.completed

            //Log.d(TAG, "${this.gameEvent.status.type.statusName} ${this.gameEvent.status.type.statusID}")

            when {
                completed -> dateTextView.text = "Final"
                this.gameEvent.status.type.statusName == "STATUS_IN_PROGRESS" -> {
                    val displayClock = this.gameEvent.status.displayClock
                    dateTextView.text = "  Q$period\n$displayClock"
                }
                this.gameEvent.status.type.statusName == "STATUS_HALFTIME" -> dateTextView.text = "Halftime"
                this.gameEvent.status.type.statusName == "STATUS_END_PERIOD" -> dateTextView.text = "End of Qtr $period"
                this.gameEvent.status.type.statusName == "STATUS_SCHEDULED" -> {
                    dateTextView.text = "$hourString:$minuteString $amPmString"
                    scheduleButton.visibility = View.VISIBLE
                }
                //Log.d(TAG, link)
                //Log.d(TAG, date.toString())
            }
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

            scheduleButton.setOnClickListener{
                Toast.makeText(activity, "Reminder for ${this.gameEvent.shortName} set!", Toast.LENGTH_SHORT).show()
                scheduleButton.visibility = View.INVISIBLE

                //set notification
                ScoresActivity.createNotification(this.gameEvent, c)
            }

        }

        override fun onClick(v: View?) {
            //Log.d(TAG, gameEvent.links[0].gamePageUri.toString())
            val intent = Intent(Intent.ACTION_VIEW, gameEvent.links[0].gamePageUri)
            startActivity(intent)
        }

    }

    private inner class ScoreAdapter(var scores: List<GameEvent>)
        : RecyclerView.Adapter<ScoreHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : ScoreHolder {

            val view = layoutInflater.inflate(R.layout.list_item_game, parent, false)
            return ScoreHolder(view)
        }

        override fun getItemCount() = scores.size

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onBindViewHolder(holder: ScoreHolder, position: Int) {
            val score = scores[position]
            holder.bind(score)
        }

    }

    private fun updateScores(date: Date) {

        var calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        //if today's date is selected, call update
        if (date == calendar.time)
            onDateSelected(date)
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
                    Log.d(TAG, "Have scores from ViewModel")
                    scoreRecyclerView.adapter = ScoreAdapter(scores)
                    noGames.visibility = when{
                        scores.isEmpty() -> View.VISIBLE
                        else -> View.GONE
                    }
                })

    }

    companion object {
        fun newInstance(): ScoresFragment {
            return ScoresFragment()
        }
    }

}

