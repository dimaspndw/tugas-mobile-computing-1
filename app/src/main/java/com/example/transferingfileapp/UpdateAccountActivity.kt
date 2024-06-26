package com.example.transferingfileapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.transferingfileapp.databinding.ActivityUpdateAccountBinding
import com.example.transferingfileapp.requestAPI.API
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import kotlin.math.sign

class UpdateAccountActivity : ComponentActivity() {
    private lateinit var binding: ActivityUpdateAccountBinding
    private lateinit var auth: FirebaseAuth
    lateinit var api: API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        api = API(this)

        binding.btnUpdateProfile.setOnClickListener {
            val newEmail = binding.edtEmailUpdate.text.toString()
            val newPassword = binding.edtPasswordUpdate.text.toString()
            if (newEmail.isNotEmpty() && newPassword.isEmpty()) {
                if (newEmail.isEmpty()) {
                    binding.edtEmailUpdate.error = "Email harus diisi!"
                    binding.edtEmailUpdate.requestFocus()
                    return@setOnClickListener
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    binding.edtEmailUpdate.error = "Email tidak valid!"
                    binding.edtEmailUpdate.requestFocus()
                    return@setOnClickListener
                }

                updateEmail(newEmail)
            } else if(newEmail.isEmpty() && newPassword.isNotEmpty()) {
                if (newPassword.isEmpty()) {
                    binding.edtPasswordUpdate.error = "Password harus diisi!"
                    binding.edtPasswordUpdate.requestFocus()
                    return@setOnClickListener
                }

                if (newPassword.length <= 8) {
                    binding.edtPasswordUpdate.error = "Panjang password minimal 8 karakter!"
                    binding.edtPasswordUpdate.requestFocus()
                    return@setOnClickListener
                }

                updatePassword(newPassword)
            } else {
                if (newEmail.isEmpty()) {
                    binding.edtEmailUpdate.error = "Email harus diisi!"
                    binding.edtEmailUpdate.requestFocus()
                    return@setOnClickListener
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    binding.edtEmailUpdate.error = "Email tidak valid!"
                    binding.edtEmailUpdate.requestFocus()
                    return@setOnClickListener
                }

                updateEmail(newEmail)

                if (newPassword.isEmpty()) {
                    binding.edtPasswordUpdate.error = "Password harus diisi!"
                    binding.edtPasswordUpdate.requestFocus()
                    return@setOnClickListener
                }

                if (newPassword.length <= 8) {
                    binding.edtPasswordUpdate.error = "Panjang password minimal 8 karakter!"
                    binding.edtPasswordUpdate.requestFocus()
                    return@setOnClickListener
                }

                updatePassword(newPassword)
            }

        }
    }

    private fun updateEmail(newEmail: String) {
        val user = auth.currentUser
        user?.let {
            it.verifyBeforeUpdateEmail(newEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Email verification sent.", Toast.LENGTH_SHORT).show()
                        var uid = user?.uid
                        Log.d("Uuid request update", uid.toString())
                        Log.d("Email request update", newEmail)
                        api.updateDataUser(uid.toString(), newEmail)
                        signOut()
                    } else {
                        try {
                            throw task.exception ?: Exception("Unknown error")
                        } catch (e: FirebaseAuthRecentLoginRequiredException) {
                            Toast.makeText(this, "Please re-authenticate and try again.", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this, "Gagal memperbarui email: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    private fun updatePassword(newPassword: String) {
        val user = auth.currentUser
        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    signOut()
                } else {
                    Toast.makeText(this, "Gagal memperbarui password: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signOut() {
        auth.signOut()

        val sharedPreferences = getSharedPreferences("TokenPreference", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("token")
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
