package com.example.workline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.workline.fragments.MembersFragment
import com.example.workline.fragments.MuroFragment
import com.example.workline.fragments.SubGroupFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_in_group.*

class InGroupActivity : AppCompatActivity() {

    private var carrera = "LMAD"
    private val dbrt = FirebaseDatabase.getInstance()
    private val estadosRef = dbrt.getReference("Estados")
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_group)

        val bundle = intent.extras
        carrera = bundle?.getString("carrera").toString()

        textViewTitleGroup.text = carrera

        auth = Firebase.auth

        setEstado("Activo")

        imageButton.setOnClickListener {
            finish()
        }

        floatingActionButton.setOnClickListener{
            val  activityIntent =  Intent(this,CreateGroupActivity::class.java)
            activityIntent.putExtra("carrera", carrera)
            startActivity(activityIntent)
        }

        menuTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> changeFragment(MuroFragment(), "fragmentMuro")
                    1 -> changeFragment(SubGroupFragment(), "fragmentGroups")
                    2 -> changeFragment(MembersFragment(), "fragmentMembers")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        menuTabLayout.selectTab(menuTabLayout.getTabAt(0))
        changeFragment(MuroFragment(), "fragmentMuro")
    }

    private fun changeFragment(fragment: Fragment, tag: String) {

        val currentFragment = supportFragmentManager.findFragmentByTag(tag)

        // Se valida que no este ya puesto el fragmento en el contenedor
        if (currentFragment == null || currentFragment.isVisible.not()) {

            // Se cambia el fragmento actual en el contenedor por el nuevo fragmento
            supportFragmentManager.beginTransaction()
                    .replace(R.id.framentContainer, fragment, tag)
                    .commit()
        }
    }

    private fun setEstado(estado: String) {
        estadosRef.child(auth.currentUser.uid).setValue(estado)
    }

    override fun onResume() {

        //ESTADO ACTIVO
        setEstado("Activo")

        super.onResume()
    }

    override fun onPause() {

        //ESTADO INACTIVO
        if(auth.currentUser != null) {
            setEstado("Inactivo")
        }

        super.onPause()
    }

    @JvmName("getCarrera")
    fun getCarrera(): String {
        return carrera
    }

    @JvmName("setCarrera")
    fun setCarrera(carrera:String) {
        this.carrera = carrera
    }
}