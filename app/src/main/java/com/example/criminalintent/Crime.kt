package com.example.criminalintent

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.*

data class Crime(
    var
    id: String = "",
    var
    title: String = "",
    var
    date: Timestamp = Timestamp(Date()),
    var
    time: Timestamp = Timestamp(Date()),
    var
    isSolved: Boolean? = true,
    var
    requiresPolice: Boolean = true
){
    companion object {

        fun fromData(snapshot: QueryDocumentSnapshot): Crime {
            return Crime(
                id = snapshot.data["id"] as String,
                title = snapshot.data["title"] as String,
                date = snapshot.data["date"] as Timestamp,
                time = snapshot.data["time"] as Timestamp,
                isSolved = snapshot["isSolved"] as? Boolean,
                requiresPolice = snapshot["requiresPolice"] as Boolean

            )
        }

        fun fromDocument(doc: DocumentSnapshot): Crime {

            return Crime(
                id = doc["id"] as String,
                title = doc["title"] as String,
                date = doc["date"] as Timestamp,
                time = doc["time"] as Timestamp,
                isSolved = doc["isSolved"] as Boolean,
                requiresPolice = doc["requiresPolice"] as Boolean
            )
        }
    }
}
