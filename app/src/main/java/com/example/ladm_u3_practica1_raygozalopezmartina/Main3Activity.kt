package com.example.ladm_u3_practica1_raygozalopezmartina

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.Gravity
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ladm_u3_practica1_raygozalopezmartina.Utils.Utils
import kotlinx.android.synthetic.main.activity_main3.*
import java.lang.Exception


class Main3Activity : AppCompatActivity() {
    var nombreBaseDatos = "WORK"
    var idFoto = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        var extrs = intent.extras
        var idEliminar = extrs?.getString("id").toString()

        cargarRegistro(idEliminar)

        btn_Eliminar.setOnClickListener {
            eliminarRegistro(idEliminar)
            var otroActivity = Intent(this, Main2Activity :: class.java)
            startActivity(otroActivity)
        }

        btn_Cancelar.setOnClickListener {
            finish()
        }

        btn_Ant.setOnClickListener {
            VS1.showPrevious()
        }

        btn_Sig.setOnClickListener {
            VS1.showNext()
        }
    }

    fun cargarRegistro(id : String) {
        try {
            var baseDatos = BaseDatos(this, nombreBaseDatos, null, 1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM ACTIVIDADES WHERE Id_Actividad = ?"
            var parametros = arrayOf(id)
            var cursor = select.rawQuery(SQL, parametros)

            if(cursor.moveToFirst()){
                //SI HAY RESULTADO
                lbl_ID.setText("ID:" + cursor.getString(0))
                lbl_Des.setText("Descripcion: " + cursor.getString(1))
                lbl_FC.setText("Fecha Captura: " + cursor.getString(2))
                lbl_FE.setText("Fecha Entrega: " + cursor.getString(3))
            } else {
                //NO HAY RESULTADO
                mensaje("NO SE ENCONTRO COINCIDENCIA")
            }
            select.close()
            baseDatos.close()

            recuperarImgs(id)

        } catch (error : SQLiteException){
            dialogo(error.message.toString())
        }
    }

    fun recuperarImgs(id : String) {
        var nulo : ByteArray? = null
        var evidencia = Evidencias(nulo)
        evidencia!!.asignarPuntero(this)
        var imagenes = evidencia.buscarImagen(id)
        var img = ArrayList<ImageView>()

        VS1.setInAnimation(this, android.R.anim.slide_in_left)
        VS1.setInAnimation(this, android.R.anim.slide_out_right)

        try {
            (0..imagenes.size-1).forEach {
                var imgNew = ImageView(this)
                imgNew.adjustViewBounds = true
                val bitmap = Utils.getImage(imagenes[it])
                imgNew.setImageBitmap(bitmap)
                var param : LinearLayout.LayoutParams = LinearLayout.LayoutParams(500, 500)
                imgNew.layoutParams = param
                VS1.addView(imgNew)
            }
        } catch (error : Exception){
        }
    }

    fun eliminarRegistro(id : String) {
        try {
            var base = BaseDatos(this, nombreBaseDatos, null, 1)
            var eliminar = base.writableDatabase
            var idEliminar = arrayOf(id.toString())
            var respuesta = eliminar.delete("EVIDENCIAS", "Id_Actividad = ?", idEliminar)

            if(respuesta.toInt() == 0) {
                dialogo("NO SE HA ELIMINADO CORRECTAMENTE")
            }

            var respuesta2 = eliminar.delete("ACTIVIDADES", "Id_Actividad = ?", idEliminar)

            if(respuesta2.toInt() == 0) {
                dialogo("NO SE HA ELIMINADO CORRECTAMENTE")
            }
        } catch (e : SQLiteException) {
            mensaje(e.message.toString())
        }
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
