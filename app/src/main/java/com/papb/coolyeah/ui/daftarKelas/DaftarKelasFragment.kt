package com.papb.coolyeah.ui.daftarKelas

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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
import com.papb.coolyeah.databinding.FragmentDaftarkelasBinding


class DaftarKelasFragment : Fragment() {

    private lateinit var DaftarKelasView: DaftarKelasViewModel
    private lateinit var _binding: FragmentDaftarkelasBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    lateinit var recycler: RecyclerView
    lateinit var adapter: RecyclerViewAdapter
    lateinit var _kelasList: ArrayList<Kelas>
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private lateinit var txInfo: TextView
    lateinit var txUsername: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        DaftarKelasView =
            ViewModelProvider(this).get(DaftarKelasViewModel::class.java)

        _binding = FragmentDaftarkelasBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textGallery
//        galleryViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        database = Firebase.database.reference
        auth = Firebase.auth
        user = auth.currentUser

//        txInfo = findViewById(R.id.txInfo)
        txInfo = binding.txInfo
//        txUsername = findViewById(R.id.title_username2)

        loadData()

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
        var userRef = database.child("users/${user?.uid}")
        /*kelasRef.get().addOnSuccessListener {
            for (kelas in it.children) {
                if (kelas.child("users/${user?.uid}").value == true) {
                    var nama = kelas.child("nama_kelas").value
                    var desc = kelas.child("deskripsi").value
                    var pengajar = kelas.child("pengajar").value
                    var image = kelas.child("image").value
                    var akronim = kelas.child("akronim").value
                    _kelasList.add(Kelas("$nama", "$desc", "$pengajar", "$image", "$akronim"))
                }
            }
            initRecyclerView()
        }.addOnFailureListener {
            Log.d("failed", "FAILED")
        }*/

        val listKelas = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _kelasList.clear()
                for (kelas in dataSnapshot.children) {
                    if (kelas.child("users/${user?.uid}").value == true) {
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
                Log.w(ContentValues.TAG, "listKelas:onCancelled", databaseError.toException())
            }
        }
        kelasRef.addValueEventListener(listKelas)

        val titleUsername = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("uid").value.toString() == user?.uid) {
//                    txUsername.setText(snapshot.child("username").value.toString())
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