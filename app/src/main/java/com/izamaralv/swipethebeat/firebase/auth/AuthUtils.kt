package com.izamaralv.swipethebeat.firebase.auth

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.hardComponentColor
import com.izamaralv.swipethebeat.common.textColor
import kotlinx.coroutines.tasks.await

suspend fun createAccount(email: String, password: String): Boolean {
    return try {
        val result = Auth.auth.createUserWithEmailAndPassword(email, password).await()
        true
    } catch (e: Exception) {
        Log.e("authError", "Error: ${e.message}", e)
        false
    }
}

suspend fun loginAccount(email: String, password: String): Boolean {
    return try {
        val result = Auth.auth.signInWithEmailAndPassword(email, password).await()
        true
    } catch (e: Exception) {
        Log.e("authError", "Error: ${e.message}", e)
        false
    }
}

fun isValidEmail(email: String): Boolean {
    // email standard validation
    val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})+\$")
    return email.matches(emailRegex)
}

fun isValidPassword(password: String): Boolean {
    // Password should have at least 8 characters, one uppercase letter, and one digit
    val passwordRegex = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")
    return password.matches(passwordRegex)
}

@Composable
fun CustomAuthDialog(
    title: String,
    message: String,
    variable1: MutableState<String>? = null,
    variable2: MutableState<String>? = null,
    variable3: MutableState<String>? = null,
    onDismissRequest: () -> Unit

) {
    AlertDialog(
        onDismissRequest = {
            // Reset the variables to empty strings before dismissing the dialog
            variable1?.value = ""
            variable2?.value = ""
            variable3?.value = ""
            onDismissRequest()
        },
        title = {
            Text(
                text = title,
                color = textColor,
                fontSize = 35.sp,
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Text(text = message, color = textColor, fontSize = 20.sp)
        },
        confirmButton = {
            Button(
                onClick = { onDismissRequest() }, colors = ButtonDefaults.buttonColors(
                    hardComponentColor
                )
            ) {
                Text(text = "OK", color = Color.Black, fontSize = 15.sp)
            }
        },
        containerColor = backgroundColor,
        dismissButton = null
    )
}


