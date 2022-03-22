package com.app.aline.data.models

import java.util.*

data class MessageUser (
    var user: User,
    var message:String?,
    var dateStr: String?,
    var date: Date?,
)