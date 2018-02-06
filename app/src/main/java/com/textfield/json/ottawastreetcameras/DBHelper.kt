package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DBHelper(private val mContext: Context) : SQLiteOpenHelper(mContext, DB_NAME, null, 1) {
    private var mDataBase: SQLiteDatabase? = null

    init {
        DB_PATH = if(android.os.Build.VERSION.SDK_INT >= 17) mContext.applicationInfo.dataDir + "/databases/"
        else "/data/data/" + mContext.packageName + "/databases/"
    }

    @Throws(IOException::class)
    fun createDataBase() {
        //If the database does not exist, copy it from the assets.

        val mDataBaseExist = checkDataBase()
        if (!mDataBaseExist) {
            this.readableDatabase
            this.close()
            try {
                //Copy the database from assests
                copyDataBase()
                Log.e(TAG, "createDatabase database created")
            } catch (mIOException: IOException) {
                throw Error("ErrorCopyingDataBase")
            }

        }
    }

    //Check that the database exists here: /data/data/your package/databases/Da Name
    private fun checkDataBase(): Boolean {
        val dbFile = File(DB_PATH + DB_NAME)
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists()
    }

    //Copy the database from assets
    @Throws(IOException::class)
    private fun copyDataBase() {
        val mInput = mContext.assets.open(DB_NAME)
        val outFileName = DB_PATH + DB_NAME
        val mOutput = FileOutputStream(outFileName)
        val mBuffer = ByteArray(1024)
        var mLength: Int

        mLength = mInput.read(mBuffer)
        while (mLength > 0) {
            mOutput.write(mBuffer, 0, mLength)
            mLength = mInput.read(mBuffer)
        }
        mOutput.flush()
        mOutput.close()
        mInput.close()
    }

    //Open the database, so we can query it
    @Throws(SQLException::class)
    fun openDataBase(): Boolean {
        val mPath = DB_PATH + DB_NAME
        //Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY)
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null
    }

    @Synchronized override fun close() {
        if (mDataBase != null)
            mDataBase!!.close()
        super.close()
    }

    override fun onCreate(db: SQLiteDatabase) {

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    companion object {
        private val TAG = "DataBaseHelper" // Tag just for the LogCat window
        //destination path (location) of our database on device
        private var DB_PATH = ""
        private val DB_NAME = "camera_list.db"// Database name
    }
}
