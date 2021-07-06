package com.example.pocket.viewmodels


interface LoginViewModel {
    fun authenticate(emailAddress:String, password:String): Boolean

}

class LoginViewModelImpl : LoginViewModel {


    override fun authenticate(emailAddress: String, password: String): Boolean {
        TODO()

    }
}