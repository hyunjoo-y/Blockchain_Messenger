package com.example.blockchain_messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.blockchain_messenger.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import io.ipfs.multibase.Base16
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import java.security.DigestException
import java.security.MessageDigest
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    lateinit var keyDbHelper: KeyDBHelper

    private var checkUser: Boolean = false

    private var inputPubKey = ""

    private val SearchFriend = FriendContractManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        keyDbHelper = KeyDBHelper(this)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        var userID = ""

        if (user != null && getSharedPreferences("Token", MODE_PRIVATE).getString("token","").toString().isBlank()) {
            val token = user.uid
            val pref = getSharedPreferences("Token", MODE_PRIVATE)
            val checkToken =pref.getString("token",  "").toString()
            Log.d("TOKEN", checkToken)
            if (token == checkToken) {
                val nextIntent = Intent(this, MainActivity::class.java)
                nextIntent.putExtra("id", userID)
                startActivity(nextIntent)
            }else{
                Log.d("AUTO LOGIN","User is null")
            }

        } else {
            Toast.makeText(this, "로그인 정보가 만료되었습니다.", Toast.LENGTH_LONG).show()
        }

        var token: String = ""
        binding.moveToSignupButton.setOnClickListener {
            val nextIntent = Intent(this, SignUpActivity::class.java)
            startActivity(nextIntent)
        }

        binding.loginButton.setOnClickListener {
            if (user != null) {
                token = user.uid
            }
            val inputId = binding.editTextMnemonic.text.toString()
            val inputPassword = binding.editTextPasswordLogin.text.toString()

            val loginCheck = login(inputId, inputPassword)
            if (loginCheck) {
                Log.d("LOGIN", "success")
                auth.signInAnonymously()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_LONG).show()
                            token = auth.currentUser!!.uid
                            Log.d("LOGIN TOKEN", token)
                            saveDate(token)
                            val nextIntent = Intent(this, MainActivity::class.java)
                            nextIntent.putExtra("id", inputId)
                            startActivity(nextIntent)
                        }else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInAnonymously:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

            } else {
                Log.d("LOGIN", "fail")
                Toast.makeText(this, "잘못된 정보입니다.", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun autoCheck(){
        if(binding.loginCheckBox.isChecked){

        }else{
            var random_uuid = UUID.randomUUID()
        }
    }

    private fun login(inputMnemonic: String, inputPassword: String): Boolean {
        checkUser = false
        val data: MutableList<KeystoreData> = keyDbHelper.readData()
        //val credentials = WalletUtils.loadBip39Credentials(inputPassword,inputMnemonic)
        if (data.isNotEmpty())
            Log.d("Read", "read")
        for (i in 0 until data.size) {
            Log.d(
                "READ",
                "NICKNAME : ${data[i].id} \n PRIV_KEY : ${data[i].priv_key}\n"

            )
            if (inputMnemonic == data[i].id) {
                val credentials = Credentials.create(data[i].priv_key)
                val credAddr = credentials.address
                val inputPassHash = hashSHA256(inputPassword + data[i].priv_key)
                val checkHash = SearchFriend.getPass(inputMnemonic)
                Log.d("LOGIN", "Address: ${credentials.address}\n")
                Log.d("LOGIN", "input: $inputPassHash\n blockchain $checkHash\n")
                val result = SearchFriend.checkLogin(data[i].id)
                if (credAddr == result && inputPassHash == checkHash) {
                    checkUser = true
                }
                break
            }
        }
        return checkUser
    }

    fun saveDate(uuid: String) {
        val pref = getSharedPreferences("UUid", MODE_PRIVATE) //shared key 설정\
        val edit = pref.edit() // 수정모드
        edit.putString("UUID", uuid) // 값 넣기
        edit.apply() // 적용하기
    }

    fun hashSHA256(msg: String): String? {
        val hash: ByteArray
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(msg.toByteArray())
            hash = md.digest()
        } catch (e: CloneNotSupportedException) {
            throw DigestException("couldn't make digest of partial content");
        }

        return Base16.bytesToHex(hash)
    }
}
