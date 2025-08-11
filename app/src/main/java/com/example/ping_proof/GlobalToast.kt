package com.example.ping_proof
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object GlobalToast {

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun show(message: String) {
        appContext?.let {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
