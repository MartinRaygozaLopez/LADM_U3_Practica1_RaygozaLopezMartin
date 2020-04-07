package com.example.ladm_u3_practica1_raygozalopezmartina

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.ladm_u3_practica1_raygozalopezmartina.Utils.Utils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var nombreBaseDatos = "WORK"
    var imagenes = ArrayList<ImageView>()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layout_Img.setInAnimation(this, android.R.anim.slide_in_left)
        layout_Img.setInAnimation(this, android.R.anim.slide_out_right)

        btn_SImg.setOnClickListener {
            cargarImagen()
        }

        btn_InsertarA.setOnClickListener {
            insertarActividad()
        }

        btn_Buscar.setOnClickListener {
            var otroActivity = Intent(this, Main2Activity :: class.java)
            startActivity(otroActivity)
        }

        btn_A1Ant.setOnClickListener {
            layout_Img.showPrevious()
        }

        btn_A1Sig.setOnClickListener {
            layout_Img.showNext()
        }
    }

    fun cargarImagen() {
        var intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 10 && resultCode == Activity.RESULT_OK && data != null){
            var path = data.data
            var imgNew = ImageView(this)
            imgNew.setImageURI(path)
            imgNew.adjustViewBounds = true
            var param : LinearLayout.LayoutParams = LinearLayout.LayoutParams(500, 500)
            imgNew.layoutParams = param
            layout_Img.addView(imgNew)

            imagenes.add(imgNew)
            layout_Img.background = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun insertarActividad() {
        try {
            var baseDatos = BaseDatos(this, nombreBaseDatos, null, 1)
            var insertar = baseDatos.writableDatabase
            var SQL = "INSERT INTO ACTIVIDADES VALUES(NULL, '${txt_Descripcion.text.toString()}', '${txt_FCaptura.text.toString()}', '${txt_FEntrega.text.toString()}')"

            insertar.execSQL(SQL)
            insertar.close()
            baseDatos.close()

            insertarEvidencia()
        } catch (error : SQLiteException) {
            dialogo(error.message.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun insertarEvidencia() {
        try {
            (0..imagenes.size-1).forEach{
                val bitmap = (imagenes.get(it).drawable as BitmapDrawable).bitmap
                var evidencia = Evidencias(Utils.getBytes(bitmap))
                evidencia.asignarPuntero(this)
                evidencia.idActividad = ultimoID()
                var resultado = evidencia.insertarImagen()

                if(resultado == true){
                    mensaje("Se inserto correctamente")
                }else{
                    mensaje("ERROR")
                }
            }
            limpiarCampos()

        } catch (error : SQLiteException) {
            dialogo(error.message.toString())
        }
    }

    fun ultimoID() : String{
        try {
            var baseDatos = BaseDatos(this, nombreBaseDatos, null, 1)
            var select = baseDatos.readableDatabase
            var columnas = arrayOf("Id_Actividad")

            var cursor = select.query("ACTIVIDADES", columnas, null, null,null,null,null)

            if(cursor.moveToLast()) {
                return cursor.getString(0)
            }

            select.close()
            baseDatos.close()
        } catch (error : SQLiteException){
            dialogo(error.message.toString())
        }
        return ""
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun limpiarCampos() {
        txt_FEntrega.setText("")
        txt_FCaptura.setText("")
        txt_Descripcion.setText("")
        layout_Img.removeAllViews()
        imagenes = ArrayList<ImageView>()
        layout_Img.background = getDrawable(R.drawable.imgnodisponible)
    }

    fun mensaje(s : String) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }

    fun dialogo(s : String) {
        AlertDialog.Builder(this)
                .setTitle("ATENCION")
                .setMessage(s)
                .setPositiveButton("OK") {d , i -> }
                .show()
    }
}
