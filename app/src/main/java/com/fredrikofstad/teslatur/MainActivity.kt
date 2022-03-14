package com.fredrikofstad.teslatur

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private companion object{
        private const val TAG = "MainActivity"
    }

    val db = Firebase.firestore

    private lateinit var auth: FirebaseAuth
    private lateinit var btnTimes: Button
    private lateinit var btnDepart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize Firebase Auth
        auth = Firebase.auth
        //UI elements
        btnTimes = findViewById(R.id.btnTime)
        btnDepart = findViewById(R.id.btnDepart)
        // go to times
        val timesIntent = Intent(this, TimesActivity::class.java)
        btnTimes.setOnClickListener(){
            startActivity(timesIntent)
        }
        btnDepart.setOnClickListener(){
            showAlertDialog()
        }
        // listener for change in document
        val docTeslaTime = db.collection("tesla").document("tur")
        docTeslaTime.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: ${snapshot.data}")
                val dateFormat = SimpleDateFormat("hh:mm")
                val sistUte = snapshot.getDate("sistUte")
                //val sistUte = snapshot.toObject<SistUte>()?.timestamp?.toDate()
                btnTimes.text = dateFormat.format(sistUte)

            } else {
                Log.d(TAG, "Current data: null")
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
    private fun alreadyGone(): Boolean{
        //tests if tesla needs to go on a trip
        return false
    }

    private fun showAlertDialog(){
        val dialog = AlertDialog.Builder(this)
            .setTitle("Tessetur")
            .setMessage(
                if(alreadyGone()){
                    "Tesla har allerede gått på tur, men vil alltid gå flere ganger!"
                } else {
                    "Er du klar for tessetur?"
                })
            .setPositiveButton("Aldri sur!", null)
            .setNegativeButton("Bare kødda", null)
            .show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
            Log.i(TAG, "Går ut med tess")
            val currentuser = auth.currentUser
            if(currentuser == null){
                Toast.makeText(this, "Ingen bruker pålogget",Toast.LENGTH_SHORT)
                return@setOnClickListener
            }
            // edit firestore document with time right now
            db.collection("tesla").document("tur").update("sistUte", Timestamp.now())
            // TODO: add document for who did what and when
            dialog.dismiss()
        }

    }

}