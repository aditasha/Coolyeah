package com.papb.coolyeah.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.papb.coolyeah.Kelas
import com.papb.coolyeah.R
import com.papb.coolyeah.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var _binding: FragmentHomeBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    lateinit var recycler: RecyclerView
    lateinit var adapter: RecyclerViewAdapter
    lateinit var _kelasList: ArrayList<Kelas>
    lateinit var auth: FirebaseAuth
    var firebaseUser: FirebaseUser? = null
    private lateinit var database: DatabaseReference
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    var count: Int = 0
    lateinit var akun: Button
    lateinit var myClass: Button
    lateinit var txInfo: TextView
    lateinit var txUsername: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = Firebase.database.reference
        auth = Firebase.auth
        firebaseUser = auth.currentUser

//        akun = findViewById(R.id.akun)
//        myClass = findViewById(R.id.list_kelas)
//        txInfo = findViewById(R.id.info)
//        txUsername = findViewById(R.id.title_username)


        txInfo = binding.info
        txUsername = binding.titleUsername


//        akun.setOnClickListener(this)
//        myClass.setOnClickListener(this)

        loadData()

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    private fun initRecyclerView() {
//        recycler = findViewById<RecyclerView>(R.id.recyclerView)
        recycler = binding.recyclerView
        adapter = RecyclerViewAdapter(_kelasList, requireContext())
        recycler.adapter = adapter
        recycler.layoutManager = GridLayoutManager(requireContext(), 1)
        if (_kelasList.isEmpty()) {
            txInfo.text = "Belum ada kelas lagi!"
        }
        else {
            txInfo.text = ""
        }
    }


    private fun loadData() {
        _kelasList = ArrayList()
        var kelasRef = database.child("kelas")
        var userRef = database.child("users/${firebaseUser?.uid}")

        val tambahKelas = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _kelasList.clear()
                for (kelas in dataSnapshot.children) {
                    if (kelas.child("users/${firebaseUser?.uid}").exists()) {
                        continue
                    }
                    else {
                        var nama = kelas.child("nama_kelas").value
                        var desc = kelas.child("deskripsi").value
                        var pengajar = kelas.child("pengajar").value
                        var image = kelas.child("image").value
                        var akronim = kelas.child("akronim").value
                        _kelasList.add(Kelas("$nama", "$desc", "$pengajar", "$image", "$akronim"))
                    }
                }
                initRecyclerView()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "tambahKelas:onCancelled", databaseError.toException())
            }
        }
        kelasRef.addValueEventListener(tambahKelas)
        val titleUsername = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("uid").value.toString() == firebaseUser?.uid) {
                    txUsername.text = snapshot.child("username").value.toString()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "txUsername:onCancelled", databaseError.toException())
            }
        }
        userRef.addValueEventListener(titleUsername)
    }


//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

}