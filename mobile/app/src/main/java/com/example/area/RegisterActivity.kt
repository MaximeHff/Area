package com.example.area

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.area.model.RegisterFields
import com.example.area.model.Token
import com.example.area.repository.Repository
import com.example.area.utils.*
import javax.security.auth.callback.Callback

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        var token: String?
        findViewById<Button>(R.id.request_button).setOnClickListener {
            lateinit var url: String;
            val sessionManager = SessionManager(this)
            val registerForm = RegisterFields(
                findViewById<EditText>(R.id.first_name_field).text.toString(),
                findViewById<EditText>(R.id.last_name_field).text.toString(),
                findViewById<EditText>(R.id.email_field).text.toString(),
                findViewById<EditText>(R.id.password_field).text.toString()
            )
            try {
                url = urlParser(
                    findViewById<EditText>(R.id.ip_field).text.toString(),
                    findViewById<EditText>(R.id.port_field).text.toString()
                )
                checkRegisterField(registerForm)
            }
            catch (e: IllegalArgumentException) {
                Toast.makeText(this, "Error: " + e.message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val rep = Repository(url)
            val viewModelFactory = MainViewModelFactory(rep)
            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
            viewModel.register(registerForm)
            viewModel.userResponse.observe(this, Observer { response ->
                if (response.isSuccessful) {
                    token = response.body()?.token
                    token?.let {
                        sessionManager.saveAuthToken("user_token", token!!)
                        sessionManager.saveAuthToken("url", url)
                    }
                    Toast.makeText(this, "Successfully logged in!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                }
            })
        }
    }
}