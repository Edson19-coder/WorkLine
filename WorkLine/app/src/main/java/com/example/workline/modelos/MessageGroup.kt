package com.example.workline.modelos

import com.google.firebase.database.Exclude

class MessageGroup (
        var id: String = "",
        var content: String = "",
        var emitter: String = "",
        var created_at: String = "",
        var nameUser: String = "",
        var nameGroup: String = ""
) {
    @Exclude
    var mine: Boolean = false
}
