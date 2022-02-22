package com.papb.coolyeah.ui.daftarKelas

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
import com.papb.coolyeah.detailKelas.DetailKelas
import com.papb.coolyeah.GlideApp
import com.papb.coolyeah.Kelas
import com.papb.coolyeah.R

class RecyclerViewAdapter(_kelasList: ArrayList<Kelas>, _context: Context) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private var _kelasList: ArrayList<Kelas>
    private var _context: Context
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    var database: DatabaseReference
    var auth: FirebaseAuth
    var user: FirebaseUser?

    init {
        this._kelasList = _kelasList
        this._context = _context
        database = Firebase.database.reference
        auth = Firebase.auth
        user = auth.currentUser
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var _context: Context
        var _txNama: TextView
        var _image: ImageView
        var _card: CardView

        init {
            _context = itemView.context
            _txNama = itemView.findViewById(R.id.titleListKelas)
            _image = itemView.findViewById<ImageView>(R.id.bgListKelas)
            _card = itemView.findViewById(R.id.card)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_list_kelas, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val circularProgressDrawable = CircularProgressDrawable(_context)
        circularProgressDrawable.strokeWidth = 7f
        circularProgressDrawable.centerRadius = 40f
        circularProgressDrawable.start()

        GlideApp.with(_context /* context */)
            .load(storageRef.child(_kelasList[position]._image.toString()))
            .placeholder(circularProgressDrawable)
            .into(holder._image)
        holder._txNama.text = _kelasList[position]._nama.toString()

        holder._card.setOnClickListener {
            var intent = Intent(_context, DetailKelas::class.java)
            intent.putExtra("nama", _kelasList[position]._nama.toString())
            intent.putExtra("from", "list kelas")
            _context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return _kelasList.size
    }
}