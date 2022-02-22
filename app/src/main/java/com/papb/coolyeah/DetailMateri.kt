package com.papb.coolyeah

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DetailMateri : AppCompatActivity(){
    private lateinit var database: DatabaseReference
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private var intentPengajar: String? = null
    private var intentKelas: String? = null
    private var intentKey: String? = null
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var pengajar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_materi)

        var intent = intent
        intentPengajar = intent.getStringExtra("pengajar").toString()
        intentKelas = intent.getStringExtra("akronim kelas").toString()
        intentKey = intent.getStringExtra("key").toString()

        title = findViewById(R.id.materi_title)
        description = findViewById(R.id.desc_materi)
        pengajar = findViewById(R.id.pengajar_materi)

        database = Firebase.database.reference
        auth = Firebase.auth
        user = auth.currentUser
        loadData()
    }

    private fun loadData() {
        var materiRef = database.child("kelas/$intentKelas/materi").orderByKey().equalTo(intentKey)
        materiRef.get().addOnSuccessListener {
            for (materi in it.children) {
                title.text = materi.child("nama-materi").value.toString()
                pengajar.text = intentPengajar
                description.text = materi.child("desc").value.toString()
            }
        }
    }
}