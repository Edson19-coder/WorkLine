package com.example.workline

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.workline.modelos.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLoginClose.setOnClickListener {
            finish()
        }

        setup()
        session()
    }

    private fun session() {
        /*val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if(email != null) {
            showHome(email)
        }*/
    }

    private fun setup() {
        title = "Ingresar"

        btnLoginEntrar.setOnClickListener {
            if(editTextLoginEmail.text.isNotEmpty() && editTextLoginPassword.text.isNotEmpty()) {
                auth = Firebase.auth
                auth.signInWithEmailAndPassword(editTextLoginEmail.text.toString(), editTextLoginPassword.text.toString())
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            db.collection("users").document(auth.uid.toString())
                                .get()
                                .addOnSuccessListener {
                                    if(task.isSuccessful) {
                                        val user = User((it.get("userName") as String), editTextLoginEmail.text.toString(), it.get("name") as String, it.get("lastName") as String, it.get("carrera") as String)
                                        Log.d("Success", "Usuario iniciado correctamente")
                                        showHome(user)
                                    } else {
                                        Log.e("Error", "Error al iniciar sesion")
                                    }
                                }
                        } else {
                            Log.e("Error", "Error al auth")
                        }
                    }
            }
        }
    }

    private fun showHome(user:User) {
        val activityHome = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", user.email)
        }
        startActivity(activityHome)
    }
}