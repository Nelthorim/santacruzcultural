package eu.nelthorim.santacruzcultural.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table Bookmarks (id int primary key, json text not null)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // do nothing
    }

    companion object {
        /**
         * Método Factory que obtiene una base de datos global por defecto lista para usar
         * @param context
         */
        fun getDB(context: Context?): SQLiteDatabase {
            return Database(context, "main", null, 1).writableDatabase
        }
    }

}