package com.example.criminalintent

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.*

data class Crime(
    var
    uid: String? = null,
    var
    title: String? = null,
    var
    date: Timestamp?= null,
    var
    time: Timestamp?= null,
    var
    isSolved: Boolean?= null,
    var
    requiresPolice: Boolean?= null
){
    companion object {

        fun fromData(snapshot: QueryDocumentSnapshot): Crime {
            return Crime(
                uid = snapshot.id,
                title = snapshot.data["title"] as? String,
                date = snapshot.data["date"] as? Timestamp,
                time = snapshot.data["time"] as? Timestamp,
                isSolved = snapshot["isSolved"] as? Boolean,
                requiresPolice = snapshot["requiresPolice"] as? Boolean

            )
        }

        fun fromDocument(doc: DocumentSnapshot): Crime {

            return Crime(
                uid = doc.id,
                title = doc["title"] as? String,
                date = doc["date"] as? Timestamp,
                time = doc["time"] as? Timestamp,
                isSolved = doc["isSolved"] as? Boolean,
                requiresPolice = doc["requiresPolice"] as? Boolean
            )
        }
    }
}
