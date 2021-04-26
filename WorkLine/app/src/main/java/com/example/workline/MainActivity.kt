package com.example.workline

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.workline.modelos.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLogin.setOnClickListener {
            showLogin()
        }

        btnRegister.setOnClickListener {
            showRegister()
        }

        session()
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if(email != null) {
            showHome(email)
        }
    }

    private fun showLogin() {
        val activityLogin = Intent(this, LoginActivity::class.java)
        startActivity(activityLogin)
    }

    private fun showRegister() {
        val activityRegister = Intent(this, RegisterActivity::class.java)
        startActivity(activityRegister)
    }

    private fun showHome(email: String) {
        val activityHome = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(activityHome)
    }
}