package com.example.blockchain_messenger

import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.example.blockchain_messenger.databinding.FragmentChatBinding
import com.example.blockchain_messenger.databinding.FragmentFriendsBinding
import java.util.concurrent.CompletableFuture

class ChatFragment : Fragment(){

    private lateinit var chatScroller: ScrollView
    private lateinit var chatWindow: TextView
    private lateinit var line: EditText
    private lateinit var sendButton: Button
    private lateinit var chatNode: ChatNode
    private lateinit var multicastLock: WifiManager.MulticastLock
    lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        chatScroller = binding.chatScroll
        chatWindow = binding.chat
        line =binding.line
        sendButton = binding.send

        CompletableFuture.runAsync {
            acquireMulticastLock()

            chatNode = ChatNode(::chatMessage)
            sendButton.setOnClickListener { sendText() }
            chatMessage("\nLibp2p Chatter!\n=============\n")
            chatMessage("This node is ${chatNode.peerId}, listening on ${chatNode.address}\n")
        }
        return binding.root
    }
/*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }*/
    override fun onDestroy() {
        super.onDestroy()

        releaseMulticastLock()
        chatNode.stop()
    } // onDestroy

    private fun sendText() {
        val msg = line.text.toString().trim()
        Log.d("ChatActivity", chatNode.peerId.toString());
        if (msg.isEmpty())
            return
        // chatNode = ChatNode(::chatMessage)
        // send message here
        chatNode.send(msg)

        chatMessage("You > $msg")

        line.text.clear()
    } // sendText

    private fun chatMessage(msg: String) {
        runOnUiThread {
            chatWindow.append(msg)
            chatWindow.append("\n")
            chatScroller.post { chatScroller.fullScroll(View.FOCUS_DOWN) }
        }
    } // chatMessage

    private fun acquireMulticastLock() {
        val wifi = activity?.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        multicastLock = wifi.createMulticastLock("libp2p-chatter")
        multicastLock.acquire()
    }

    private fun releaseMulticastLock() {
        multicastLock.release()
    }
}