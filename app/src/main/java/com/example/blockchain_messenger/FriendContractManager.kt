package com.example.blockchain_messenger

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.StaticGasProvider
import java.math.BigInteger
import java.util.concurrent.Future
import kotlin.concurrent.thread

class FriendContractManager private constructor() {

    private var profile = Array(4) { _ -> "" }
    private var check : Boolean = true

    private val contractAddress = "0xf05e6dd054f8cd9440c85f210c98f566004c3df0"
    private val url = "https://ropsten.infura.io/v3/87717aaaf9814ccb81e0c404644a1307"
    private val privatekey = "351a27fc9a4170ae0f6cfae743e3c6edf78ad575137d617cc314d77275223dff"
    private val gasLimit: BigInteger = BigInteger.valueOf(3000000)
    private val gasPrice: BigInteger = BigInteger.valueOf(3000000)
    private val gasProvider: ContractGasProvider = StaticGasProvider(gasPrice, gasLimit)

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: FriendContractManager? = null

        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
        fun getInstance(_context: Context): FriendContractManager {
            return instance ?: synchronized(this) {
                instance ?: FriendContractManager().also {
                    context = _context
                    instance = it
                }
            }
        }
    }

    fun connect(url: String) {
        val web3j = Web3j.build(HttpService(url))
        thread {
            val web3ClientVersion = web3j!!.web3ClientVersion().sendAsync().get()
            val clientVersion = web3ClientVersion.web3ClientVersion
            println("Connected to Ethereum client version: $clientVersion")
        }
    }

    @DelicateCoroutinesApi
    fun registerUser(nick: String, libp2pAddr:String, ethAddr: String,password: String, ipfs: String){
        val web3j = Web3j.build(HttpService(url))
        val credentials =
            org.web3j.crypto.Credentials.create(privatekey)
        val friendSC =
            FriendsManagement.load(contractAddress, web3j, credentials, gasProvider)
        val job = GlobalScope.launch(Dispatchers.Default) {
            val transactionReceipt =
                friendSC.set(nick,libp2pAddr,ethAddr,password,ipfs).sendAsync().get()
            val result = transactionReceipt?.blockNumber
            Log.d("BLOCKCHAIN", result.toString())
        }
        runBlocking {
            job.join()
        }
    }
    fun addFriend(user: String, frNick: String){
        val web3j = Web3j.build(HttpService(url))
        val credentials =
            org.web3j.crypto.Credentials.create(privatekey)
        val friendSC =
            FriendsManagement.load(contractAddress, web3j, credentials, gasProvider)

        val job = GlobalScope.launch(Dispatchers.Default) {
            val transactionReceipt  =
                friendSC.setFriend(user,frNick).sendAsync().get()
            val result = transactionReceipt?.gasUsed
            Log.d("Friend Add", result.toString())
        }
        runBlocking {
            job.join()
        }
    }

    @DelicateCoroutinesApi
    fun getUserProfile(
        nick: String
    ): Array<String> {
        val web3j = Web3j.build(HttpService(this.url))
        val credentials =
            org.web3j.crypto.Credentials.create(this.privatekey)
        val friendSC =
            FriendsManagement.load(contractAddress, web3j, credentials, this.gasProvider)
        val job = GlobalScope.launch(Dispatchers.Default) {
            val getEthAddr: String? = friendSC.getEthAddress(nick).sendAsync().get()
            val getLibp2pAddr: String? = friendSC.getlibp2pAddress(nick).sendAsync().get()
            val getIPFS: String? = friendSC.getIPFSHash(nick).sendAsync().get()

            profile = arrayOf(getEthAddr.toString(),getLibp2pAddr.toString(),getIPFS.toString())
        }
        runBlocking {
            job.join()
        }

        return profile
    }


    @DelicateCoroutinesApi
    fun checkUser(
        nick: String
    ): Boolean {
        val web3j = Web3j.build(HttpService(url))
        val credentials =
            org.web3j.crypto.Credentials.create(privatekey)
        val friendSC =
            FriendsManagement.load(contractAddress, web3j, credentials, gasProvider)
        val job = GlobalScope.launch(Dispatchers.Default) {
            val result = friendSC.contains(nick).sendAsync().get()
            check = result
            Log.d("Check", check.toString())
        }
        runBlocking {
            job.join()
        }

        return check
    }
    fun getPass(nick:String) : String{
        var result: String = ""
        val web3j = Web3j.build(HttpService(url))
        val credentials =
            org.web3j.crypto.Credentials.create(privatekey)
        val friendSC =
            FriendsManagement.load(contractAddress, web3j, credentials, gasProvider)
        val job = GlobalScope.launch(Dispatchers.Default) {
            result = friendSC.getPassword(nick).sendAsync().get()
            Log.d("Blockchain Password", result)
        }
        runBlocking {
            job.join()
        }

        return result
    }

    @DelicateCoroutinesApi
    fun checkLogin(
        nick: String
    ): String? {
        var result :String? = null
        val web3j = Web3j.build(HttpService(url))
        val credentials =
            org.web3j.crypto.Credentials.create(privatekey)
        val friendSC =
            FriendsManagement.load(contractAddress, web3j, credentials, gasProvider)
        val job = GlobalScope.launch(Dispatchers.Default) {
            result = friendSC.getEthAddress(nick).sendAsync().get().toString()
            Log.d("Result", result!!)
        }
        runBlocking {
            job.join()
        }

        return result
    }
}