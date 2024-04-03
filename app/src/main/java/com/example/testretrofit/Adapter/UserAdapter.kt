import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testretrofit.Model.User
import com.example.testretrofit.Model.UserWithId
import com.example.testretrofit.R

class UserAdapter(
    private val users: List<UserWithId>,
    private val onDeleteClickListener: OnDeleteClickListener,
    private val onEditClickListener: OnEditClickListener
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(), Filterable {
    private var filteredUsers: List<UserWithId> = users

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
        holder.ivDelete.setOnClickListener {
            onDeleteClickListener.onDeleteClick(user)
        }
        holder.ivEdit.setOnClickListener {
            onEditClickListener.onEditClick(user)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(user: UserWithId)
    }

    interface OnEditClickListener {
        fun onEditClick(user: UserWithId)
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val firstNameTextView: TextView = itemView.findViewById(R.id.tvFirstName)
        private val lastNameTextView: TextView = itemView.findViewById(R.id.tvLastName)
        private val ageTextView: TextView = itemView.findViewById(R.id.tvage)
        private val idTextView : TextView = itemView.findViewById(R.id.tvId)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)

        fun bind(user: UserWithId) {
            firstNameTextView.text = "First Name: ${user.firstName}"
            lastNameTextView.text = "Last Name: ${user.lastName}"
            ageTextView.text = "Age: ${user.age}"
            idTextView.text="ID : ${user.id}"
        }
    }
    // Implement filtering based on user ID
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<UserWithId>()
                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(users)
                } else {
                    val filterPattern = constraint.toString().trim()
                    for (user in users) {
                        if (user.id.contains(filterPattern, ignoreCase = true)) {
                            filteredList.add(user)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredUsers = results?.values as List<UserWithId>
                notifyDataSetChanged()
            }
        }
    }
}