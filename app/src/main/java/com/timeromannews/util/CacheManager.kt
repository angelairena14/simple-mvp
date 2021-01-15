package com.timeromannews.util

import android.content.Context
import android.os.Build
import android.util.Log
import com.google.gson.GsonBuilder
import java.lang.reflect.Type
import java.util.*
import android.os.Build.VERSION_CODES.M
import java.io.*
import java.nio.charset.StandardCharsets


class CacheManager(val context: Context){
    fun writeJson(objects: Any, type : Type, filename : String){
        var file = File(context.cacheDir, filename)
        var outputStream : OutputStream? = null
        var gson = GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create()
        try {
            outputStream = FileOutputStream(file)
            var bufferedWriter : BufferedWriter
            bufferedWriter = if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)){
                BufferedWriter(OutputStreamWriter(outputStream,StandardCharsets.UTF_8))
            } else {
                BufferedWriter(OutputStreamWriter(outputStream,"UTF-8"))
            }
            gson.toJson(objects,type,bufferedWriter)
            bufferedWriter.close()
        } catch (e : FileNotFoundException){
            Log.i("not_found","file nof")
        } finally {
            if (outputStream != null){
                try {
                    outputStream.flush()
                    outputStream.close()
                } catch (e: IOException) { }

            }
        }
    }

    fun readJson (type: Type, filename: String) : Any? {
        var jsonData : Any? = null
        var file = File(context.cacheDir,filename)
        var inputStream : InputStream? = null
        var gson = GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create()
        try {
            inputStream = FileInputStream(file)
            var inputStreamReader : InputStreamReader
            inputStreamReader =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8
                    )
                } else {
                    InputStreamReader(inputStream, "UTF-8")
                }
            jsonData = gson.fromJson(inputStreamReader,type)
            inputStreamReader.close()
        } catch (e : FileNotFoundException){ }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) { }
            }
        }
        return jsonData
    }
}