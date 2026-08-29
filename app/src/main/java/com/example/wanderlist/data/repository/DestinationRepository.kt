package com.example.wanderlist.data.repository

import com.example.wanderlist.data.model.Destination
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DestinationRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("destinations")

    private fun currentUserId(): String? = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun getAllDestinations(): List<Destination> {
        val uid = currentUserId() ?: return emptyList()
        val snapshot = collection.whereEqualTo("userId", uid).get().await()
        return snapshot.toObjects(Destination::class.java)
    }

    suspend fun addDestination(destination: Destination) {
        val uid = currentUserId() ?: return
        val docRef = collection.document()
        val withId = destination.copy(id = docRef.id, userId = uid)
        docRef.set(withId).await()
    }

    suspend fun updateDestination(destination: Destination) {
        collection.document(destination.id).set(destination).await()
    }

    suspend fun deleteDestination(id: String) {
        collection.document(id).delete().await()
    }
}