package com.example.notepad

import android.content.Context
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

@Suppress("UNCHECKED_CAST")
class FileHelper {

    private val filename = "information.dat"

    fun writeData(item: ArrayList<String>, context: Context) {
        val fos: FileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
        val oas = ObjectOutputStream(fos)
        oas.writeObject(item)
        oas.close()
    }

    fun readData(context: Context): ArrayList<String> {
        var itemList: ArrayList<String>
        try {
            val fis = context.openFileInput(filename)
            val ois = ObjectInputStream(fis)
            itemList = ois.readObject() as ArrayList<String>
        } catch (e: Exception) {
            itemList = ArrayList()
        }
        return itemList
    }


}