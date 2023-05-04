package com.example.recyclerusuarios

import HomeButton
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import clases.OpenHelper
import com.androidadvance.topsnackbar.TSnackbar
import com.example.recyclerusuarios.databinding.ActivityLoginBinding
import java.net.URL


class LoginActivity : HomeButton() {

    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.createButton.setOnClickListener {
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
        }

        binding.gogol.setOnClickListener {
            val url = "https://www.google.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)

        }

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

                val querier: SQLiteDatabase = OpenHelper(this).writableDatabase //readableDatabase
                val cursor: Cursor = querier.query(
                    "user",
                    arrayOf("*"),
                    "username = ? and pass = ?",
                    arrayOf(username, pass),
                    null,
                    null,
                    null,
                    null
                )
                if (cursor.moveToFirst()) {
                    val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                    val pass = cursor.getString(cursor.getColumnIndexOrThrow("pass"))
                    val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    val pfp = cursor.getBlob(cursor.getColumnIndexOrThrow("pfp"))
                    val groupId = cursor.getString(cursor.getColumnIndexOrThrow("groupid"))

                    val byteArray: ByteArray = pfp
                    val i = Intent(this, MainActivity::class.java)
                    i.putExtra("user", username)
                    i.putExtra("pass", pass)
                    i.putExtra("date", date)
                    i.putExtra("pfp", byteArray)
                    i.putExtra("groupId", groupId.toString())

                    finish()
                    startActivity(i)
                } else {
                    TSnackbar.make(findViewById(android.R.id.content),"El usuario no existe en la base de datos", TSnackbar.LENGTH_LONG).show();
                }
                cursor.close()
            } else {
                TSnackbar.make(findViewById(android.R.id.content),"Por favor introduce caracteres validos", TSnackbar.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBackPressed() {

        if ((getWindow().getDecorView().getRootWindowInsets().isVisible(WindowInsets.Type.navigationBars())) == false) {
            val mensaje: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
            mensaje.setTitle("¿Desea Salir de la Aplicacion?")
            mensaje.setCancelable(false)
            mensaje.setPositiveButton("Aceptar",
                DialogInterface.OnClickListener { dialog, which -> finish() })
            mensaje.setNegativeButton("Cancelar",
                DialogInterface.OnClickListener { dialog, which -> })
            mensaje.show()        } else {
            super.onBackPressed();
        }


    }

    override fun onUserLeaveHint() {
        // Show dialog here
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { dialog, which ->
                finish() // Close the activity and exit the app
            }
            .setNegativeButton("No", null)
            .show()
        super.onUserLeaveHint()
    }


}
