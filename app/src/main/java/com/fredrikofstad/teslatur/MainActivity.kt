package com.fredrikofstad.teslatur

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private companion object{
        private const val TAG = "MainActivity"
    }

    val db = Firebase.firestore

    private lateinit var auth: FirebaseAuth
    private lateinit var btnTimes: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize Firebase Auth
        auth = Firebase.auth
        //UI elements
        btnTimes = findViewById(R.id.btnTime)
        // go to times
        val timesIntent = Intent(this, TimesActivity::class.java)
        btnTimes.setOnClickListener(){
            startActivity(timesIntent)
        }
    }

    fun onLatestListener(){
        val query = db.collection("times")
        val options = FirestoreRecyclerOptions.Builder<Tur>().setQuery(query, Tur::class.java)
            .setLifecycleOwner(this).build() // set activity as the query's lifetime
        val adapter = object: FirestoreRecyclerAdapter<Tur, TurViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurViewHolder {
                // can customize layout later
                val view = LayoutInflater.from(this@MainActivity)
                    .inflate(android.R.layout.simple_list_item_2, parent, false)
                return TurViewHolder(view)
            }

            override fun onBindViewHolder(holder: TurViewHolder, position: Int, model: Tur) {
                val tvName: TextView = holder.itemView.findViewById(android.R.id.text1)
                val tvDate: TextView = holder.itemView.findViewById(android.R.id.text2)
                tvName.text = model.displayName
                tvDate.text = model.timestamp.toDate().toString()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.miLogout){
            Log.i(TAG,"User logged out.")
            // Logout user
            auth.signOut()
            val logoutIntent = Intent(this, LoginActivity::class.java)
            // clear the backstack
            logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logoutIntent)

        }
        return super.onOptionsItemSelected(item)
    }

}