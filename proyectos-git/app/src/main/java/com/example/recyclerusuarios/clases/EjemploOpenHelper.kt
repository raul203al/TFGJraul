package clases

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate


/**
 * NOTAS--
 * Si quereis subir a bd un LocalDate por ejemplo, subidlo con un format aplicado y guardad ese format. De esa forma puedes subirlo y recuperarlo.
 * Si no, decidle a chatgpt que os haga la sentencia sql con el formato. la funcion sql es stl_to_date('YYYY-mm-DD','2002-07-22').
 *
 *                                                                                              Es un ejemplo. buscad los formatos correspondientes.
 */
class EjemploOpenHelper : AppCompatActivity() {
    override fun onStart() {
        super.onStart()
        var querier:SQLiteDatabase=OpenHelper(this).writableDatabase //readableDatabase
        //A execSQL se le puede hacer sqlInjection. A las demás no.
        querier.execSQL("insert into tabla values('tatata')")//devuelve Unit
        //querier.update()
        //querier.insert()

            /*cursor Database*/  //QUERY = SELECT * FROM...
        var cursor: Cursor=querier.query(
            "user",
            arrayOf("email","pass"),
            "LENGTH(pass)>2 and email = ?",
            arrayOf("fran@gmail.com"),
            "email",
            null,
            "desc",
            null)//nombreTabla,ArrayString con las columnas a recuperar(null=todas),condiciones pero sin escribir where, como el preparedStatement pero creamos un arrayOf con todos los strings que queremos reemplazar en orden (null si no hace falta), group by pero sin poner group by(null si no hace falta), having pero sin escribir having(null si no hace falta), order by pero sin poner order by (null si no hace falta), limit 33 pero sin poner la palabra limit(null si no queremos limite).

        while(cursor.moveToNext()){
            cursor.getString(cursor.getColumnIndexOrThrow("email"))//Van en orden como se pusieron en el arrayOf de las columnas, arriba se ve
            cursor.getString(cursor.getColumnIndexOrThrow("pass"))
            var bool=cursor.getInt(cursor.getColumnIndexOrThrow("columnaInexistente"))!=0 //Si es distinto de 0, es true
            var fecha:LocalDate= LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow("fechaRegistro")))
        }


        querier.delete("user","username = ? and pass = ?", arrayOf("fran@gmail.es")) //nombre tabla, sentencia where sin el where, array palabras que sustituyen las ? en orden
    }

}