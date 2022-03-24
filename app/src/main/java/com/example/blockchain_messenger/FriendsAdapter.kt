package com.example.blockchain_messenger

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FriendsAdapter(private val context: Context) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>(){

    var datas = mutableListOf<FriendData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_friends,parent,false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val friendName: TextView = itemView.findViewById(R.id.friendsNameTextView)
        private val nickName: TextView = itemView.findViewById(R.id.nicknameTextView)
        private val ethAddr: TextView = itemView.findViewById(R.id.EthereumAddrTextView)
        private val friendState: TextView = itemView.findViewById(R.id.friendStateTextView)
        private val friendImage: ImageView = itemView.findViewById(R.id.FriendImageView)

        fun bind(item: FriendData) {
            friendName.text = item.userName
            nickName.text = item.userNickname
            ethAddr.text = item.ethAddr
            friendState.text = item.userState
            Glide.with(itemView).load(item.userProfileImageUrl).into(friendImage)

        }
    }
}
