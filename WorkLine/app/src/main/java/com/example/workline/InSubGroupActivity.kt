package com.example.workline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.workline.fragments.*
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_in_group.*
import kotlinx.android.synthetic.main.activity_in_sub_group.*

class InSubGroupActivity : AppCompatActivity() {

    private var idSubGroup: String = ""
    private var idGroup: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_sub_group)

        val bundle = intent.extras
        idSubGroup = bundle?.getString("idSubGroup").toString()
        idGroup = bundle?.getString("groupId").toString()
        val nameGroup = bundle?.getString("nameGroup").toString()

        textViewTitleSubGroup.text = nameGroup

        imageButtonSubGroup.setOnClickListener {
            finish()
        }

        menuTabLayoutSubGroup.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> changeFragment(ChantInSubGroupFragment(), "fragmentChatInSubGroup")
                    1 -> changeFragment(MembersInSubGroupFragment(), "fragmentMembersInSubGroup")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        menuTabLayoutSubGroup.selectTab(menuTabLayoutSubGroup.getTabAt(0))
        changeFragment(ChantInSubGroupFragment(), "fragmentChatInSubGroup")
    }

    private fun changeFragment(fragment: Fragment, tag: String) {

        val currentFragment = supportFragmentManager.findFragmentByTag(tag)

        // Se valida que no este ya puesto el fragmento en el contenedor
        if (currentFragment == null || currentFragment.isVisible.not()) {

            // Se cambia el fragmento actual en el contenedor por el nuevo fragmento
            supportFragmentManager.beginTransaction()
                    .replace(R.id.framentContainerSubGroup, fragment, tag)
                    .commit()
        }
    }

    @JvmName("getIdSubGroup")
    fun getSubGroup(): String {
        return idSubGroup
    }

    @JvmName("getIdGroup")
    fun getGroup(): String {
        return idGroup
    }
}