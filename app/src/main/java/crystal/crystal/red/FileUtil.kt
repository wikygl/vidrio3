package crystal.crystal.red

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream

object FileUtil {
    fun getPath(context: Context, uri: Uri): String? {
        val fileName = getFileName(context, uri) ?: return null
        val tempFile = File(context.cacheDir, fileName)
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            return null
        }
        return tempFile.absolutePath
    }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String? {
        var nombre: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                nombre = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return nombre
    }
}


