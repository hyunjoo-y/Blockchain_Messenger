package com.example.blockchain_messenger

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.blockchain_messenger.databinding.FragmentFriendsBinding
import io.ipfs.api.IPFS
import io.ipfs.multihash.Multihash
import kotlinx.coroutines.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.StaticGasProvider
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.RuntimeException
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Paths
import java.security.Provider
import java.security.Security

class FriendsFragment : Fragment(){



    lateinit var friendsAdapter: FriendsAdapter
    private val datas = mutableListOf<FriendData>()

    private var searchFriendData = ProfileData()

    lateinit var proDbHelper: ProDBHelper
    lateinit var friendDBHelper: FriendsListDBHelper
    lateinit var getProDBHelper: getProfileDBHelper

    lateinit var binding: FragmentFriendsBinding

    private lateinit var userNick : String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        proDbHelper = ProDBHelper(requireActivity())
        getProDBHelper = getProfileDBHelper(requireActivity())
        friendDBHelper = FriendsListDBHelper(requireActivity())

        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        val toolbar: Toolbar = binding.toolbar2
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        myProfile()
        initRecycler()
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as AppCompatActivity).menuInflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

        if (id == R.id.searchFriend) {
            return true
        }
        if (id == R.id.addFriend) {
            showAddFriend()
            return true
        }
        if (id == R.id.setting) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }



    @SuppressLint("SdCardPath")
    @DelicateCoroutinesApi
    @Throws(IOException::class)
    // toolbar
    private fun showAddFriend() {
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.addfriend_dialog, null)
        val input: EditText = view.findViewById(R.id.inputNickEditText)

        val add = view.findViewById<Button>(R.id.addButton)
        val cancel = view.findViewById<Button>(R.id.cancelButton)
        val search = view.findViewById<Button>(R.id.searchFrinedBlockButton)
        val profile = view.findViewById<LinearLayout>(R.id.searchProfileLinear)

        profile.visibility = View.INVISIBLE

        val name = view.findViewById<TextView>(R.id.friendsNameTextView)
        val nickId = view.findViewById<TextView>(R.id.nicknameTextView)
        val ethAddr = view.findViewById<TextView>(R.id.EthereumAddrTextView)
        val status = view.findViewById<TextView>(R.id.friendStateTextView)
        val imageView = view.findViewById<ImageView>(R.id.searchFriendImageView)
        nickId.text = input.text
        val frName = input.text

        val alterDialog = AlertDialog.Builder(requireContext())
            .create()

        // 친구 추가
        add.setOnClickListener {
            val SearchFriend = FriendContractManager.getInstance(requireContext())
            friendDBHelper.insertFriends(searchFriendData)
            SearchFriend.addFriend(userNick,nickId.toString())
            initRecycler()
            alterDialog.dismiss()
        }

        // 친구 추가 취소
        cancel.setOnClickListener {
            alterDialog.dismiss()
        }




        // 블록체인에서 친구 검색 --> 값 가져와서 프로필 띄우기
        search.setOnClickListener {
            setupBouncyCastle()
            val SearchFriend = FriendContractManager.getInstance(requireContext())
            val result =
                SearchFriend.checkUser(frName.toString())
            if (result) {
                val friendArray = SearchFriend.getUserProfile(frName.toString())
                // 이름, 이더리움, libp2p, IPFS
                var profileHash = friendArray[2]
                Log.d("Hash", profileHash)
                val job = GlobalScope.launch(Dispatchers.Default) {
                    try {
                        val ipfs = IPFS("/dnsaddr/ipfs.infura.io/tcp/5001/https")
                        val multihash: Multihash = Multihash.fromBase58(profileHash)
                        val inputStream: InputStream = ipfs.catStream(multihash)
                        val getPath =
                            "/data/data/com.example.blockchain_messenger/databases/getProfile.db"
                        val getPro = File(getPath)
                        if (getPro.exists())
                            getPro.delete()
                        Files.copy(
                            inputStream,
                            Paths.get(getPath)
                        )

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
                val data: MutableList<ProfileData> = getProDBHelper.readData()
                if (data.isNotEmpty())
                    Log.d("Read", "read")
                val image = data[0].img?.let { getImage(it) }
                imageView.setImageBitmap(image)
                name.text = data[0].name
                nickId.text = data[0].id
                ethAddr.text = data[0].address
                status.text = data[0].status
                searchFriendData = data[0]

                profile.visibility = View.VISIBLE
            }
            else{
                profile.visibility = View.VISIBLE
                name.text = "존재하지 않는 닉네임입니다."
                nickId.visibility = View.INVISIBLE
                ethAddr.visibility = View.INVISIBLE
                status.visibility = View.INVISIBLE
                imageView.visibility = View.INVISIBLE

            }

        }
        alterDialog.setView(view)
        alterDialog.show()
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


    private fun myProfile() {
        val getIntent = activity?.intent
        val equalAddress = getIntent?.getStringExtra("id")
        if (equalAddress != null) {
            readProDB(equalAddress)
        }
    }

    private fun readProDB(check: String) {
        val data: MutableList<ProfileData> = proDbHelper.readData()
        if (data.isNotEmpty())
            Log.d("Read", "read")
        for (i in 0 until data.size) {
            if (check == data[i].id) {
                val image = data[i].img?.let { getImage(it) }
                binding.myImageView.setImageBitmap(image)
                binding.userNameTextView.text = data[i].name
                binding.userStateTextView.text = data[i].status
                userNick = data[i].id
            }
        }
    }

    private fun getImage(byteArray: ByteArray): Bitmap {
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        return bitmap
    }


    // 시작할 때
    private fun initRecycler() {
        friendsAdapter = FriendsAdapter(requireContext())

        datas.apply {
            val data: MutableList<ProfileData> = friendDBHelper.readData()
            if (data.isNotEmpty())
                Log.d("Read", "read")
            for (i in 0 until data.size) {
                val image = data[i].img?.let { getImage(it) }
                add(
                    FriendData(
                        image, data[i].name, data[i].id, data[i].address, data[i].status
                    )
                )
            }
        }
        friendsAdapter!!.datas = datas
        binding.countFriends.text = friendsAdapter!!.datas.size.toString()
        binding.myFriendRecyclerView.adapter = friendsAdapter
        //friendsAdapter!!.notifyDataSetChanged()

    }


}