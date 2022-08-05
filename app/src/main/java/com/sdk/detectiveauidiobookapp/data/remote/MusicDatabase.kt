package com.sdk.detectiveauidiobookapp.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.sdk.detectiveauidiobookapp.data.model.Story
import com.sdk.detectiveauidiobookapp.util.Constants.SONG_COLLECTION
import kotlinx.coroutines.tasks.await

class MusicDatabase {
    private val fireStore = FirebaseFirestore.getInstance()
    private val songCollection = fireStore.collection(SONG_COLLECTION).orderBy("mediaId")

    suspend fun getAllSongs(): List<Story> {
        return try {
            songCollection.get().await().toObjects(Story::class.java)
        } catch(e: Exception) {
            emptyList()
        }
    }
}