package com.example.workline

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.workline.modelos.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_configuration.*
import kotlinx.android.synthetic.main.activity_login.*
import java.io.ByteArrayOutputStream
import java.util.*

class ConfigurationActivity : AppCompatActivity() {

    private var selectedPhotoUri: Uri? = null
    private var urlUpload: Uri? = null

    private var mStorage = FirebaseStorage.getInstance()
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        auth = Firebase.auth

        btnChangeImageProfile.setOnClickListener {
            changeImage()
        }

        getUserInfo()

    }

    private fun getUserInfo() {
        db.collection("users").document(auth.uid.toString()).get().addOnSuccessListener {
            user = User((it.get("userName") as String), it.get("email").toString(), it.get("name") as String, it.get("lastName") as String, it.get("carrera") as String, it.get("image").toString())

            if(user != null) {
                editTextTextPersonName2.setText(user!!.nombre)
                editTextTextPersonName3.setText(user!!.lastName)
                editTextTextPersonName4.setText(user!!.userName)
                editTextTextPersonName5.setText(user!!.email)
                if(user!!.image != "") {
                    Picasso.get().load(Uri.parse(user!!.image)).into(imgProfileActivity)
                }
            }
        }
    }


    private fun changeImage() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            var boolDo:Boolean =  false
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, 1001)
            }
            else{
                //permission already granted
                boolDo =  true

            }


            if(boolDo == true){
                pickImageFromGallery()
            }

        }

    }

    private fun pickImageFromGallery() {
        //Abrir la galería
        val intent  =  Intent()
        intent.setAction(Intent.ACTION_PICK);
        intent.type = "image/*"
        startActivityForResult(intent, 1000)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            1001 -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //PERMISO DENEGADO
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestcode: Int, resultcode: Int, data: Intent?) {
        super.onActivityResult(requestcode, resultcode, intent)

        if (resultcode == Activity.RESULT_OK) {

            if(requestcode == 1000) {
                selectedPhotoUri = data?.data
                imgProfileActivity.setImageURI(selectedPhotoUri)
                saveImage()
            }
        }
    }

    private fun saveImage() {

        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = mStorage.getReference("/userImage/$filename")
        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("AddImage", "Success upload image from group: ${it.metadata?.path}")
            ref.downloadUrl.addOnSuccessListener {
                Log.d("AddImage", "File location: $it")
                urlUpload = it
                if(urlUpload != null) {
                    db.collection("users").document(auth.uid.toString()).update("image", urlUpload.toString())
                }
            }
        }
    }
}