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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    public var email:String = "";
    var userCarrera = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle = intent.extras
        userCarrera = bundle?.getString("carrera").toString()

        //GAURDADO DE DATOS
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("carrera", userCarrera)

        setCarrera(userCarrera)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.messagesFragment, R.id.groupsFragment, R.id.tasksFragment))
        bottomNavigationView.setupWithNavController(findNavController(R.id.fragment))
    }

    private fun showStart() {
        val activityMain = Intent(this, MainActivity::class.java)
        startActivity(activityMain)
    }

    @JvmName("getCarrera")
    fun getCarrera(): String {
        return userCarrera
    }

    @JvmName("setCarrera")
    fun setCarrera(carrera:String) {
        this.userCarrera = carrera
    }
}