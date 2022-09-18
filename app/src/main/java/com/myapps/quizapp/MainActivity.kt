package com.myapps.quizapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var text ="Player"
        var editor: SharedPreferences.Editor
        val sharedPreferences:SharedPreferences =
            applicationContext.getSharedPreferences("MyPref", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.apply()
        val button = findViewById<Button>(R.id.btn_start)
        val playerName=findViewById<EditText>(R.id.player_name)
        if (sharedPreferences.contains("name"))
             text= sharedPreferences.getString("name",null)!!
        playerName.setText(text)
        button.setOnClickListener {
            if (playerName.text.toString().isEmpty()) {
                playerName.error = "Please enter your name"
            }
            else {
                editor= sharedPreferences.edit()
               editor.putString("name",playerName.text.toString())
                editor.apply()

                val intent = Intent(this, QuizQuestions::class.java)
                intent.putExtra(Constants.USER_NAME,playerName.text.toString())
                startActivity(intent)
                finish()
            }

        }


    }
}