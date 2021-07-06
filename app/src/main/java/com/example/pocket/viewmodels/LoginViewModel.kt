package com.example.pocket.viewmodels


interface LoginViewModel {
    fun authenticate(userName:String,password:String): Boolean

}

class LoginViewModelImpl : LoginViewModel {


    override fun authenticate(userName: String, password: String): Boolean {
        TODO()

    }
}