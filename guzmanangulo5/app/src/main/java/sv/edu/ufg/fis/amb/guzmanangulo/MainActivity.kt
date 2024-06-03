package sv.edu.ufg.fis.amb.guzmanangulo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)
        buttonSave = findViewById(R.id.buttonSave)

        createInitialFiles() // Crear los archivos al iniciar la app

        buttonSave.setOnClickListener {
            val text = editText.text.toString()
            if (text.isNotEmpty()) {
                saveToInternalFile(text)
                if (isExternalStorageWritable() && checkPermission()) {
                    saveToExternalFile(text)
                }

                // Verificar si el archivo se guardó en la memoria interna
                val fileNameInternal = "escritura_de_campo_interno.txt"
                val fileInternal = File(filesDir, fileNameInternal)
                if (fileInternal.exists()) {
                    Log.d("Archivo Interno", "Existe en memoria interna")
                } else {
                    Log.d("Archivo Interno", "No existe en memoria interna")
                }

                // Verificar si el archivo se guardó en la memoria externa
                val fileNameExternal = "escritura_de_campo_externo.txt"
                val fileExternal = File(getExternalFilesDir(null), fileNameExternal)
                if (fileExternal.exists()) {
                    Log.d("Archivo Externo", "Existe en memoria externa")
                } else {
                    Log.d("Archivo Externo", "No existe en memoria externa")
                }
            } else {
                Toast.makeText(this, "Ingrese un texto antes de guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createInitialFiles() {
        // Crear archivo en almacenamiento interno
        val fileNameInternal = "archivo_inicial_interno.txt"
        val fileInternal = File(filesDir, fileNameInternal)
        try {
            FileOutputStream(fileInternal, true).use { fos ->
                fos.write("Archivo inicial en memoria interna\n".toByteArray())
            }
            Toast.makeText(this, "Archivo inicial creado en memoria interna", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Crear archivo en almacenamiento externo
        val fileNameExternal = "archivo_inicial_externo.txt"
        if (isExternalStorageWritable() && checkPermission()) {
            val fileExternal = File(getExternalFilesDir(null), fileNameExternal)
            try {
                FileOutputStream(fileExternal, true).use { fos ->
                    fos.write("Archivo inicial en memoria externa\n".toByteArray())
                }
                Toast.makeText(this, "Archivo inicial creado en memoria externa", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveToInternalFile(data: String) {
        val fileName = "escritura_de_campo_interno.txt"
        val file = File(filesDir, fileName)
        try {
            FileOutputStream(file, true).use { fos ->
                fos.write((data + "\n").toByteArray())
            }
            editText.setText("")
            Toast.makeText(this, "Guardado en memoria interna", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar en memoria interna", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToExternalFile(data: String) {
        val fileName = "escritura_de_campo_externo.txt"
        val file = File(getExternalFilesDir(null), fileName)
        try {
            FileOutputStream(file, true).use { fos ->
                fos.write((data + "\n").toByteArray())
            }
            Toast.makeText(this, "Guardado en memoria externa", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar en memoria externa", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    private fun checkPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                createInitialFiles() // Crear los archivos si el permiso es concedido
                buttonSave.performClick()
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
