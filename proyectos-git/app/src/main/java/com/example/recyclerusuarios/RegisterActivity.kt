package com.example.recyclerusuarios

import HomeButton
import android.Manifest
import android.R
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import clases.OpenHelper
import com.androidadvance.topsnackbar.TSnackbar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.recyclerusuarios.clases.User
import com.example.recyclerusuarios.databinding.ActivityRegisterBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class RegisterActivity : HomeButton(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityRegisterBinding

    private val CAMERA_PERMISSION_CODE = 100
    private val GALLERY_PERMISSION_CODE = 101
    private val PICK_IMAGE_REQUEST_CODE = 102
    var photoFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load(com.example.recyclerusuarios.R.drawable.pfplaceholder)
            .placeholder(com.example.recyclerusuarios.R.drawable.pfplaceholder)
            .into(binding.imageView)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }

        // Solicitar permiso de lectura de almacenamiento externo
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                GALLERY_PERMISSION_CODE
            )
        }

        binding.registerDateInput.setOnClickListener {
            showDatePickerDialog()
        }

        val options = arrayOf("Admin", "Usuario")

        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, options)

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        binding.spinner.adapter = adapter

        var groupId: Int? = null
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedOption = options[position]
                if (selectedOption.equals("Admin")) {
                    groupId = 1
                } else {
                    groupId = 2
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Nada que hacer aquí
            }
        }



        binding.createButton.setOnClickListener {
            val regex = Regex("^[a-zA-Z0-9]+$")
            val regexPass = Regex("^[a-zA-Z0-9-_]+$")

            if (binding.registerUserInput.text.isNullOrBlank() || binding.registerPassInput.text.isNullOrBlank() || binding.registerDateInput.text.isNullOrBlank()) {
                TSnackbar.make(findViewById(android.R.id.content),"Por favor rellene los campos", TSnackbar.LENGTH_LONG).show();

                //usergen()
            } else if (regex.matches(binding.registerUserInput.text.toString()) && regexPass.matches(
                    binding.registerPassInput.text.toString()
                )
            ) {
                val username: String = binding.registerUserInput.text.toString()
                val pass: String = binding.registerPassInput.text.toString()
                val date: String = binding.registerDateInput.text.toString()
                val pfp: Bitmap = binding.imageView.drawToBitmap()
                var querier: SQLiteDatabase = OpenHelper(this).writableDatabase //readableDatabase
                var cursor: Cursor = querier.query(
                    "user",
                    arrayOf("*"),
                    "username = ? and pass = ?",
                    arrayOf(username, pass),
                    null,
                    null,
                    null,
                    null
                )

                if (cursor.moveToNext()) {
                    TSnackbar.make(findViewById(android.R.id.content),"Usuario ya registrado", TSnackbar.LENGTH_LONG).show();

                } else {
                    val stream = ByteArrayOutputStream()
                    pfp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()
                    val values = ContentValues()
                    values.put("username", username)
                    values.put("pass", pass)
                    values.put("date", date)
                    values.put("pfp", byteArray)
                    values.put("groupid", groupId)
                    querier.insert("user", null, values)
                    val i = Intent(this, LoginActivity::class.java)
                    i.putExtra("user", username)
                    i.putExtra("pass", pass)
                    i.putExtra("date", date)
                    i.putExtra("pfp", byteArray)
                    i.putExtra("groupid", groupId)
                    finish()
                    startActivity(i)
                }
            } else {
                TSnackbar.make(findViewById(android.R.id.content),"Por favor introduce caracteres validos", TSnackbar.LENGTH_LONG).show();
            }
        }

        binding.imageView.setOnClickListener {
            val options = arrayOf<CharSequence>("Tomar foto", "Elegir de galería")
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Elegir una opción")
            builder.setItems(options) { dialog, item ->
                if (options[item] == "Tomar foto") {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent.resolveActivity(packageManager) != null) {
                        // Crea un archivo temporal para almacenar la imagen tomada
                        val photoFile: File? = try {
                            createImageFile()
                        } catch (ex: IOException) {
                            null
                        }
                        // Si se pudo crear el archivo temporal, continua con la toma de la foto
                        photoFile?.let {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                this,
                                "com.example.recyclerusuarios.fileprovider",
                                it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, CAMERA_PERMISSION_CODE)
                        }

                    }

                } else if (options[item] == "Elegir de galería") {
                    // Lógica para seleccionar una imagen de la galería
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(
                        Intent.createChooser(intent, "Selecciona una imagen"),
                        PICK_IMAGE_REQUEST_CODE
                    )
                }
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, R.style.ThemeOverlay_Material_Dialog, this, year, month, day)
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = "$dayOfMonth/${month + 1}/$year"
        binding.registerDateInput.setText(selectedDate)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de cámara concedido
            }
        }
        if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de lectura de almacenamiento externo concedido
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Obtener la imagen seleccionada de la galería
            val imageUri = data?.data
            Glide.with(this)
                .load(imageUri)
                .placeholder(com.example.recyclerusuarios.R.drawable.pfplaceholder)
                .into(binding.imageView)
        } else if (requestCode == CAMERA_PERMISSION_CODE && resultCode == RESULT_OK) {
            val photoFile = photoFile
            val bitmap = BitmapFactory.decodeFile(photoFile?.absolutePath)
            Glide.with(this)
                .load(bitmap)
                .placeholder(com.example.recyclerusuarios.R.drawable.pfplaceholder)
                .into(binding.imageView)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            photoFile = this
        }
    }

     fun usergen() {
        var querier: SQLiteDatabase = OpenHelper(this).writableDatabase //readableDatabase

        for (i in 0..100) {
            val stream = ByteArrayOutputStream()
            val bitmap = binding.imageView.drawToBitmap()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            val values = ContentValues()
            values.put("username", "a$i")
            values.put("pass", "a$i")
            values.put("date", LocalDate.now().toString())
            values.put("pfp", byteArray)
            val random = Random()
            val numero = random.nextInt(3) + 1

            values.put("groupid", numero)
            querier.insert("user", null, values)
        }
    }

}