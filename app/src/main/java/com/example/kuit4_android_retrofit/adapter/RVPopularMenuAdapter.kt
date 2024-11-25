import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kuit4_android_retrofit.data.MenuData
import com.example.kuit4_android_retrofit.databinding.ItemPopularMenuBinding

// 메뉴 아이템 데이터를 리사이클러뷰의 각 항목에 표시해줌
class RVPopularMenuAdapter(
    private val menuList: List<MenuData>,
    private val onItemClick: (MenuData) -> Unit // 클릭 리스너 전달
) : RecyclerView.Adapter<RVPopularMenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(private val binding: ItemPopularMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: MenuData) {
            binding.tvPopularMenuName.text = menu.menuName
            Glide.with(binding.root.context).load(menu.menuImg).into(binding.ivPopularMenuImg)

            binding.root.setOnClickListener {
                onItemClick(menu)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemPopularMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menuList[position])
    }

    override fun getItemCount() = menuList.size
}