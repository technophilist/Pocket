package com.example.pocket.utils

import android.net.Uri
import com.example.pocket.auth.AuthServiceAccountCreationException
import com.example.pocket.auth.AuthServiceUserCollisionException
import com.example.pocket.auth.AuthServiceWeakPasswordException
import com.google.firebase.auth.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * This extension method is used for creating a [FirebaseUser] with [name],[email],[password] and profile picture ([profilePhotoUri]).
 * This method uses [suspendCancellableCoroutine] and resumes with an instance of [FirebaseUser] if the user was created successfully.
 * Else, it resumes with an instance of one of the subclasses of [com.example.pocket.auth.AuthenticationServiceException]
 *
 * Firebase doesn't provide a default method to create a user along with a display name and profile photo.In order to perform such a
 * task we need to chain two methods - [FirebaseAuth.createUserWithEmailAndPassword] and [FirebaseAuth.updateCurrentUser].
 */
suspend fun FirebaseAuth.createUser(
    name: String,
    email: String,
    password: String,
    profilePhotoUri: Uri?
): FirebaseUser = suspendCancellableCoroutine { cancellableContinuation ->
    createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { createUserTask ->
            if (createUserTask.isSuccessful) {

                //if user is created successfully, set the display name and profile picture
                val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(profilePhotoUri)
                    .build()

                currentUser?.updateProfile(userProfileChangeRequest)?.addOnCompleteListener { updateProfileTask ->
                        if (updateProfileTask.isSuccessful) {
                            //if the update task is successful, return the firebase user
                            cancellableContinuation.resume(currentUser!!)
                        } else {
                            val exception = AuthServiceAccountCreationException(
                                message = "An error occurred while updating the firebase user object",
                                cause = updateProfileTask.exception
                            )
                            cancellableContinuation.resumeWithException(exception)
                        }
                    }
            } else {

                //if there is an error creating the user, resume with exception
                cancellableContinuation.resumeWithException(when(createUserTask.exception){
                    is FirebaseAuthUserCollisionException -> {
                        AuthServiceUserCollisionException(cause = createUserTask.exception)
                    }
                    is FirebaseAuthWeakPasswordException->{
                        AuthServiceWeakPasswordException(
                            message = createUserTask.exception?.message ?: "Weak Password",
                            cause = createUserTask.exception
                        )
                    }
                    else -> {
                        AuthServiceAccountCreationException(
                            message = "Unable to create account",
                            cause = createUserTask.exception
                        )
                    }
                })
            }
        }
}
