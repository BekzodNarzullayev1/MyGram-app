package com.example.mygram.models

class Chat {
    var sender:String?=null
    var receiver:String?=null
    var message:String?=null
    var date:String?=null

    constructor()

    constructor(sender: String?, receiver: String?, message: String?, date: String?) {
        this.sender = sender
        this.receiver = receiver
        this.message = message
        this.date = date
    }


}