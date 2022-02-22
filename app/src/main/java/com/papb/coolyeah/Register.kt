package com.papb.coolyeah

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var database: DatabaseReference
    private lateinit var regisEmail: TextView
    private lateinit var regisUsername: TextView
    private lateinit var regisPass: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        auth = Firebase.auth
        database = Firebase.database.reference

        regisEmail = findViewById(R.id.regisEmail)
        regisUsername = findViewById(R.id.regisUsername)
        regisPass = findViewById(R.id.regisPass)
        var register = findViewById<Button>(R.id.register)
        register.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.register -> {
                auth.createUserWithEmailAndPassword(regisEmail.text.toString(), regisPass.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            user = auth.currentUser
                            buatAkun()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        }
    }

    private fun buatAkun() {
        var dbuser = database.child("users/${user?.uid}")
        dbuser.get().addOnSuccessListener {
            if (!it.exists()) {
                var user = User_Model(user?.uid, regisEmail.text.toString(), regisUsername.text.toString(), "default profile picture.png")
                var userValues = user.toMap()
                val childUpdates = hashMapOf<String, Any>(
                    "/users/${user.uid}" to userValues
                )
                database.updateChildren(childUpdates)
            }
            val intent = Intent(this, Login::class.java)
            intent.putExtra("email", regisEmail.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}