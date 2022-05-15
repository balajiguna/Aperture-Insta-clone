package com.wolfython.aperture

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.wolfython.aperture.data.Event
import com.wolfython.aperture.data.PostData
import com.wolfython.aperture.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import kotlin.math.exp


const val USERS = "users"
const val POSTS= "posts"

@HiltViewModel
class IgViewModel @Inject constructor(
 val auth: FirebaseAuth,
 val db: FirebaseFirestore,
 val storage: FirebaseStorage

):ViewModel() {

 val signedIn = mutableStateOf(false)
 val inProgress = mutableStateOf(false)
 val userData = mutableStateOf<UserData?>(null)
 val popupNotification = mutableStateOf<Event<String>?>(null)

 val refreshPostProgress = mutableStateOf(false)
 val posts = mutableStateOf<List<PostData>>(listOf())


 init {
 // auth.signOut()
  val currentUser = auth.currentUser
  signedIn.value =currentUser !=null
  currentUser?.uid?.let { uid->
   getUserData(uid)
  }
 }

 fun onSignup(username: String, email : String, pass: String){

  if(username.isEmpty() or email.isEmpty() or pass.isEmpty() ){
   handleException(customMessage = "Please Fill In All Fields")
   return
  }
 inProgress.value = true

  db.collection(USERS).whereEqualTo("username",username).get()
   .addOnSuccessListener { documents ->
    if (documents.size() > 0){
         handleException(customMessage = "Username already exists")
     inProgress.value = false
    }else{
     auth.createUserWithEmailAndPassword(email,pass)
      .addOnCompleteListener { task ->
         if (task.isSuccessful){
          signedIn.value = true
            createOrUpdateProfile(username = username)
         }else{

          handleException(task.exception,"Signup failed")
         }
         inProgress.value = false
      }
    }

   }

   .addOnFailureListener {  }

 }

 fun onLogin(email: String,pass: String){

  if (email.isEmpty() or pass.isEmpty()){
   handleException(customMessage = "Please Fill In All Fields")
   return
  }
  inProgress.value = true
  auth.signInWithEmailAndPassword(email,pass)
   .addOnCompleteListener { task->

    if (task.isSuccessful){
     signedIn.value = true
     inProgress.value = false
     auth.currentUser?.uid?.let { uid ->
    //  handleException(customMessage = "Login Success")
      getUserData(uid)

     }

    }
    else{
     handleException(task.exception,"Login Failed")
     inProgress.value  = false
    }

   }

   .addOnFailureListener { exp->

    handleException(exp,"login failed")
    inProgress.value = false

   }

 }

 private fun createOrUpdateProfile(
  name: String? = null,
  username: String? = null,
  bio: String? = null,
  imageUrl: String? = null
 ){
  val uid = auth.currentUser?.uid
  val userData = UserData(
   userId = uid,
   name = name ?: userData.value?.name,
   username = username ?: userData.value?.username,
   bio = bio ?:  userData.value?.bio,
   imageUrl = imageUrl ?:  userData.value?.imageUrl,
   following =  userData.value?.following

  )

  uid?.let{
   inProgress.value = true
   db.collection(USERS).document(uid).get().addOnSuccessListener {

    if (it.exists()){
     it.reference.update(userData.toMap())
      .addOnSuccessListener {
       this.userData.value = userData
       inProgress.value = false
      }
      .addOnFailureListener {

       handleException(it, "Cannot update user")
       inProgress.value= false
      }
    }else{
     db.collection(USERS).document(uid).set(userData)
     getUserData(uid)
     inProgress.value = false
    }
   }
    .addOnFailureListener { exp ->
     handleException(exp, "Cannot create user")
     inProgress.value = false

    }

  }
 }
 private fun getUserData(uid: String){
  inProgress.value = true
  db.collection(USERS).document(uid).get()

   .addOnSuccessListener {
    val user = it.toObject<UserData>()
    userData.value = user
    inProgress.value = false
    refreshPost()
   }

   .addOnFailureListener { exc ->
    handleException(exc,"Cannot retrieve user data")
    inProgress.value = false
   }
 }

 fun handleException(exception: Exception? = null,customMessage: String=""){
  exception?.printStackTrace()
  val errorMsg = exception?.localizedMessage ?: ""
  val message = if (customMessage.isEmpty()) errorMsg else "$customMessage: $errorMsg"
  popupNotification.value = Event(message)
 }

 fun updateProfileData(name: String,username: String,bio: String){

  createOrUpdateProfile(name,username,bio)

 }

 fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit){
  inProgress.value=true
  val storageRef = storage.reference
  val uuid = UUID.randomUUID()
  val imagreRef = storageRef.child("image/$uuid")
  val uploadTask = imagreRef.putFile(uri)

  uploadTask.addOnSuccessListener {
   val result = it.metadata?.reference?.downloadUrl
   result?.addOnSuccessListener(onSuccess)

  }
   .addOnFailureListener { exc ->
     handleException(exc)
    inProgress.value = false

   }
 }

 fun uploadProfileImage(uri: Uri){

  uploadImage(uri){

   createOrUpdateProfile(imageUrl = it.toString())
  }
 }



 fun onLogout(){

  auth.signOut()
  signedIn.value = false
  userData.value = null
  popupNotification.value = Event("Logged out")

 }

 fun onNewPost(uri: Uri, description: String, onPostSuccess: () -> Unit){
  uploadImage(uri){

   onCreatePost(it, description, onPostSuccess)
  }


 }

 private fun onCreatePost(imageUrl: Uri, description: String, onPostSuccess: () -> Unit){

  inProgress.value = true
  val currentUid = auth.currentUser?.uid
  val currentUsername = userData.value?.username
  val currentUserImage = userData.value?.imageUrl

  if (currentUid != null){

val postUuid = UUID.randomUUID().toString()
   val post = PostData(
    postId = postUuid,
    userId = currentUid,
    username = currentUsername,
    userImage = currentUserImage,
    postImage = imageUrl.toString(),
    postDescription = description,
    time = System.currentTimeMillis()
   )

   db.collection(POSTS).document(postUuid).set(post)
    .addOnSuccessListener {
     popupNotification.value = Event("Post successfully created")
     inProgress.value = false
     refreshPost()
     onPostSuccess.invoke()
    }

    .addOnFailureListener{exc ->
     handleException(exc,"Unable to create post")
     inProgress.value = false
    }

  }else{

   handleException(customMessage = "Error: username unavailable")
   onLogout()
   inProgress.value = false
  }

 }

 private fun refreshPost(){
 val currentUid = auth.currentUser?.uid

  if (currentUid != null){

   refreshPostProgress.value = true
    db.collection(POSTS).whereEqualTo("userId",currentUid).get()
     .addOnSuccessListener { documents ->
      convertPosts(documents, posts)
      refreshPostProgress.value = false
     }
     .addOnFailureListener{exc ->

      handleException(exc, "Cannot Fetch posts")

      refreshPostProgress.value = false

     }

  }else{

   handleException(customMessage = "Error: username unavailable. Unable  to refresh posts")
  }
 }

 private fun convertPosts(documents: QuerySnapshot, outState: MutableState<List<PostData>>){
  val newPosts = mutableListOf<PostData>()
  documents.forEach{ doc ->
   val post = doc.toObject<PostData>()
   newPosts.add(post)

  }
  val sortedPost = newPosts.sortedByDescending { it.time }
  outState.value = sortedPost

 }

}