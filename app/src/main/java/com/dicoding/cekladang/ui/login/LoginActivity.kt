package com.dicoding.cekladang.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cekladang.MainActivity
import com.dicoding.cekladang.R
import com.dicoding.cekladang.databinding.ActivityLoginBinding
import com.dicoding.cekladang.ui.daftar.DaftarActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditText.error = getString(R.string.email_empty)
                }

                password.isEmpty() -> {
                    binding.passwordEditText.error = getString(R.string.password_empty)
                }

                password.length < 8 -> {
                    binding.passwordEditText.error = getString(R.string.password_short)
                }

                else -> {
                    showToast()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }

        binding.btnDaftar.setOnClickListener {
            startActivity(Intent(this, DaftarActivity::class.java))
        }
    }

    private fun showToast() {
        Toast.makeText(this@LoginActivity, "login berhasil", Toast.LENGTH_SHORT).show()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}