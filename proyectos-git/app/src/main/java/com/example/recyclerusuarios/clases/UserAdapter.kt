package com.example.recyclerusuarios.clases

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import clases.OpenHelper
import com.androidadvance.topsnackbar.TSnackbar
import com.example.recyclerusuarios.EditActivity
import com.example.recyclerusuarios.LoginActivity
import com.example.recyclerusuarios.R
import java.io.ByteArrayOutputStream

class UserAdapter(private val dataList: ArrayList<User>, private val loggedUser: User) :
    RecyclerView.Adapter<UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.element_recycler, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val context: Context = holder.itemView.context
        val data = dataList[position]
        // otras propiedades de diseño relevantes
        if (loggedUser.groupId != 1) {
            holder.editButton.isVisible = false
            holder.deleteButton.isVisible = false
        }
        val backgroundView = holder.itemView.findViewById<View>(R.id.element_container)
        when (data.groupId) {
            1 -> backgroundView.setBackgroundColor(context.resources.getColor(R.color.admin))
            2 -> backgroundView.setBackgroundColor(context.resources.getColor(R.color.user))
            3 -> backgroundView.setBackgroundColor(context.resources.getColor(R.color.other))
            else -> backgroundView.setBackgroundColor(Color.WHITE)
        }
        holder.username.setText(data.username)
        holder.date.setText(data.date)
        holder.imageView.setImageBitmap(data.pfp)
        holder.editButton.setOnClickListener {
            val i = Intent(context, EditActivity::class.java)
            i.putExtra("user", data.username)
            i.putExtra("pass", data.password)
            i.putExtra("date", data.date)
            i.putExtra("pfp", data.pfp)
            i.putExtra("groupId", loggedUser.groupId.toString())
            val stream = ByteArrayOutputStream()
            data.pfp.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            i.putExtra("pfp", byteArray)
            context.startActivity(i)

        }
        holder.deleteButton.setOnClickListener {
            // Cargar el archivo de audio desde res/raw
            val mediaPlayer = MediaPlayer.create(context, R.raw.notificacion)

            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)

            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.EFFECT_HEAVY_CLICK))

            // Reproducir el audio
            //mediaPlayer.start()

            Handler(Looper.getMainLooper()).postDelayed({
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0)
            }, 5000)

            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Eliminar usuario")
            builder.setMessage("¿Está seguro que desea eliminar este usuario?")
            builder.setPositiveButton("Sí") { dialog, which ->
                // Eliminar el usuario
                val querier: SQLiteDatabase =
                    OpenHelper(context).writableDatabase //readableDatabase
                val rowDelted = querier.delete(
                    "user",
                    "username = ? and pass = ?",
                    arrayOf(data.username, data.password)
                )

                if (rowDelted > 0) {
                    if (dataList.get(position).username.equals(loggedUser.username) && dataList.get(
                            position
                        ).password.equals(loggedUser.password)
                    ) {
                        val i = Intent(context, LoginActivity::class.java)
                        (context as Activity).finish()
                        context.startActivity(i)
                    }
                    TSnackbar.make(holder.itemView, "Usuario: " + data.username + " eliminado", TSnackbar.LENGTH_LONG).show();
                    dataList.removeAt(position)
                    notify(position)
                } else {

                    TSnackbar.make(holder.itemView.findViewById(android.R.id.content),"Error al intentar eliminar a: " + data.username, TSnackbar.LENGTH_LONG).show();
                }
            }
            builder.setNegativeButton("No", null)
            val dialog = builder.create()
            dialog.show()
        }
    }

    fun notify(position: Int) {
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, dataList.size)
    }

    override fun getItemCount() = dataList.size
}