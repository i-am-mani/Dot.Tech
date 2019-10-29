package com.omega.dottech2k20



import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_events.*

import com.ramotion.cardslider.CardSliderLayoutManager

class EventsFragment : Fragment() {

    val mFirestore = FirebaseFirestore.getInstance()
    val TAG = "FIRESTORE"
    lateinit var holder: FirestoreRecyclerAdapter<Event?, EventsHolder?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_events, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRV()
    }

    private fun initRV() {
        val snapshotParser = SnapshotParser { it: DocumentSnapshot ->
            val event = it.toObject(Event::class.java)!!
            event.id = it.id
            return@SnapshotParser event
        }
        val reference: Query = mFirestore.collection("Events")
        reference.get().addOnCompleteListener(OnCompleteListener {
            Log.d(TAG,"Reply data")
            if(it.isSuccessful){
                val querySnapshot = it.getResult()
                val list = querySnapshot?.toObjects(Event::class.java)
                Log.d(TAG,"Fetched data = $list" + list?.size )
            }

        })
        val options: FirestoreRecyclerOptions<Event?> =
            FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(reference, snapshotParser)
                .build()
         holder = object :
            FirestoreRecyclerAdapter<Event?, EventsHolder?>(options) {
            override fun onBindViewHolder(holder: EventsHolder, position: Int, model: Event) {
               holder.onBind(model.thumbNail!!)
                Log.d(TAG,model.toString())
            }

            override fun onCreateViewHolder(group: ViewGroup, i: Int): EventsHolder {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                Log.d(TAG,"ItemView Created")
                val view: View = LayoutInflater.from(group.context).inflate(R.layout.adapter_events, group, false)
                return EventsHolder(view)
            }
        }

        rv_event_thumb_nails.adapter = holder
        rv_event_thumb_nails.layoutManager = CardSliderLayoutManager(context!!)
        holder.startListening()
    }

    override fun onStop() {
        super.onStop()
        holder.stopListening()
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            EventsFragment()
    }
}
