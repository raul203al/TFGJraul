package com.example.recyclerusuarios.clases

import android.content.Context
import android.os.AsyncTask
import android.view.View
import android.widget.Toast
import com.androidadvance.topsnackbar.TSnackbar
import com.example.recyclerusuarios.servicios.LoginResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class HttpConection(private val username: String, private val password: String, val context: Context) :
    AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void?): String {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://172.26.100.205:8585/Partes/resources/login?usuario=$username&password=$password")
            .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful){
            return response.body?.string() ?: ""
        }else{
            return null.toString()
        }

    }

    override fun onPostExecute(result: String?) {
        // Aquí puedes procesar el XML y guardar los campos en variables
        // Para parsear el XML, puedes usar alguna librería como XmlPullParser o JAXB
        if(result.equals("null")){
            Toast.makeText(context, "error en la conexion", Toast.LENGTH_LONG).show()
        }else{
            val gson = Gson()
            val loginResponse = gson.fromJson(result, LoginResponse::class.java)
            if (loginResponse.getRespuesta()?.getId() == 1){
                Toast.makeText(context, loginResponse.getOperario()?.getPersona()?.getDenominacionSocial(), Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context, loginResponse.getRespuesta()?.getMensaje(), Toast.LENGTH_LONG).show()
            }
        }

    }
}