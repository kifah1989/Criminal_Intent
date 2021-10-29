package com.example.criminalintent

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {
    /**
     * Required interface for hosting activities
     */
    interface Callbacks {
        fun onCrimeSelected(crime: Crime) {
        }
        fun newCrime(){

        }
    }

    private var callbacks: Callbacks? = null
    private lateinit var viewModel: CrimeListViewModel
    private lateinit var viewModelDetail: CrimeDetailViewModel
    private lateinit var crimeRecyclerView: RecyclerView
    private lateinit var noCrimeText: TextView
    private lateinit var addCrime: Button
    private lateinit var auth: FirebaseAuth

    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        noCrimeText = view.findViewById(R.id.no_crimes) as TextView
        addCrime = view.findViewById(R.id.add_crime) as Button
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter
        return view
    }

    private val observerCrimes = Observer<List<Crime>> {
        Log.d(TAG, "Got crimes ${it.size}")
        updateUI(it)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CrimeListViewModel::class.java)
        viewModel.fetchCrimes()
        viewModel.crimeListLiveData.observe(viewLifecycleOwner, observerCrimes)


    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                callbacks?.newCrime()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(crimes: List<Crime>) {
        if (crimes.isEmpty()) {
            noCrimeText.text = getText(R.string.no_crimes_available)
            addCrime.setOnClickListener {
                callbacks?.newCrime()
            }
        } else {
            noCrimeText.visibility =
                View.GONE
            addCrime.visibility = View.GONE
        }
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    private abstract class CrimeHolder(view: View) : RecyclerView.ViewHolder(view) {

        var crime = Crime()
        val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
    }

    private inner class NormalCrimeHolder(view: View) : CrimeHolder(view), View.OnClickListener {
        val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            val date = DateFormat.format("EEE dd MMM yyyy", this.crime.date?.toDate())
            val time = DateFormat.format("hh:mm", this.crime.time?.toDate())
            dateTextView.text = "${date} ${time}"
            solvedImageView.visibility = if (this.crime.isSolved!!) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        override fun onClick(v: View) {
            alertDialog(crime)
        }
    }

    private inner class SeriousCrimeHolder(view: View) : CrimeHolder(view), View.OnClickListener {
        val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)
        val contactPoliceButton: Button = itemView.findViewById(R.id.call_police)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            val date = DateFormat.format("EEE dd MMM yyyy", this.crime.date?.toDate())
            val time = DateFormat.format("hh:mm", this.crime.time?.toDate())
            dateTextView.text = "${date} ${time}"
            solvedImageView.visibility = if (this.crime.isSolved!!) {
                View.VISIBLE
            } else {
                View.GONE
            }

            contactPoliceButton.isEnabled = !crime.isSolved!!
            contactPoliceButton.setOnClickListener {
                Toast.makeText(context, "calling 911", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onClick(v: View) {
            alertDialog(crime)
        }
    }

    private inner class CrimeAdapter(var crimes: List<Crime>) :
        ListAdapter<Crime, CrimeHolder>(DiffCallback) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : CrimeHolder {
            return when (viewType) {
                0 -> {
                    val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                    NormalCrimeHolder(view)
                }
                else -> {
                    val view = layoutInflater.inflate(R.layout.list_item_require_police_crime, parent, false)
                    SeriousCrimeHolder(view)
                }
            }
        }

        override fun getItemCount() = crimes.size
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            when (holder) {
                is NormalCrimeHolder -> holder.bind(crime)
                is SeriousCrimeHolder -> holder.bind(crime)
                else -> throw IllegalArgumentException()
            }
        }

        override fun getItemViewType(position: Int): Int {
            val crime = crimes[position]
            return when (crime.requiresPolice) {
                true -> 1
                else -> 0
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }
    }

    private fun alertDialog(crime: Crime) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogitem = arrayOf<CharSequence>("Edit Data", "Delete Data")
        builder.setTitle(crime.title)
        builder.setItems(dialogitem) { _, i: Int ->
            if (i == 0) {
                callbacks?.onCrimeSelected(crime)
            } else
                if (i == 1) {
                    viewModel.deleteCrime(crime.uid!!)
                    viewModel.fetchCrimes()
                }
        }
            .show()

    }
}

