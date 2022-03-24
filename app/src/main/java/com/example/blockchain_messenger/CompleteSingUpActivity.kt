package com.example.blockchain_messenger

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.blockchain_messenger.databinding.ActivityCompletesignupBinding
import com.example.blockchain_messenger.databinding.ActivityLoginBinding

class CompleteSingUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompletesignupBinding
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompletesignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.moveToLoginButton.setOnClickListener {
            val nextIntent = Intent(this, LoginActivity::class.java)
            startActivity(nextIntent)
        }

    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}