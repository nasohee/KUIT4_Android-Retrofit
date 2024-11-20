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
) : RecyclerView.Adapter<RVPopularMenuAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemPopularMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // 데이터 뷰에 연결
        @SuppressLint("SetTextI18n")
        fun bind(menu: MenuData) {
            Glide.with(binding.root)
                .load(menu.menuImg)
                .into(binding.ivPopularMenuImg)
            binding.tvPopularMenuName.text = menu.menuName
            binding.tvPopularMenuTime.text = menu.menuTime.toString() + "분"
            binding.tvPopularMenuRate.text = menu.menuRate.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPopularMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menuList[position])
    }

    override fun getItemCount(): Int = menuList.size
}
