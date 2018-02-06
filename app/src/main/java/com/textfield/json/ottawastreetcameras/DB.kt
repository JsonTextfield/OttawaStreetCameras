package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log

import java.io.IOException

class DB(private val mContext: Context) {
    private var mDb: SQLiteDatabase? = null
    private val mDbHelper: DBHelper = DBHelper(mContext)

    @Throws(SQLException::class)
    fun createDatabase(): DB {
        try {
            mDbHelper.createDataBase()
        } catch (mIOException: IOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase")
            throw Error("UnableToCreateDatabase")
        }

        return this
    }

    @Throws(SQLException::class)
    fun open(): DB {
        try {
            mDbHelper.openDataBase()
            mDbHelper.close()
            mDb = mDbHelper.readableDatabase
        } catch (mSQLException: SQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString())
            throw mSQLException
        }

        return this
    }

    fun close() {
        mDbHelper.close()
    }

    fun runQuery(sql: String): Cursor? {
        println(sql)
        try {
            val mCur = mDb!!.rawQuery(sql, null)
            mCur?.moveToNext()
            return mCur
        } catch (mSQLException: SQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString())
            throw mSQLException
        }

    }

    companion object {
        protected val TAG = "DataAdapter"
    }
}