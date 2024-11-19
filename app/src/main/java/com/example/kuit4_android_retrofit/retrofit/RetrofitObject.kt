package com.example.kuit4_android_retrofit.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//API와 통신
object RetrofitObject {
    // 서버의 기본 URL 설정
    // 모든 API 요청은 이 URL을 기준으로 경로가 추가됨
    private const val BASE_URL = "https://673c2cf896b8dcd5f3f8c318.mockapi.io/kuit/"

    // Retrofit 인스턴스 생성하고 초기화
    val retrofit: Retrofit =
        Retrofit.Builder()  // Retrofit Builder를 생성
            .baseUrl(BASE_URL)   // 기본 URL 설정
            .addConverterFactory(GsonConverterFactory.create())
                    // JSON 데이터를 코틀린 객체로 변환하기 위한 Gson 컨버터 추가
            .build()  // Retrofit 객체 생성
}