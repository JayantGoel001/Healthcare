package com.example.healthcare

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class LoginAct : AppCompatActivity() {

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login.setOnClickListener {
            val name=name_text.text.toString()
            val email=email_text.text.toString()
            val pass=password_text.text.toString()
            val sharedPreferences=getSharedPreferences("name_email_pass", Context.MODE_PRIVATE)
            val editor=sharedPreferences.edit()

            editor.putString("name",name)
            editor.putString("email",email)
            editor.putString("pass",pass)
            editor.apply()
            val intent= Intent(this,MainActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}
