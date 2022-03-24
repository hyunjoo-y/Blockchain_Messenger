package com.example.blockchain_messenger

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.blockchain_messenger.databinding.ActivitySignupBinding

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.*
import java.io.File
import java.security.*
import java.io.ByteArrayOutputStream
import android.os.NetworkOnMainThreadException

import io.ipfs.api.IPFS
import io.ipfs.api.NamedStreamable
import io.ipfs.multibase.Base16.bytesToHex
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.RuntimeException


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    // keystore sqlite
    lateinit var keyDbHelper: KeyDBHelper
    lateinit var proDbHelper: ProDBHelper
    lateinit var getProDBHelper: getProfileDBHelper
    lateinit var friendDBHelper: FriendsListDBHelper
    // lateinit var database: SQLiteDatabase


    private var check: Boolean? = null
    private val SearchFriend = FriendContractManager.getInstance(this)

    private var profileHash = ""

    @SuppressLint("SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBouncyCastle()
        initImageViewProfile()

        keyDbHelper = KeyDBHelper(this)
        proDbHelper = ProDBHelper(this)
        getProDBHelper = getProfileDBHelper(this)
        friendDBHelper = FriendsListDBHelper(this)

        // dbHelper = DBHelper(applicationContext, "keystore.db", null, 1)
        // database = dbHelper.writableDatabase


        binding.signUpButton.setOnClickListener {
            val password = binding.editTextPassword.text.toString()
            val id = binding.editTextID.text.toString()
            val name = binding.editTextUserName.text.toString()
            val result =
                SearchFriend.checkUser(id)
            check = result
            Log.d("CHECK", check.toString())
            if (id.length >= 3 && password.length >= 4 && id.length >= 2 && name.length >= 2) {
                if (check == true)
                    showToast("중복된 닉네임입니다.")
                else if (check == false) {
                    createWallet(password, id)
                    uploadFile()
                    val nextIntent = Intent(this, CompleteSingUpActivity::class.java)
                    startActivity(nextIntent)
                }
            } else if (password == "" && name == "" && id == "") {
                showToast("모든 정보를 입력해주세요.")
            } else if (password == "" || name == "" || id == "") {
                if (id == "")
                    showToast("아이디를 입력해주세요")
                else if (check == true)
                    showToast("중복된 아이디입니다.")
                else if (password.length < 4 || password == "")
                    showToast("비밀번호는 4자리 이상 입력해주세요.")
            }
        }

    }

    private fun setupBouncyCastle() {
        val provider: Provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
            ?: // Web3j will set up the provider lazily when it's first used.
            return
        if (provider.javaClass == BouncyCastleProvider::class.java) {
            // BC with same package name, shouldn't happen in real life.
            return
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }


    @DelicateCoroutinesApi
    @SuppressLint("SdCardPath")
    fun createWallet(pass: String, nick: String) {
        val walletPath = "/sdcard/Android/data/com.example.blockchain_messenger/files/Keystore"
        val keystore = File(walletPath)
        if (!keystore.exists()) {
            keystore.mkdirs()
            Log.d("FILE", "생성")
        }

        val name = binding.editTextUserName.text.toString()
        val img = drawbleToByteArray(binding.userProfileImageVIew.drawable)
        val status = binding.editTextStatus.text.toString()

        try {
            val walletName = WalletUtils.generateBip39Wallet(pass, keystore)
            val credentials: Credentials =
                WalletUtils.loadBip39Credentials(pass, walletName.mnemonic)
            val keypair = credentials.ecKeyPair
            val priv_key = keypair.privateKey.toString(16)
            val address = credentials.address.toString()

            Log.d("SIGN", walletName.filename)
            Log.d("SIGN", address)

            val passHash = hashSHA256(pass + priv_key)
            if (passHash != null) {
                Log.d("PASSWORD",passHash)
            }
            val keystore = KeystoreData(nick, priv_key)
            val dbCheck = keystore.let { keyDbHelper.insertKey(it) }
            if (dbCheck) Log.d("DB", "DB 저장 성공: ${keyDbHelper.databaseName}")
            else Log.d("DB", "DB 실패")
            val lib = "QmWndDdkbzhUM75yuvUj2n6FNGsYRdQNEYakzHD9i2mxML"

            val proData = ProfileData(img, name, nick, status, lib, address)
            val proSaveCheck = proDbHelper.insertProfile(proData)
            if (proSaveCheck) Log.d("DB", proDbHelper.databaseName)
            uploadFile()
            if (passHash != null) {
                //SearchFriend.registerUser(nick,lib,address,passHash,profileHash)
            }
            readKeyDB()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

        return bytesToHex(hash)
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }


    private fun drawbleToByteArray(drawble: Drawable?): ByteArray? {
        val bitmapDrawable = drawble as BitmapDrawable?
        var bitmap = bitmapDrawable?.bitmap
        val stream = ByteArrayOutputStream()
        if (bitmap != null) {
            bitmap = getResizedBitmap(bitmap, 500)
        }

        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)

        return stream.toByteArray()
    }

    private fun readKeyDB() {
        val data: MutableList<KeystoreData> = keyDbHelper.readData()
        if (data.isNotEmpty())
            Log.d("Read", "read")
        for (i in 0 until data.size) {
            Log.d(
                "READ",
                "NICKNAME : ${data[i].id}\n PRIV_KEY : ${data[i].priv_key}\n"

            )
        }
    }
    private fun readFriendsList() {
        val data: MutableList<ProfileData> = friendDBHelper.readData()
        if (data.isNotEmpty())
            Log.d("Read", "read")
        for (i in 0 until data.size) {
            Log.d(
                "READ",
                "NICKNAME : ${data[i].name}\n STATUS : ${data[i].status} \n LIBADDR : ${data[i].libaddr}\n" +
                        "NICKNAME : ${data[i].id}\n ADDRESS : ${data[i].address} \n"
            )
        }
    }

    private fun insertFriend() {
        val data: MutableList<ProfileData> = getProDBHelper.readData()
        val profile = data[0]
        if (data.isNotEmpty())
            Log.d("Read", "read")

        friendDBHelper.insertFriends(profile)
    }

    @SuppressLint("SdCardPath")
    private fun uploadFile() {
        val job = GlobalScope.launch(Dispatchers.Default) {
            try {
                val ipfs = IPFS("/dnsaddr/ipfs.infura.io/tcp/5001/https")
                val profile: NamedStreamable.FileWrapper =
                    NamedStreamable.FileWrapper(File("/data/data/com.example.blockchain_messenger/databases/Profile.db"))
                profileHash = ipfs.add(profile)[0].hash.toBase58()
                /*val response: List<MerkleNode> = ipfs.add(file1)
                response.forEach(Consumer<MerkleNode> { merkleNode: MerkleNode ->
                    profileHash = merkleNode.hash.toBase58()
                })*/
                //System.out.println( "Hase(base 58): " + merkleNode.name.get() + " - " + merkleNode.hash.toBase58()));
                System.out.println("Hase(base 58): $profileHash")
                /*val multihash: Multihash = Multihash.fromBase58(profileHash)
                val inputStream: InputStream = ipfs.catStream(multihash)
                val getPath = "/data/data/com.example.blockchain_messenger/databases/getProfile.db"
                val getPro = File(getPath)
                if(getPro.exists())
                    getPro.delete()
                Files.copy(
                    inputStream,
                    Paths.get(getPath)
                )
                insertFriend()
                readFriendsList()*/
                // registerHash()
                // MerkleNode response = ipfs.add(file).get(0);
                //   System.out.println("Hash (base 58): " + response.hash.toBase58());
            } catch (ex: IOException) {
                throw RuntimeException("Error whilst communicating with the IPFS node", ex)
            } catch (ex: NetworkOnMainThreadException) {
                Log.d("Profile", "ERROR!")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        runBlocking {
            job.join()
        }
    }

    private fun initImageViewProfile() {
        binding.userProfileImageVIew.setOnClickListener {
            when {
                // 갤러리 접근 권한이 있는 경우
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                -> {
                    navigateGallery()
                }

                // 갤러리 접근 권한이 없는 경우 & 교육용 팝업을 보여줘야 하는 경우
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                -> {
                    showPermissionContextPopup()
                }

                // 권한 요청 하기(requestPermissions) -> 갤러리 접근(onRequestPermissionResult)
                else -> requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            }

        }
    }

    // 권한 요청 승인 이후 실행되는 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    navigateGallery()
                else
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                //
            }
        }
    }

    private fun navigateGallery() {
        // Intent.ACTION_GET_CONTENT: 핸드폰의 컨텐츠를 가져오는 안드로이드 내장 액티비티를 시작한다.
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        // 가져올 컨텐츠들 중에서 Image 만을 가져온다.
        intent.type = "image/*"
        // 갤러리에서 이미지를 선택한 후, 프로필 이미지뷰를 수정하기 위해 갤러리에서 수행한 값을 받아오는 startActivityForeResult를 사용한다.
        startActivityForResult(intent, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 예외처리
        if (resultCode != Activity.RESULT_OK)
            return

        when (requestCode) {
            // 2000: 이미지 컨텐츠를 가져오는 액티비티를 수행한 후 실행되는 Activity 일 때만 수행하기 위해서
            2000 -> {
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    binding.userProfileImageVIew.setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("프로필 이미지를 바꾸기 위해서는 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

    fun showToast(toast: String?) {
        runOnUiThread {
            Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
        }
    }
}

