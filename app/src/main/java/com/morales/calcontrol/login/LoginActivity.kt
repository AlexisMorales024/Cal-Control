package com.morales.calcontrol.login

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.morales.calcontrol.EntradaAlimentoActivity
import com.morales.calcontrol.MainActivity
import kotlinx.coroutines.launch
import com.morales.calcontrol.R
import kotlin.jvm.java

class LoginActivity : AppCompatActivity() {

    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton

    private lateinit var tvRegisterLink: TextView

    private lateinit var db: AppDatabase

    private val emailPattern =
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DatabaseProvider.getDatabase(this)

        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        btnLogin = findViewById(R.id.btnLogin)
        tvRegisterLink = findViewById(R.id.tvRegisterLink)

        // 🔥 CLICK LOGIN
        btnLogin.setOnClickListener {
            if (validateInputs()) {
                loginUser()
            }
        }

        // 🔥 CLICK REGISTRO (ESTO TE FALTABA)
        tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        lifecycleScope.launch {

            val user = db.usuarioDao().login(email, password)

            if (user != null) {

                Toast.makeText(this@LoginActivity, "Login exitoso", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@LoginActivity, EntradaAlimentoActivity::class.java)

                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)

            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Usuario o contraseña incorrectos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validateInputs(): Boolean {

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        tilEmail.error = null
        tilPassword.error = null

        if (email.isEmpty()) {
            tilEmail.error = "El correo es obligatorio"
            return false
        }

        if (!emailPattern.matches(email)) {
            tilEmail.error = "Correo inválido"
            return false
        }

        if (password.isEmpty()) {
            tilPassword.error = "La contraseña es obligatoria"
            return false
        }

        if (password.length < 6) {
            tilPassword.error = "Mínimo 6 caracteres"
            return false
        }

        return true
    }
}