package com.example.kuit4_android_retrofit.retrofit.service

import com.example.kuit4_android_retrofit.data.CategoryData
import com.example.kuit4_android_retrofit.data.MenuData
import retrofit2.Call
import retrofit2.http.GET

interface MenuService {
    @GET("menu")
    fun getMenus() : Call<List<MenuData>>
}