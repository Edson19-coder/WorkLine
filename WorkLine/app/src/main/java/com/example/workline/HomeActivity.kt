package com.example.workline

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.workline.fragments.MessagesFragment
import com.example.workline.modelos.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.oAuthCredential
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_login.*

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    var email:String = ""
    var correoA = "davidmove@mail.com" //User.correo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle = intent.extras
        email = bundle?.getString("email").toString()

        //GAURDADO DE DATOS
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)

        setCorreo("edson@mail.com")
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.messagesFragment, R.id.groupsFragment, R.id.tasksFragment))
        bottomNavigationView.setupWithNavController(findNavController(R.id.fragment))

    }

    private fun showStart() {
        val activityMain = Intent(this, MainActivity::class.java)
        startActivity(activityMain)
    }

    @JvmName("getCorreo1")
    fun getCorreo(): String {
        return correoA
    }

    @JvmName("setCorreo1")
    fun setCorreo(correo:String) {
        this.correoA = correo
    }

}