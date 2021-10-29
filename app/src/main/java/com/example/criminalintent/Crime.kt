package com.example.criminalintent

import com.example.criminalintent.databinding.ListItemRequirePoliceCrimeBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.*

class Crime {
    var uid: String? = null
    var title: String? = null
    var date: Timestamp? = null
    var time: Timestamp? = null
    var isSolved: Boolean? = null
    var requiresPolice: Boolean? = null
    constructor()

    constructor(uid: String?, title: String?, date: Timestamp?, time: Timestamp?, isSolved: Boolean?, requiresPolice: Boolean?){
        this.uid = uid
        this.title = title
        this.date = date
        this.time = time
        this.isSolved = isSolved
        this.requiresPolice = requiresPolice
    }

    constructor(title: String?, date: Timestamp?, time: Timestamp?, isSolved: Boolean?, requiresPolice: Boolean?){
        this.title = title
        this.date = date
        this.time = time
        this.isSolved = isSolved
        this.requiresPolice = requiresPolice
    }
}
