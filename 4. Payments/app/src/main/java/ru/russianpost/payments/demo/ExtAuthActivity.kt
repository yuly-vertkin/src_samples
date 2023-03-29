package ru.russianpost.payments.demo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ExtAuthActivity : AppCompatActivity() {
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com/"/*"https://esia.gosuslugi.ru/login/"*/))
        startForResult.launch(extIntent)
    }
}