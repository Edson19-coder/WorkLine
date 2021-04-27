package com.example.workline.modelos

import com.google.firebase.database.Exclude

class Message(
    var id: String = "",
    var content: String = "",
    var emitter: String = "",
    var created_at: String = "",
    var nameChat: String = "",
    var emmiterName: String = ""
) {
    @Exclude
    var mine: Boolean = false
}