package com.example.recyclerusuarios

import HomeButton
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginStart
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import clases.OpenHelper
import com.androidadvance.topsnackbar.TSnackbar
import com.example.recyclerusuarios.clases.User
import com.example.recyclerusuarios.clases.UserAdapter


class MainActivity : HomeButton() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val users: ArrayList<User> = ArrayList<User>()

        val byteArray: ByteArray = intent.getByteArrayExtra("pfp")!!
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        val username: String = intent.getStringExtra("user")!!
        val password : String = intent.getStringExtra("pass")!!
        val date : String = intent.getStringExtra("date")!!
        val groupId : Int = intent.getStringExtra("groupId")!!.toInt()

        val user = User(username, password, date, bitmap, groupId)
        val snackbar :TSnackbar = TSnackbar.make(findViewById(android.R.id.content),"Hola $username", TSnackbar.LENGTH_INDEFINITE)

        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.parseColor("#747572"));
        val textView = snackbarView.findViewById<View>(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.parseColor("#E5EBEA"))
        textView.textAlignment= View.TEXT_ALIGNMENT_CENTER
        snackbar.setAction("✘") { snackbar.dismiss() }
        snackbar.show()

        val querier: SQLiteDatabase = OpenHelper(this).writableDatabase //readableDatabase
        val cursor: Cursor = querier.query(
            "user",
            arrayOf("*"),
            null,
            null,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            val pass = cursor.getString(cursor.getColumnIndexOrThrow("pass"))
            val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val groupId = cursor.getInt(cursor.getColumnIndexOrThrow("groupid"))
            val pfp = cursor.getBlob(cursor.getColumnIndexOrThrow("pfp"))
            val byteArray: ByteArray = pfp
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            val user = User(username, pass, date, bitmap, groupId)
            users.add(user)
        }
        cursor.close()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = UserAdapter(users, user)
        recyclerView.adapter = adapter

        val logoutButton: Button = findViewById<Button>(R.id.logout)
        logoutButton.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            finish()
            startActivity(i)
        }

    }

    override fun onBackPressed() {
        val mensaje: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        mensaje.setTitle("¿Desea Salir de la Aplicacion?")
        mensaje.setCancelable(false)
        mensaje.setPositiveButton("Aceptar",
            DialogInterface.OnClickListener { dialog, which -> finish() })
        mensaje.setNegativeButton("Cancelar",
            DialogInterface.OnClickListener { dialog, which -> })
        mensaje.show()
    }
}
