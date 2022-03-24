package com.example.blockchain_messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.blockchain_messenger.databinding.FragmentFriendsBinding
import com.example.blockchain_messenger.databinding.FragmentSettingBinding
import com.google.firebase.auth.FirebaseAuth

class SetFragment : Fragment() {
    lateinit var binding: FragmentSettingBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser!!

        binding.logoutButton.setOnClickListener {
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("LOGOUT", "User account deleted.")
                    }
                }
            auth.signOut()
            val nextIntent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(nextIntent)
        }
        return binding.root
    }

}