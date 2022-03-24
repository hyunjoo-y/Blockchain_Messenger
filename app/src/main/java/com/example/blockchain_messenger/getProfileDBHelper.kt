package com.example.blockchain_messenger

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class getProfileDBHelper(val context: FragmentActivity) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private var insertCheck: Boolean = true

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "getProfile.db"
        const val TABLE_NAME = "ProfileDatas"
        const val IMAGE = "image"
        const val NAME = "name"
        const val ID = "id"
        const val STATUS = "status"
        const val LIB_ADDR = "lib_addr"
        const val ADDRESS = "address"
    }

    override fun onCreate(db: SQLiteDatabase?) {
       /* val create_table = "create table if not exists $TABLE_NAME($IMAGE" +
                " blob,  $NAME text, $NICKNAME text, $STATUS text, $LIB_ADDR text, $ADDRESS text)"
        db?.execSQL(create_table)*/
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        /*val sql : String = "DROP TABLE if exists mytable"

        db.execSQL(sql)
        onCreate(db)*/
    }


    fun insertProfile(proData: ProfileData): Boolean {
        val values = ContentValues()
        values.put(IMAGE, proData.img)
        values.put(NAME, proData.name)
        values.put(ID, proData.id)
        values.put(STATUS, proData.status)
        values.put(LIB_ADDR, proData.libaddr)
        values.put(ADDRESS, proData.address)

        val job = GlobalScope.launch(Dispatchers.Default) {
            val db: SQLiteDatabase = writableDatabase
            if (db.insert(TABLE_NAME, null, values) > 0) {
                /*val activity = context as MainActivity
                activity.pIdEdit.setText("")
                activity.pNameEdit.setText("")
                activity.pQuantityEdit.setText("")*/
                insertCheck = true
                db.close()
            } else {
                insertCheck = false
                db.close()
            }
        }
        runBlocking {
            job.join()
        }

        return insertCheck
    }


/*
    fun update(
        name: String, password: String, password_ok: String, phone: String, email: String,
        address: String, level: String
    ) {
        var db: SQLiteDatabase = writableDatabase

        db.execSQL(
            "UPDATE MEMBER SET PASSWORD = " + "'" + password + "'" + ", PASSWORD_OK = '" + password_ok + "'" + ", PHONE = '" + phone + "'"
                    + ", EMAIL = '" + email + "'" + ", ADDRESS = '" + address + "'" + ", LEVEL = '" + level + "'" +
                    "WHERE NAME = '" + name + "';"
        )

        db.close()
    }*/

    @SuppressLint("Range")
    fun readData(): MutableList<ProfileData> {
        val list: MutableList<ProfileData> = ArrayList()
        val db: SQLiteDatabase = readableDatabase
        val query = "Select * from $TABLE_NAME"
        val result: Cursor = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                val proData = ProfileData()
                proData.img = result.getBlob(result.getColumnIndex(IMAGE))
                proData.name = result.getString(result.getColumnIndex(NAME))
                proData.id = result.getString(result.getColumnIndex(ID))
                proData.status = result.getString(result.getColumnIndex(STATUS))
                proData.libaddr = result.getString(result.getColumnIndex(LIB_ADDR))
                proData.address = result.getString(result.getColumnIndex(ADDRESS))

                list.add(proData)
            } while (result.moveToNext())
        } else
            Toast.makeText(context, "There is no data.", Toast.LENGTH_LONG).show()

        result.close()
        db.close()
        return list
    }

}