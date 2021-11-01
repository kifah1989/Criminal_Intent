package com.example.criminalintent

import com.google.firebase.Timestamp
import java.util.*

class Crime {
    var uid: String = ""
    var title: String = ""
    var date: Timestamp = Timestamp(Date())
    var time: Timestamp = Timestamp(Date())
    var isSolved: Boolean = false
    var requiresPolice: Boolean = false
    var suspect: String = ""
    var suspectPhoneNumber: String = ""

    constructor()

    constructor(
        uid: String,
        title: String,
        date: Timestamp,
        time: Timestamp,
        isSolved: Boolean,
        requiresPolice: Boolean,
        suspect: String,
        sPhone: String
    ) {
        this.uid = uid
        this.title = title
        this.date = date
        this.time = time
        this.isSolved = isSolved
        this.requiresPolice = requiresPolice
        this.suspect = suspect
        this.suspectPhoneNumber = sPhone
    }

    constructor(
        title: String,
        date: Timestamp,
        time: Timestamp,
        isSolved: Boolean,
        requiresPolice: Boolean,
        suspect: String,
        sPhone: String

    ) {
        this.title = title
        this.date = date
        this.time = time
        this.isSolved = isSolved
        this.requiresPolice = requiresPolice
        this.suspect = suspect
        this.suspectPhoneNumber = sPhone
    }
}
