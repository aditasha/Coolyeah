package com.papb.coolyeah

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var _txEmail: TextView
    private lateinit var _txPass: TextView
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data?.getStringExtra("from").toString() == "logout") {
                Toast.makeText(this, "has logged out", Toast.LENGTH_LONG).show()
            }
            else {
                var _tempEmail = data?.getStringExtra("email").toString()
                _txEmail.text = _tempEmail
                Toast.makeText(this, "You are registered!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        auth = Firebase.auth

        _txEmail = findViewById<TextView>(R.id.email)
        _txPass = findViewById<TextView>(R.id.password)
        var login = findViewById<Button>(R.id.login)
        var register = findViewById<Button>(R.id.goRegister)
        login.setOnClickListener(this)
        register.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.login -> {
                auth.signInWithEmailAndPassword(_txEmail.text.toString(), _txPass.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            var intent = Intent(this, NavDraw::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }

            R.id.goRegister -> {
                var intent = Intent(this, Register::class.java)
                resultLauncher.launch(intent)
            }
        }
    }
}