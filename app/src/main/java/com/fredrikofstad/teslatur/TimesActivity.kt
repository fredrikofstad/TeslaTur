package com.fredrikofstad.teslatur

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TimesActivity : AppCompatActivity() {

    val db = Firebase.firestore

    private lateinit var rvTur: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_times)

        rvTur = findViewById(R.id.rvTur)

        val query = db.collection("times")
        val options = FirestoreRecyclerOptions.Builder<Tur>().setQuery(query, Tur::class.java)
            .setLifecycleOwner(this).build() // set activity as the query's lifetime
        val adapter = object: FirestoreRecyclerAdapter<Tur, TurViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurViewHolder {
                // can customize layout later
                val view = LayoutInflater.from(this@TimesActivity)
                    .inflate(android.R.layout.simple_list_item_2, parent, false)
                return TurViewHolder(view)
            }

            override fun onBindViewHolder(holder: TurViewHolder, position: Int, model: Tur) {
                val tvName: TextView = holder.itemView.findViewById(android.R.id.text1)
                val tvDate: TextView = holder.itemView.findViewById(android.R.id.text2)
                tvName.text = model.displayName
                tvDate.text = model.timestamp.toString()
            }
        }
        rvTur.adapter = adapter
        rvTur.layoutManager = LinearLayoutManager(this)
    }
}