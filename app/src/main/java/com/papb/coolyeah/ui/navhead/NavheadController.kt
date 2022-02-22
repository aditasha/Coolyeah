package com.papb.coolyeah.ui.navhead

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.papb.coolyeah.GlideApp
import com.papb.coolyeah.R
import com.papb.coolyeah.databinding.NavHeaderNavDrawBinding

class NavheadController : AppCompatActivity() {

    private lateinit var _imgDatabase : ImageView
    private lateinit var _txUsername : TextView
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser?  = null
    private lateinit var database: DatabaseReference
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private var imageUri: Uri? = null
    private var image: String? = null
    private lateinit var imageName: String
    private lateinit var circularProgressDrawable: CircularProgressDrawable
    private lateinit var navView:NavigationView

    private lateinit var _binding: NavHeaderNavDrawBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_header_nav_draw)

        database = Firebase.database.reference
        auth = Firebase.auth
        user = auth.currentUser

        _binding = NavHeaderNavDrawBinding.inflate(layoutInflater)

        navView = findViewById(R.id.navhead)
//        _txUsername = navView.getHeaderView(0).findViewById(R.id.userName)
        _imgDatabase = findViewById(R.id.database_img2)
//        _txUsername = binding.userName
//        _imgDatabase = binding.databaseImg2

        _txUsername.text = "IKI NAVDRAW"

        circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 20f
        circularProgressDrawable.start()

        var dbuser = database.child("users/${user?.uid}")
        dbuser.get().addOnSuccessListener {
//            _txEmail.setText(it.child("email").value.toString())
            _txUsername.text = it.child("username").value.toString()
//            image = it.child("image").value.toString()

            GlideApp.with(this /* context */)
                .load(storageRef.child("${it.child("image").value}"))
                .placeholder(circularProgressDrawable)
                .into(_imgDatabase)
        }.addOnFailureListener{
            Log.e(ContentValues.TAG, "gagal lur")
        }
    }
}