package com.emretemir.babymonitorwithesp32

import android.content.Context
import android.widget.Toast
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class FirebaseFunctionsHelper(private val context: Context) {
    private val functions: FirebaseFunctions = Firebase.functions

    fun callFunction(functionName: String, parameters: Map<String, Any>? = null, onSuccess: (result: Any?) -> Unit, onFailure: (exception: Exception) -> Unit) {
        functions
            .getHttpsCallable(functionName)
            .call(parameters)
            .addOnSuccessListener { result ->
                onSuccess(result.data)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Bağlantı hatası: ${exception.message}", Toast.LENGTH_SHORT).show()
                onFailure(exception)
            }
    }
}
