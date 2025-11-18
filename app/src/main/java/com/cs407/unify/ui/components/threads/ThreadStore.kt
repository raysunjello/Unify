package com.cs407.unify.ui.components.threads

import java.io.Serializable

data class Thread(
    val title: String,
    val body: String,
    val hub: String // TODO : change to backend organization??
    // TODO : add comment section
) : Serializable

object ThreadStore {
    val threads: HashMap<String, Thread> = hashMapOf()
}
//// Object to hold and manage favorite cards
//object FavoriteStore {
//    val favorites: ArrayList<Thread> = arrayListOf()
//    fun add(entry: Thread) {
//        if (favorites.none { it.name == entry.name
//                    && it.hobby == entry.hobby
//                    && it.age == entry.age }) {
//            favorites.add(entry)
//        }
//    }
//    fun remove(entry: CardEntry) {
//        favorites.removeAll { it.name == entry.name
//                && it.hobby == entry.hobby
//                && it.age == entry.age }
//    }
//}

