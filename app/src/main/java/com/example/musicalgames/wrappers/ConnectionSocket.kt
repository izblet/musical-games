package com.example.musicalgames.wrappers
interface ConnectionSocketListener {
    fun onMessage(i: Int)
}
interface ConnectionSocket {
    fun sendMessage(i: Int)
}