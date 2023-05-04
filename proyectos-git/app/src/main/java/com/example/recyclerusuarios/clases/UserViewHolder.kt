package com.example.recyclerusuarios.clases

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerusuarios.R

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val username: TextView = itemView.findViewById(R.id.username)
    val date: TextView = itemView.findViewById(R.id.date)
    val imageView: ImageView = itemView.findViewById(R.id.pfp)
    val deleteButton : Button = itemView.findViewById(R.id.deleteButton)
    val editButton : Button = itemView.findViewById(R.id.editButton)
}