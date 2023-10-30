package se.helagro.filereader

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = Companion::class.java.name.toString()
        const val BASE_FOLDER = "/storage/emulated/0/Documents/Henrik's Vault"

        val SUB_FOLDERS = arrayOf(
            "",
            "i",
            "prj"
        )
    }

    private lateinit var sendBtn: ImageButton
    private lateinit var textInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!hasStoragePermission()) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ), 1
            )

            val intent = Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivity(intent)
        }


        // ============= VIEWS =============

        sendBtn = findViewById(R.id.sendBtn)
        textInput = findViewById(R.id.inputField)

        sendBtn.setOnClickListener {
            onSendBtnClicked()
        }
    }

    private fun hasStoragePermission(): Boolean {
        val read = hasPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        val write = hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val manage = Environment.isExternalStorageManager()

        if (read && write && manage) return true
        else {
            Toast.makeText(
                this,
                "Missing permission: read:$read write:$write manage:$manage",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun onSendBtnClicked() {
        if (!hasStoragePermission())
            return Toast.makeText(this, "No permission to read storage", Toast.LENGTH_LONG).show()


        val text = textInput.text.toString()
        val path = getFilePath(text)
            ?: return Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show()

        Log.d(TAG, "onSendBtnClicked: $path")
        val intent = Intent(this, ReaderActivity::class.java)
        intent.putExtra("path", path)
        startActivity(intent)
    }

    private fun getFilePath(file: String): String? {
        for (folder in SUB_FOLDERS) {
            val path = "$BASE_FOLDER/$folder/$file.md"
            if (File(path).exists())
                return path
        }

        return null
    }

    override fun onResume() {
        super.onResume()

        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        textInput.requestFocus()
        inputMethodManager.showSoftInput(textInput, InputMethodManager.SHOW_IMPLICIT)
    }

}