package se.helagro.filereader

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException


class ReaderActivity : AppCompatActivity() {
    companion object {
        val TAG = Companion::class.java.name.toString()
        val tabRegex = Regex("\t")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reader)

        val path = intent.getStringExtra("path")

        findViewById<TextView>(R.id.file_view).text = readFile(path)
    }

    private fun readFile(path: String?): String {
        if (path == null)
            return Toast.makeText(this, "No path provided!", Toast.LENGTH_LONG).show().toString()

        val text = StringBuilder()
        try {
            val file: File = File(path)
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                text.append('\n')
            }
            br.close()
        } catch (e: IOException) {
            Toast.makeText(this, "Error reading file! $e", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

        val string = text.toString()
        return string.replace(tabRegex, "    ")
    }

}