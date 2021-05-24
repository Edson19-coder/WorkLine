package com.example.workline

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
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
import kotlinx.android.synthetic.main.nav_header.view.*
import android.app.Dialog
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_register.*

class   HomeActivity : AppCompatActivity() {

    private var userImage: String = ""
    private var userEmail:String = ""
    private var userCarrera: String = ""
    private var name: String = ""
    private var lastName: String = ""

    var estados = arrayOf("Activo","Ocupado","Ausente","Desconectado")
    lateinit var dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle = intent.extras
        userCarrera = bundle?.getString("carrera").toString()
        userImage = bundle?.getString("userImage").toString()
        userEmail = bundle?.getString("email").toString()
        name = bundle?.getString("name").toString()
        lastName = bundle?.getString("lastName").toString()

        navView.getHeaderView(0).textViewNameHeader.text = name + " " + lastName
        navView.getHeaderView(0).textViewHeaderEmail.text = userEmail

        //GAURDADO DE DATOS
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("carrera", userCarrera)
        prefs.putString("userImage", userImage)
        prefs.putString("email", userEmail)
        prefs.putString("name", name)
        prefs.putString("lastName", lastName)
        prefs.apply()

        setCarrera(userCarrera)

        dialog = Dialog(this)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.opcCerrar -> {
                    //BORRAR DATOS
                    val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                    prefs.clear()
                    prefs.apply()

                    //Funcion para cambiar estado del usuario.

                    FirebaseAuth.getInstance().signOut()
                    finish()
                }
                R.id.opcEstado -> {

                }
                R.id.opcEditar -> println("Perfil")
            }
            true
        }

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.messagesFragment, R.id.groupsFragment, R.id.tasksFragment))
        bottomNavigationView.setupWithNavController(findNavController(R.id.fragment))
    }

    override fun onResume() {

        //ESTADO INACTIVO
        println("He vuelto")

        super.onResume()
    }

    override fun onPause() {

        //ESTADO ACTIVO
        println("A mimir")

        super.onPause()
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