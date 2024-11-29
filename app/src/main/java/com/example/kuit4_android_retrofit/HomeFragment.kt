package com.example.kuit4_android_retrofit

import RVPopularMenuAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kuit4_android_retrofit.data.CategoryData
import com.example.kuit4_android_retrofit.data.MenuData
import com.example.kuit4_android_retrofit.databinding.DialogAddCategoryBinding
import com.example.kuit4_android_retrofit.databinding.DialogAddMenuBinding
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

        binding.ivAddCategory.setOnClickListener() {
            addCategoryDialog()
        }
        binding.ivAddMenu.setOnClickListener() {
            addMenuDialog()
        }
        return binding.root
    }

    private fun addMenuDialog() {
        val dialogBinding = DialogAddMenuBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnAddMenu.setText("추가")
        dialogBinding.btnAddMenu.setOnClickListener {
            val menuName = dialogBinding.etMenuName.text.toString().trim()
            val menuImageUrl = dialogBinding.etMenuImageUrl.text.toString().trim()
            val menuRate = dialogBinding.etMenuRate.text.toString().trim()
            val menuTime = dialogBinding.etMenuTime.text.toString()+"분".trim()

            if (menuName.isNotEmpty() && menuImageUrl.isNotEmpty()) {
                val newMenu = MenuData(
                    menuName,
                    menuImageUrl,
                    menuTime.toInt(),
                    menuRate.toDouble(),
                    ""
                )
                addMenu(newMenu)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.btnCancelMenu.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    /// 메뉴 추가
    private fun addMenu(menuData: MenuData) {
        val service = RetrofitObject.retrofit.create(MenuService::class.java)
        val call = service.postMenu(menuData) // 서버에 메뉴 추가 요청

        call.enqueue(object : retrofit2.Callback<MenuData> {
            override fun onResponse(call: Call<MenuData>, response: Response<MenuData>) {
                val addedMenu = response.body()
                if (response.isSuccessful) { // 응답 성공하면
                    Log.d("성공", "메뉴 추가 성공 : $addedMenu")
                    fetchMenuInfo()  // 메뉴 정보 새로고침
                } else {
                    Log.e("Error", "메뉴 추가 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MenuData>, t: Throwable) {
                Log.e("Error", "네트워크 오류: ${t.message}")
            }
        })
    }

    private fun showEditMenuDialog(menu: MenuData) {
        val dialogBinding = DialogAddMenuBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog =
            AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()
        // 기존 데이터로 다이얼로그 초기화
        dialogBinding.etMenuName.setText(menu.menuName)
        dialogBinding.etMenuImageUrl.setText(menu.menuImg)
        dialogBinding.etMenuRate.setText(menu.menuRate.toString())
        dialogBinding.etMenuTime.setText(menu.menuTime.toString())


        dialogBinding.btnAddMenu.text = "수정"
        // 수정 버튼 클릭 시
        dialogBinding.btnAddMenu.setOnClickListener {
            val updatedName =
                dialogBinding.etMenuName.text.toString().trim()
            val updatedImageUrl =
                dialogBinding.etMenuImageUrl.text.toString().trim()
            val updatedRate =
                dialogBinding.etMenuRate.text.toString().trim()
            val updatedTime =
                dialogBinding.etMenuTime.text.toString()+"분".trim()

            // 필드가 모두 채워졌는지 확인 후 메뉴 수정
            if (updatedName.isNotEmpty() && updatedImageUrl.isNotEmpty()) {
                val updatedMenu =
                    MenuData(
                        updatedName,
                        updatedImageUrl,
                        updatedTime.toInt(),
                        updatedRate.toDouble(),
                        menu.id
                    )
                updateMenu(updatedMenu) // updateMenu 함수 호출
                dialog.dismiss()// 다이얼로그 닫기
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.btnCancelMenu.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // 메뉴 수정
    private fun updateMenu(menuData: MenuData) {
        val service = RetrofitObject.retrofit.create(MenuService::class.java)
        val call = service.putMenu(menuData.id, menuData) // 서버에 메뉴 수정 요청

        call.enqueue(object : retrofit2.Callback<MenuData> {
            override fun onResponse(call: Call<MenuData>, response: Response<MenuData>) {
                if (response.isSuccessful) {
                    fetchMenuInfo() // 메뉴 정보 새로고침
                    Log.e("성공", "메뉴 수정 성공: ${response.body()}")
                } else {
                    Log.e("Error", "메뉴 수정 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MenuData>, t: Throwable) {
                Log.e("Error", "네트워크 오류: ${t.message}")
            }
        })
    }

    // 메뉴 삭제
    private fun deleteMenu(menuId: String) {
        val service = RetrofitObject.retrofit.create(MenuService::class.java)
        val call = service.deleteMenu(menuId) // 서버에 메뉴 삭제 요청

        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    fetchMenuInfo() // 메뉴 정보 새로고침

                } else {
                    Log.e("실패", "메뉴 삭제 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("실패", "네트워크 오류: ${t.message}")
            }
        })
    }

    private fun showMenuOptionsDialog(menu: MenuData) {
        val options = arrayOf("수정", "삭제")

        AlertDialog.Builder(requireContext())
            .setTitle("메뉴 옵션")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditMenuDialog(menu) // 수정
                    1 -> deleteMenu(menu.id) // 삭제
                }
            }.show()
    }

    private fun showCategoryOptionsDialog(category: CategoryData) {
        val options = arrayOf("수정", "삭제")

        AlertDialog
            .Builder(requireContext())
            .setTitle("카테고리 옵션")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditCategoryDialog(category) // 수정
                    1 -> deleteCategory(category.id) // 삭제
                }
            }.show()
    }

    private fun showEditCategoryDialog(category: CategoryData) {
        val dialogBinding = DialogAddCategoryBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog =
            AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

        // 기존 데이터로 다이얼로그 초기화
        dialogBinding.etCategoryName.setText(category.categoryName)
        dialogBinding.etCategoryImageUrl.setText(category.categoryImg)

        // "수정" 버튼 클릭 시
        dialogBinding.btnAddCategory.text = "수정"
        dialogBinding.btnAddCategory.setOnClickListener {
            val updatedName =
                dialogBinding.etCategoryName.text
                    .toString()
                    .trim()
            val updatedImageUrl =
                dialogBinding.etCategoryImageUrl.text
                    .toString()
                    .trim()

            if (updatedName.isNotEmpty() && updatedImageUrl.isNotEmpty()) {
                // TODO: 수정할 데이터 설정하기
                val updatedCategory = CategoryData(updatedName, updatedImageUrl, category.id)

                updatedCategory(updatedCategory)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // "취소" 버튼 클릭 시
        dialogBinding.btnCancelCategory.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updatedCategory(updatedCategory: CategoryData) {
        val service = RetrofitObject.retrofit.create(CategoryService::class.java)
        val call = service.putCategory(updatedCategory.id, updatedCategory)

        call.enqueue(
            object : retrofit2.Callback<CategoryData> {
                override fun onResponse(
                    call: Call<CategoryData>,
                    response: Response<CategoryData>
                ) {
                    if (response.isSuccessful) {
                        Log.d("성공", "카테고리 수정 성공 : ${response.body()}")
                        fetchCategoryInfo()
                    } else {
                        Log.d("성공", "카테고리 수정 실패 : ${response.body()}")
                    }
                }

                override fun onFailure(call: Call<CategoryData>, t: Throwable) {
                    Log.d("실패", "네트워크 연결 실패 : ${t.message}")
                }

            }
        )
    }

    private fun deleteCategory(categoryId: String) {
        val service = RetrofitObject.retrofit.create(CategoryService::class.java)
        val call = service.deleteCategory(categoryId)

        call.enqueue(
            object : retrofit2.Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("성공", "카테고리 삭제 성공 : $categoryId")
                        fetchCategoryInfo()
                    } else {
                        Log.d("실패", "카테고리 삭제 실패 : ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("실패", "네트워크 요청 실패 : ${t.message}")
                }

            }
        )
    }

    private fun addCategoryDialog() {
        // ViewBinding을 활용해 dialog_add_category 레이아웃 바인딩
        val dialogBinding = DialogAddCategoryBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog =  // dialog 생성
            AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

        // "추가" 버튼 클릭 시 동작
        dialogBinding.btnAddCategory.setOnClickListener {
            val categoryName =
                dialogBinding.etCategoryName.text
                    .toString()
                    .trim()
            val categoryImageUrl =
                dialogBinding.etCategoryImageUrl.text
                    .toString()
                    .trim()

            if (categoryName.isNotEmpty() && categoryImageUrl.isNotEmpty()) {
                // TODO: 데이터 설정하기
                val newCategory = CategoryData(categoryName, categoryImageUrl, "0")
                addCategory(newCategory)

                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // "취소" 버튼 클릭 시 동작
        dialogBinding.btnCancelCategory.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addCategory(categoryData: CategoryData) {
        val service = RetrofitObject.retrofit.create(CategoryService::class.java)
        val call = service.postCategory(categoryData)

        call.enqueue(
            object : retrofit2.Callback<CategoryData> {
                override fun onResponse(
                    call: Call<CategoryData>,
                    response: Response<CategoryData>
                ) {
                    if (response.isSuccessful) {
                        val addedCategory = response.body()

                        if (addedCategory != null) {
                            Log.d("성공", "카테고리 추가 성공 : $addedCategory")
                            fetchCategoryInfo()
                        } else {
                            Log.d("실패", "카테고리 추가 실패 : 응답 데이터 없음")
                        }
                    } else {
                        Log.d("실패", "카테고리 추가 실패 : 상태코드 ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<CategoryData>, t: Throwable) {
                    Log.d("실패", "네트워크 요청 실패 : 상태코드 ${t.message}")

                }

            }

        )
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
        val adapter = RVPopularMenuAdapter(menuList) { menu ->
            showMenuOptionsDialog(menu) // 클릭 이벤트 발생 시 다이얼로그 호출
        }
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

            categoryBinding.root.setOnClickListener() {
                showCategoryOptionsDialog(category)
            }
            // 레이아웃에 카테고리 항목 추가
            binding.llMainMenuCategory.addView(categoryBinding.root)
        }
    }

}