
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kuit4_android_retrofit.R
import com.example.kuit4_android_retrofit.data.CategoryData
import com.example.kuit4_android_retrofit.databinding.ItemCategoryBinding

class RVPopularMenuAdapter(
    private val menuList: List<CategoryData>,
) : RecyclerView.Adapter<RVPopularMenuAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemCategoryBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryData) {
            binding.sivCategoryImg.setImageResource(R.drawable.img_pizza)
            binding.tvCategoryName.text = item.categoryName
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(menuList[position])
    }

    override fun getItemCount(): Int = menuList.size
}
