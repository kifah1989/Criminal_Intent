package com.example.criminalintent

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.*

data class Crime(
    var
    uid: String? = "",
    var
    title: String? = "",
    var
    date: Timestamp?= Timestamp(Date()),
    var
    time: Timestamp?= Timestamp(Date()),
    var
    isSolved: Boolean? = false,
    var
    requiresPolice: Boolean = false
){
    companion object {

        fun fromData(snapshot: QueryDocumentSnapshot): Crime {
            return Crime(
                uid = snapshot.id,
                title = snapshot.data["title"] as? String,
                date = snapshot.data["date"] as? Timestamp,
                time = snapshot.data["time"] as? Timestamp,
                isSolved = snapshot.data["solved"] as? Boolean,
                requiresPolice = snapshot.data["requiresPolice"] as Boolean

            )
        }

        fun fromDocument(doc: DocumentSnapshot): Crime {

            return Crime(
                uid = doc.id,
                title = doc["title"] as? String,
                date = doc["date"] as? Timestamp,
                time = doc["time"] as? Timestamp,
                isSolved = doc["solved"] as? Boolean,
                requiresPolice = doc["requiresPolice"] as Boolean
            )
        }
    }
}
