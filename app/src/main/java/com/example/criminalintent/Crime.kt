package com.example.criminalintent

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*
@IgnoreExtraProperties
class Crime (
    @set:Exclude @get:Exclude
    var uid: String = "",
    var barcode:String = "",
    var title: String = "",
    var date: Timestamp = Timestamp(Date()),
    var time: Timestamp = Timestamp(Date()),
    var isSolved: Boolean = false,
    var requiresPolice: Boolean = false,
    var suspect: String = "",
    var suspectPhoneNumber: String = "",
    var photoRemoteUrl: String = ""
)

