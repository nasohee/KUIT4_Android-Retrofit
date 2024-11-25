package com.example.kuit4_android_retrofit.retrofit.service

import com.example.kuit4_android_retrofit.data.CategoryData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoryService {
    @GET ("category")
    fun getCategories() : Call<List<CategoryData>>

     @POST("category")
    fun postCategory(
        @Body menu: CategoryData
    ): Call<CategoryData>

    @PUT("category/{id}")
    fun putCategory(
        @Path("id") id: String,
        @Body menu: CategoryData
    ): Call<CategoryData>

    @DELETE("category/{id}")
    fun deleteCategory(
        @Path("id") id: String
    ): Call<Void>
}