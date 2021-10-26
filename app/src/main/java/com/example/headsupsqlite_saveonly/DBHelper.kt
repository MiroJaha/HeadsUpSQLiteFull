package com.example.headsupsqlite_saveonly

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(
    context: Context?,
    name: String?= "details.db",
    factory: SQLiteDatabase.CursorFactory?= null,
    version: Int= 2,
    private val tableName: String= "celebrities"
) : SQLiteOpenHelper(context, name, factory, version) {

    private val sqLiteDatabase: SQLiteDatabase= writableDatabase

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("create table $tableName (PK INTEGER PRIMARY KEY AUTOINCREMENT, Name Text, Taboo1 Text, Taboo2 Text, Taboo3 Text)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $tableName")
        onCreate(p0)
    }

    fun saveData(name: String, taboo1: String, taboo2: String, taboo3: String): Long {
        val contentValue= ContentValues()
        contentValue.put("Name",name)
        contentValue.put("Taboo1",taboo1)
        contentValue.put("Taboo2",taboo2)
        contentValue.put("Taboo3",taboo3)
        return sqLiteDatabase.insert(tableName,null,contentValue)
    }

    fun gettingData(): ArrayList<Information>{
        val celebrity= arrayListOf<Information>()
        return try{
            val cursor =
                sqLiteDatabase.query(tableName, null, null, null, null, null, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast){
                celebrity.add(
                    Information(
                        cursor.getInt(cursor.getColumnIndexOrThrow("PK")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Taboo1")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Taboo2")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Taboo3"))
                    )
                )
                cursor.moveToNext()
            }
            celebrity
        } catch (e:Exception){
            celebrity.add(Information(0,"null","null","null","null"))
            celebrity
        }
    }

    fun updateCelebrity(information: Information): Int{
        val contentValue= ContentValues()
        contentValue.put("Name",information.name)
        contentValue.put("Taboo1",information.taboo1)
        contentValue.put("Taboo2",information.taboo2)
        contentValue.put("Taboo3",information.taboo3)
        return sqLiteDatabase.update(tableName,contentValue,"PK = ?", arrayOf("${information.pk}"))
    }

    fun deleteCelebrity(pk: Int): Int{
        return sqLiteDatabase.delete(tableName,"PK=?", arrayOf("$pk"))
    }
}