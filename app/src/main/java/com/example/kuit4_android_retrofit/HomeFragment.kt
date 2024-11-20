package com.example.kuit4_android_retrofit

import RVPopularMenuAdapter
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kuit4_android_retrofit.data.CategoryData
import com.example.kuit4_android_retrofit.data.MenuData
import com.example.kuit4_android_retrofit.databinding.FragmentHomeBinding
import com.example.kuit4_android_retrofit.databinding.ItemCategoryBinding
import com.example.kuit4_android_retrofit.databinding.ItemPopularMenuBinding
import com.example.kuit4_android_retrofit.retrofit.RetrofitObject
import com.example.kuit4_android_retrofit.retrofit.service.CategoryService
import com.example.kuit4_android_retrofit.retrofit.service.MenuService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var popularMenuAdapter: RVPopularMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)


        fetchCategoryInfo()
        fetchMenuInfo()
        return binding.root
    }

    private fun fetchMenuInfo() {
        val service = RetrofitObject.retrofit.create(MenuService::class.java)
        val call = service.getMenus()

        call.enqueue(
            object : retrofit2.Callback<List<MenuData>> {
                override fun onResponse(
                    call: Call<List<MenuData>>,
                    response: Response<List<MenuData>>
                ) {
                    if (response.isSuccessful) {
                        val menuResponse = response.body()
                        if (!menuResponse.isNullOrEmpty()) {
                            showMenuInfo(menuResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<List<MenuData>>, t: Throwable) {
                    Log.d("MenuFetchError", "메뉴 데이터 가져오는 중")

                }

            }
        )
    }

    private fun showMenuInfo(menuList: List<MenuData>) {
        val adapter = RVPopularMenuAdapter(menuList)  // 메뉴 데이터 리스트 어댑터에 전달해서 어댑터 객체 생성
        binding.rvMainPopularMenus.layoutManager = LinearLayoutManager(context)
        binding.rvMainPopularMenus.adapter = adapter
    }


    // 카테고리 정보 가져오는 함수
    private fun fetchCategoryInfo() {
        // Retrofit 설정 객체(RetrofitObject) 사용
        // Retrofit 라이브러리의 인스턴스로, baseUrl/JSON 변환기 설정
        val service = RetrofitObject.retrofit.create(CategoryService::class.java)
        //API 호출 준비
        val call = service.getCategories()

        call.enqueue(
            object : retrofit2.Callback<List<CategoryData>> {
                override fun onResponse(
                    call: Call<List<CategoryData>>,
                    response: Response<List<CategoryData>>
                ) {
                    // 서버 응답 성공 시
                    if (response.isSuccessful) {
                        val categoryResponse = response.body()  // 응답 데이터 가져옴
                        // 데이터가 성공적으로 받아와졌을 때, 즉 데이터 비어있지 않다면
                        if (!categoryResponse.isNullOrEmpty()) {
                            showCategoryInfo(categoryResponse)  // 데이터 화면에 표시하는 함수 호출
                        } else {
                            Log.d("실패1", "실패1")  // 빈 값을 받아온 경우
                        }
                    } else {
                        Log.d("실패2", "실패2") // 서버에서 응답이 실패한 경우 (상태코드 5XX)
                    }
                }

                override fun onFailure(call: Call<List<CategoryData>>, t: Throwable) {
                    Log.d("실패3", "실패3") // 네트워크 ? 오류
                }
            }
        )
    }

    private fun showCategoryInfo(categoryList: List<CategoryData>) {
        // 레이아웃 인플레이터를 사용해 카테고리 항목을 동적으로 추가
        val inflater = LayoutInflater.from(requireContext())
        binding.llMainMenuCategory.removeAllViews() // 기존 항목 제거

        categoryList.forEach { category ->
            val categoryBinding =
                ItemCategoryBinding.inflate(inflater, binding.llMainMenuCategory, false)

            // 이미지 로딩: Glide 사용 (이미지 URL을 ImageView에 로드)
            Glide
                .with(this)
                .load(category.categoryImg)
                .into(categoryBinding.sivCategoryImg)

            // 카테고리 이름 설정
            categoryBinding.tvCategoryName.text = category.categoryName

            // 레이아웃에 카테고리 항목 추가
            binding.llMainMenuCategory.addView(categoryBinding.root)
        }
    }

}
