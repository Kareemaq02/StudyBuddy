package com.example.studybuddy.data

class Request(
    var requestId: String? = null, //this saves the key
    val requestTitle: String,
    val requestDescription: String,
    var requeststate : Int,
    val requesterkey : String,




) {
    // Add a default constructor with no arguments
    constructor() : this("", "", "",requesterkey = "",requeststate = 1 )
}