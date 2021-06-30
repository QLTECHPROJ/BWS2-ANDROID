package com.brainwellnessspa.roomDataBase

import android.content.Context
import androidx.room.Room
import com.brainwellnessspa.BWSApplication

class DatabaseClient(private val Ctx: Context?) {
    //our app database object
    private val cart: AudioDatabase
    fun getaudioDatabase(): AudioDatabase {
        return cart
    }

    companion object {
        private var Instance: DatabaseClient? = null
        @Synchronized
        fun getInstance(Ctx: Context?): DatabaseClient? {
            if (Instance == null) {
                Instance = DatabaseClient(Ctx)
            }
            return Instance
        }
    }

    init {

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        cart = Room.databaseBuilder(Ctx!!, AudioDatabase::class.java, "Audio_database").addMigrations(BWSApplication.MIGRATION_2_3).build()
    }
}