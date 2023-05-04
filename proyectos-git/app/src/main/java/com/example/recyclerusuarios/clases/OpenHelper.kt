package clases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class OpenHelper(contexto:Context) : SQLiteOpenHelper(contexto,"miBD",null,3) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("create table user(username varchar(150) PRIMARY KEY,pass varchar(600), date varchar(60), pfp BLOB, groupid integer(10), FOREIGN KEY(groupid) REFERENCES usergroup(id));")
        db.execSQL("CREATE TABLE usergroup(id INTEGER PRIMARY KEY AUTOINCREMENT, groupname VARCHAR(150));")
        db.execSQL("INSERT INTO usergroup (groupname) VALUES ('admin');")
        db.execSQL("INSERT INTO usergroup (groupname) VALUES ('user');")
        db.execSQL("INSERT INTO usergroup (groupname) VALUES ('other');")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }




}