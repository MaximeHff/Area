package com.example.area.repository

import com.example.area.model.RegisterFields
import com.example.area.model.Token
import com.example.area.api.RetrofitInstance
import com.example.area.model.AREAFields
import com.example.area.model.LoginFields
import retrofit2.Response

class Repository(val URL_INPUT : String) {
    suspend fun register(registerFields: RegisterFields) : Response<Token> {
        return RetrofitInstance(URL_INPUT).api.register(registerFields)
    }
    suspend fun login(loginFields: LoginFields) : Response<Token> {
        return RetrofitInstance(URL_INPUT).api.login(loginFields)
    }
<<<<<<< HEAD
    suspend fun getUserAreaList(): Response<Token> {
        return RetrofitInstance(URL_INPUT).api.getUserAreaList()
=======
    suspend fun areaCreation(areaFields: AREAFields) : Response<Token> {
        return RetrofitInstance(URL_INPUT).api.createArea(areaFields)
>>>>>>> 49ff3367574ac1544fc6bf51396f5f4b5fa433be
    }
}