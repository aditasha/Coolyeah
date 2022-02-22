package com.papb.coolyeah.detailKelas

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.papb.coolyeah.GlideApp
import com.papb.coolyeah.Materi
import com.papb.coolyeah.NavDraw
import com.papb.coolyeah.R

class DetailKelas : AppCompatActivity(), View.OnClickListener{
    lateinit var recycler: RecyclerView
    lateinit var adapter: RecyclerViewAdapter
    private lateinit var database: DatabaseReference
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private var tag: String? = null
    private var from: String? = null
    private lateinit var bgDetail: ImageView
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var pengajar: TextView
    private lateinit var btDetailKelas: Button
    private lateinit var akronim: String
    private lateinit var _materiList: ArrayList<Materi>
    lateinit var txInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_kelas)

        var intent = intent
        tag = intent.getStringExtra("nama").toString()
        from = intent.getStringExtra("from").toString()

        bgDetail = findViewById(R.id.detail_bg)
        title = findViewById(R.id.detail_title)
        description = findViewById(R.id.detail_description)
        pengajar = findViewById(R.id.detail_pengajar)
        btDetailKelas = findViewById(R.id.btDetailKelas)
        txInfo = findViewById(R.id.txInfo)
        btDetailKelas.setOnClickListener(this)

        database = Firebase.database.reference
        auth = Firebase.auth
        user = auth.currentUser

        loadData()
    }

    private fun initRecyclerView() {
        recycler = findViewById(R.id.recyclerView)
        adapter = RecyclerViewAdapter(_materiList, this)
        recycler.adapter = adapter
        recycler.layoutManager = GridLayoutManager(this, 1)
        if (_materiList.isEmpty()) {
            txInfo.text = "Belum ada materi lagi!"
        }
        else {
            txInfo.text = ""
        }
    }

    private fun loadData() {
        var kelasRef = database.child("kelas")
        kelasRef.get().addOnSuccessListener {
            for (kelas in it.children) {
                if (kelas.child("nama_kelas").value.toString() == tag) {
                    title.text = kelas.child("nama_kelas").value.toString()
                    description.text = kelas.child("deskripsi").value.toString()
                    pengajar.text = kelas.child("pengajar").value.toString()
                    akronim = kelas.child("akronim").value.toString()
                    GlideApp.with(this)
                        .load(storageRef.child("${kelas.child("image").value}"))
                        .into(bgDetail)
                }
            }
            checkIntent()
        }.addOnFailureListener {
            Log.d("failed", "FAILED")
        }
    }

    private fun loadMateri() {
        _materiList = ArrayList()
        var materiRef = database.child("kelas/$akronim/materi")
        var listMateri = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _materiList.clear()
                for (materi in snapshot.children) {
                    var nama = materi.child("nama-materi").value.toString()
                    var desc = materi.child("desc").value.toString()
                    var pengajar = pengajar.text
                    var key = materi.key
                    _materiList.add(Materi(nama, desc, pengajar, akronim, key))
                }
                initRecyclerView()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "listMateri:onCancelled", databaseError.toException())
            }
        }
        materiRef.addValueEventListener(listMateri)
    }

    private fun checkIntent() {
        if (from == "tambah kelas") {
            btDetailKelas.text = "GABUNG"
        }
        else if (from == "list kelas") {
            loadMateri()
            btDetailKelas.text = "UNENROLL"
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btDetailKelas -> {
                var kelasRef = database.child("kelas/${akronim}")
                var userRef = database.child("users/${user?.uid}")

                if (from == "tambah kelas") {
                    userRef.child("kelas/${title.text}")
                        .setValue(true)
                    kelasRef.child("users/${user?.uid}")
                        .setValue(true)

                    Toast.makeText(this, "Anda telah terdaftar pada kelas ${title.text}", Toast.LENGTH_SHORT).show()
                    var intent = Intent(this, NavDraw::class.java)
                    startActivity(intent)
                }
                else if (from == "list kelas") {
                    userRef.child("kelas/${title.text}")
                        .setValue(null)
                    kelasRef.child("users/${user?.uid}")
                        .setValue(null)

                    Toast.makeText(this, "Anda telah unenroll kelas ${title.text}", Toast.LENGTH_SHORT).show()
                    var intent = Intent(this, NavDraw::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}