package com.example.recyclerusuarios.clases

import android.graphics.Bitmap

class User{
    var username:String
    var password:String
    lateinit var date:String
    lateinit var pfp: Bitmap
    var groupId:Int

    constructor(user:String, pass:String, date: String, pfp:Bitmap, groupId: Int){
        this.username = user
        this.password = pass
        this.date = date
        this.pfp = pfp
        this.groupId = groupId
    }

    constructor(user:String, pass:String, groupId:Int){
        this.username = user
        this.password = pass
        this.groupId = groupId
    }
    override fun toString(): String {
        return "User(username='$username', password='$password', date='$date')"
    }


}