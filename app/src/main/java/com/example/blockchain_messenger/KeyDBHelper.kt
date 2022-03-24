package com.example.blockchain_messenger

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build.ID
import android.provider.Telephony.Carriers.PASSWORD
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class KeyDBHelper (val context: Context?):SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private var insertCheck : Boolean = true

    companion object{
        const val DB_VERSION = 1
        const val DB_NAME = "keystore.db"
        const val TABLE_NAME = "KeystoreDatas"
        const val ID = "id"
        const val PRIV_KEY = "priv_key"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists $TABLE_NAME($ID text, $PRIV_KEY text)"
        db?.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        /*val sql : String = "DROP TABLE if exists mytable"

        db.execSQL(sql)
        onCreate(db)*/
    }


    fun insertKey(keyData : KeystoreData) : Boolean{
        val values = ContentValues()
        values.put(ID,keyData.id)
        values.put(PRIV_KEY,keyData.priv_key)

        val job = GlobalScope.launch(Dispatchers.Default) {
            val db: SQLiteDatabase = writableDatabase
            if(db.insert(TABLE_NAME,null,values) > 0 ){
                /*val activity = context as MainActivity
                activity.pIdEdit.setText("")
                activity.pNameEdit.setText("")
                activity.pQuantityEdit.setText("")*/
                insertCheck = true
                db.close()
            }else{
                insertCheck = false
                db.close()
            }
        }
        runBlocking {
            job.join()
        }

        return insertCheck
    }


    /*fun update(
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
    fun readData():MutableList<KeystoreData>{
        val list :MutableList<KeystoreData> = ArrayList()
        val db: SQLiteDatabase = readableDatabase
        val query = "Select * from $TABLE_NAME"
        val result:Cursor = db.rawQuery(query,null)

        if(result.moveToFirst()){
            do {
                val keyData = KeystoreData()
                keyData.id = result.getString(result.getColumnIndex(ID))
                keyData.priv_key = result.getString(result.getColumnIndex(PRIV_KEY))

                list.add(keyData)
            }while (result.moveToNext())
        }else
            Toast.makeText(context,"There is no data.",Toast.LENGTH_LONG).show()

        result.close()
        db.close()
        return list
    }



    // 로그인 체크
    fun getResult1(PUB_KEY: String, PRI_KEY: String): Boolean {
        var db: SQLiteDatabase = readableDatabase
        var result: String = ""

        var cursor: Cursor = db.rawQuery("SELECT PUB_KEY, PRI_KEY FROM MEMBER", null)
        while (cursor.moveToNext()) {
            result = (cursor.getString(0))
            if (result.equals(PUB_KEY)) {
                if (cursor.getString(1).equals(PRI_KEY)) {
                    return true
                    break
                } else {
                    return false
                }
            }else {

            }
        }

        return false
    }

}