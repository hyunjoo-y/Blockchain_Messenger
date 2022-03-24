package com.example.blockchain_messenger

import android.graphics.Bitmap

data class FriendData(
    var userProfileImageUrl : Bitmap?= null,
    var userName : String ?= null,
    var userNickname: String ?= null,
    var ethAddr: String ?= null,
    var userState : String ?= null
)

