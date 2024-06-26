package com.example.transferingfileapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.transferingfileapp.databinding.ActivityRegisterBinding
import com.example.transferingfileapp.requestAPI.API
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : ComponentActivity() {
    lateinit var binding : ActivityRegisterBinding
    lateinit var auth : FirebaseAuth
    lateinit var api: API

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        api = API(this)

        binding.tvToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.edtEmailRegister.text.toString()
            val password = binding.edtPasswordRegister.text.toString()
            val rePassword = binding.edtRePasswordRegister.text.toString()

            if (email.isEmpty()) {
                binding.edtEmailRegister.error = "Email harus diisi!"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmailRegister.error = "Email tidak valid!"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.edtPasswordRegister.error = "Password harus diisi!"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 8) {
                binding.edtPasswordRegister.error = "Panjang password minimal 8 karakter!"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            if (rePassword.isEmpty()) {
                binding.edtRePasswordRegister.error = "Tulis ulang password anda!"
                binding.edtRePasswordRegister.requestFocus()
                return@setOnClickListener
            }

            if (rePassword != password) {
                binding.edtRePasswordRegister.error = "Password tidak valid!"
                binding.edtRePasswordRegister.requestFocus()

                binding.edtPasswordRegister.error = "Password tidak valid!"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            RegisterFirebase(email, password)
        }
    }

    private fun RegisterFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid ?: "00000"
                    Toast.makeText(this, "Register berhasil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    api.acquisitionData(email, uid)
                    auth.signOut()
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}