package com.example.scoutquest.viewmodels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User


class LoginViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    private val userList = mutableListOf<User>()

    fun getUsers() {
        val db = FirebaseFirestore.getInstance()

        db.collection("user").get()
            .addOnCompleteListener { user ->
                if (user.isSuccessful) {
                    userList.clear()
                    for (document in user.result) {
                        val user = document.toObject(User::class.java)
                        userList.add(user)
                    }
                    // userAdapter
                } else {
                    //
                }
            }
    }

    fun updateUsers(){


    }

}
