package com.example.chatappfirebase

data class ChatMessage(
                       val text: String? ="",
                       val fromId: String? ="",
                       val toId: String?  ="",
                       val timestamp: Long? = 1) {
}