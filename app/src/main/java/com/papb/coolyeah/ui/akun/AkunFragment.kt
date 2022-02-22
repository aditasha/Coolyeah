package com.papb.coolyeah.ui.akun

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage
import com.papb.coolyeah.GlideApp
import com.papb.coolyeah.NavDraw
import com.papb.coolyeah.R
import com.papb.coolyeah.User_Model
import com.papb.coolyeah.databinding.FragmentAkunBinding

class AkunFragment : Fragment(), View.OnClickListener {

    private lateinit var AkunView: AkunViewModel
    private lateinit var _binding: FragmentAkunBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    private lateinit var _imgDatabase : ImageView
    private lateinit var _txProgress : TextView
    private lateinit var _txEmail : TextView
    private lateinit var _txUsername : TextView
    private lateinit var edit : Button
    private lateinit var save: Button
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser?  = null
    private lateinit var database: DatabaseReference
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private var imageUri: Uri? = null
    private var image: String? = null
    private lateinit var imageName: String
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        activityResult(result)
    }
    private lateinit var circularProgressDrawable: CircularProgressDrawable
    var editStatus = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AkunView =
            ViewModelProvider(this).get(AkunViewModel::class.java)

        _binding = FragmentAkunBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textSlideshow
//        AkunView.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        database = Firebase.database.reference
        auth = Firebase.auth
        user = auth.currentUser

        circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 20f
        circularProgressDrawable.start()

//        edit = findViewById(R.id.editProfile)
//        save = findViewById(R.id.saveProfile)
//        _txProgress = findViewById(R.id.progress)
//        _txEmail = findViewById(R.id.txEmail)
//        _txUsername = findViewById(R.id.txUsername)
//        _imgDatabase = findViewById(R.id.database_img)

        edit = binding.editProfile
        save = binding.saveProfile
        _txProgress = binding.progress
        _txEmail = binding.txEmail
        _txUsername = binding.txUsername
        _imgDatabase = binding.databaseImg

        edit.setOnClickListener(this)
        save.setOnClickListener(this)
        _imgDatabase.setOnClickListener(this)

        loadData()

        return root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.editProfile -> {
                _txEmail.isEnabled = true
                _txUsername.isEnabled = true
                edit.visibility = View.INVISIBLE
                save.visibility = View.VISIBLE
                editStatus = true
            }

            R.id.saveProfile -> {
                var dbuser = database.child("users/${user?.uid}")
                dbuser.get().addOnSuccessListener {
                    var userModel = User_Model(
                        user?.uid,
                        _txEmail.text.toString(),
                        _txUsername.text.toString(),
                        "$image"
                    )
                    var userValues = userModel.toMap()
                    val childUpdates = hashMapOf<String, Any>(
                        "/users/${user?.uid}" to userValues
                    )
                    database.updateChildren(childUpdates)
                    user?.updateEmail(_txEmail.text.toString())

                    val intent = Intent(requireContext(), NavDraw::class.java)
                    Toast.makeText(requireContext(), "Berhasil mengedit akun", Toast.LENGTH_LONG).show()
                    startActivity(intent)
                }
            }

            R.id.database_img -> {
                if (editStatus) {
                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    gallery.putExtra("request", "pickImage")
                    resultLauncher.launch(gallery)
                }
            }
        }
    }


    private fun loadData() {
        var dbuser = database.child("users/${user?.uid}")
        dbuser.get().addOnSuccessListener {
            _txEmail.text = it.child("email").value.toString()
            _txUsername.text = it.child("username").value.toString()
            image = it.child("image").value.toString()

            GlideApp.with(this /* context */)
                .load(storageRef.child("${it.child("image").value}"))
                .placeholder(circularProgressDrawable)
                .into(_imgDatabase)
        }
    }

    private fun activityResult(result: ActivityResult) {
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            imageName = imageUri?.lastPathSegment.toString() + user?.uid.toString()
            if (imageName != image) {
                image = imageName
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        val uploadTask = imageUri?.let {
            storageRef.child("${imageUri!!.lastPathSegment}${user?.uid}").putFile(it)
        }
        uploadTask?.addOnProgressListener { (bytesTransferred, totalByteCount) ->
            val progress = (100.0 * bytesTransferred) / totalByteCount
            _txProgress.text = "Upload is ${progress.toInt()}% done"
        }?.addOnPausedListener {
            _txProgress.text = "Upload is paused"
        }?.addOnSuccessListener {
            database.child("users/${user?.uid}/image").setValue("${imageUri!!.lastPathSegment}${user?.uid}")
            GlideApp.with(this /* context */)
                .load(storageRef.child("${imageUri!!.lastPathSegment}${user?.uid}"))
                .placeholder(circularProgressDrawable)
                .into(_imgDatabase)
        }
    }


//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}