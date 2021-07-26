package com.example.mygram.models

class User {
    var name: String? = null
    var photoUrl: String? = null
    var gmail: String? = null
    var uid: String? = null
    var status: String? = null

    constructor()
    constructor(name: String?, photoUrl: String?, gmail: String?, uid: String?, status: String?) {
        this.name = name
        this.photoUrl = photoUrl
        this.gmail = gmail
        this.uid = uid
        this.status = status
    }


}