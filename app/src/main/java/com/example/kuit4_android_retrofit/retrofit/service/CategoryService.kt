package com.example.kuit4_android_retrofit.retrofit.service

import com.example.kuit4_android_retrofit.data.CategoryData
import retrofit2.Call
import retrofit2.http.GET

interface CategoryService {
    @GET ("category")
    fun getCategories() : Call<List<CategoryData>>

}