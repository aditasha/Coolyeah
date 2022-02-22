package com.papb.coolyeah.detailKelas

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.papb.coolyeah.*

class RecyclerViewAdapter(_materiList: ArrayList<Materi>, _context: Context) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private var _materiList: ArrayList<Materi>
    private var _context: Context
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    var database: DatabaseReference
    var auth: FirebaseAuth
    var user: FirebaseUser?

    init {
        this._materiList = _materiList
        this._context = _context
        database = Firebase.database.reference
        auth = Firebase.auth
        user = auth.currentUser
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var _context: Context
        var _txMateri: TextView

        var _card: CardView

        init {
            _context = itemView.context
            _txMateri = itemView.findViewById(R.id.titleMateri)

            _card = itemView.findViewById(R.id.card)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_materi, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder._txMateri.text = _materiList[position]._nama.toString()

        holder._card.setOnClickListener {
            var intent = Intent(_context, DetailMateri::class.java)
            intent.putExtra("pengajar", _materiList[position]._pengajar.toString())
            intent.putExtra("akronim kelas", _materiList[position]._akronim.toString())
            intent.putExtra("key", _materiList[position]._key.toString())
            _context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return _materiList.size
    }
}