package com.example.workline

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.workline.Data.IMAGE_PICK_CODE
import com.example.workline.Data.PERMISSION_CODE
import com.example.workline.modelos.SubGroup
import com.example.workline.modelos.UserPreview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_group.*
import java.util.*

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var mStorage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val dbrt = FirebaseDatabase.getInstance()
    private val subGroupsRef = dbrt.getReference("SubGrupos")
    private val subGroupsMembersRef = dbrt.getReference("UsuariosSubGroup")
    private var selectedPhotoUri: Uri? = null
    private var urlUpload: Uri? = null
    private var title: String? = null
    private var carrera: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        val bundle = intent.extras
        carrera = bundle?.getString("carrera").toString()

        imageButtonCorreo.setOnClickListener {
            finish()
        }

        btnChangeImageGroup.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE)
                } else {
                    pickImageFromGallery()
                }
            } else {
                pickImageFromGallery()
            }
        }

        btnCreateGroup.setOnClickListener {
            title = editTextTextPersonName.text.toString()
            if(title != null || title != "") {
                uploadImage()
            } else {
                Log.e("AddGroup", "Failed add group")
            }
        }
    }

    private fun uploadImage() {
        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = mStorage.getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("AddGroup", "Success upload image from group: ${it.metadata?.path}")
            ref.downloadUrl.addOnSuccessListener {
                Log.d("AddGroup", "File location: $it")
                urlUpload = it
                createGroup()
            }
        }
    }

    private fun createGroup() {
        auth = Firebase.auth
        db.collection("users").document(auth.currentUser.uid).get().addOnSuccessListener {
            it.id
            val user = UserPreview(it.id.toString(), it.get("name").toString() + " " + it.get("lastName").toString(), it.get("image").toString())
            val groupId = subGroupsRef.push().key.toString()
            val subGroup = SubGroup( groupId,title.toString(), urlUpload.toString(), carrera.toString())
                subGroupsRef.child(carrera.toString()).child(groupId).setValue(subGroup).addOnSuccessListener {
                    subGroupsRef.child(carrera.toString()).child(groupId).child("Miembros").child(user.id).setValue(user)
                    AddUserMemberSubGroup(user, subGroup)
                }
            Log.d("AddGroup", "Success add group")
            finish()
        }
    }

    private fun AddUserMemberSubGroup(user: UserPreview, subGroup: SubGroup) {
        subGroupsMembersRef.child(user.id).child("SubGroups").child(subGroup.id).setValue(subGroup)
    }

    //GALERIA DE IMAGENES
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            PERMISSION_CODE -> {
                if(grantResults.size > 0) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            selectedPhotoUri = data?.data
            imageView4.setImageURI(selectedPhotoUri)
        }
    }
}