package com.example.recyclerusuarios.servicios

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidadvance.topsnackbar.TSnackbar
import com.example.recyclerusuarios.clases.HttpConection
import com.example.recyclerusuarios.databinding.ActivityLoginBinding
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

import java.net.HttpURLConnection
import java.net.URL


private lateinit var binding: ActivityLoginBinding


class LoginWebActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {

            val regex = Regex("^[a-zA-Z0-9]+$")
            val regexPass = Regex("^[a-zA-Z0-9-_]+$")

            if (binding.loginUserInput.text.isNullOrBlank() || binding.loginPassInput.text.isNullOrBlank()) {
                TSnackbar.make(findViewById(android.R.id.content),"Por favor rellene los campos", TSnackbar.LENGTH_LONG).show();
            } else if (regex.matches(binding.loginUserInput.text.toString()) && regexPass.matches(
                    binding.loginPassInput.text.toString()
                )
            ) {
                val username: String = binding.loginUserInput.text.toString()
                val pass: String = binding.loginPassInput.text.toString()

                HttpConection(username, pass, this).execute()

            } else {
                TSnackbar.make(findViewById(android.R.id.content),"Por favor introduce caracteres validos", TSnackbar.LENGTH_LONG).show();
            }

        }
    }
}


