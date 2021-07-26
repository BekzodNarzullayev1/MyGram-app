package com.example.mygram.models

class GroupChat {
    var sender:String?=null
    var groupId:String?=null
    var message:String?=null
    var date:String?=null

    constructor()

    constructor(
        sender: String?,
        groupId: String?,
        message: String?,
        date: String?
    ) {
        this.sender = sender
        this.groupId = groupId
        this.message = message
        this.date = date
    }


}