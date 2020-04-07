package com.example.ladm_u3_practica1_raygozalopezmartina

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException

class Evidencias (img : ByteArray?) {
    var foto = img
    var idActividad = ""
    var error = -1
    val nombreBaseDatos = "WORK"
    var puntero : Context?= null

    fun asignarPuntero(p : Context){
        puntero = p
    }

    fun insertarImagen() : Boolean{
        try {
            var base =BaseDatos(puntero!!,nombreBaseDatos,null,1)
            var insertar = base.writableDatabase
            var datos = ContentValues()
            datos.put("Foto", foto)
            datos.put("Id_Actividad", idActividad)
            var respuesta = insertar.insert("EVIDENCIAS","Id_Evidencia", datos)
            if(respuesta.toInt() == -1){
                return false
            }
        }catch (e: SQLiteException){
            error = 1
            return false
        }
        return true
    }

    fun buscarImagen(id : String) : ArrayList<ByteArray> {
        var arreglo = ArrayList<ByteArray>()
        try {
            var baseDatos = BaseDatos(puntero!!, nombreBaseDatos, null, 1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM EVIDENCIAS WHERE Id_Actividad = ?"
            var parametros = arrayOf(id)
            var cursor = select.rawQuery(SQL, parametros)
            if (cursor.moveToFirst()){
                do{
                    arreglo.add(cursor.getBlob(cursor.getColumnIndex("Foto")))
                } while (cursor.moveToNext())
            }
        } catch (error : SQLiteException){

        }
        return arreglo
    }
}