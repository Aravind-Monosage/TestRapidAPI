package com.example.testretrofit

import UserAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testretrofit.Controller.RetrofitClient
import com.example.testretrofit.Controller.UserService
import com.example.testretrofit.Model.User
import com.example.testretrofit.Model.UserWithId
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity(),
    UserAdapter.OnDeleteClickListener,
    UserAdapter.OnEditClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private val users = mutableListOf<UserWithId>()
    private lateinit var floatingActionButton: FloatingActionButton
    private val userService: UserService = RetrofitClient.instance
    private lateinit var searchView: SearchView
    private var backButtonPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(users, this, this)
        recyclerView.adapter = adapter
        floatingActionButton = findViewById(R.id.fab)
        searchView = findViewById(R.id.searchView)


        fetchDataAndPopulateRecyclerView()
        floatingActionButton.setOnClickListener {
            showAddUserDialog()
        }

        // Set up search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { fetchUserById(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })


    }

    private fun fetchUserById(userId: String) {
        userService.getUserById(userId).enqueue(object : Callback<UserWithId> {
            override fun onResponse(call: Call<UserWithId>, response: Response<UserWithId>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        users.clear()
                        users.add(user)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "User not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to fetch user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserWithId>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "Failed to fetch user: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showAddUserDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView).setTitle("Add User")
        val dialog = dialogBuilder.show()

        dialogView.findViewById<Button>(R.id.createUser).setOnClickListener {
            val firstName =
                dialogView.findViewById<EditText>(R.id.dialogAddUserFirstName).text.toString()
            val lastName =
                dialogView.findViewById<EditText>(R.id.dialogAddUserLastName).text.toString()
            val age =
                dialogView.findViewById<EditText>(R.id.dialogAddUserAge).text.toString().toInt()

            val newUser = User(firstName, lastName, age)
            userService.createUser(newUser).enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                            fetchDataAndPopulateRecyclerView() // Refresh the user list
                        }
                        fetchDataAndPopulateRecyclerView()
                        dialog.dismiss()
                    } else {
                        // Handle unsuccessful response
                        Toast.makeText(this@MainActivity, "Failed to add user", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {
                    // Handle failure
                    Toast.makeText(
                        this@MainActivity, "Failed to add user: ${t.message}", Toast.LENGTH_SHORT
                    ).show()
                    fetchDataAndPopulateRecyclerView()
                    dialog.dismiss()
                }

            })
        }
    }

    private fun fetchDataAndPopulateRecyclerView() {
        userService.getUsers().enqueue(object : Callback<List<UserWithId>> {
            override fun onResponse(
                call: Call<List<UserWithId>>, response: Response<List<UserWithId>>
            ) {
                if (response.isSuccessful) {
                    users.clear()
                    response.body()?.let {
                        users.addAll(it)
                        adapter.notifyDataSetChanged() // Refresh RecyclerView after updating data
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch users", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<List<UserWithId>>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity, "Failed to fetch users: ${t.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onDeleteClick(user: UserWithId) {
        val dialogBuilder =
            AlertDialog.Builder(this).setMessage("Are you sure you want to delete the user?")
                .setPositiveButton("Yes") { dialog, _ ->
                    deleteUser(user.id)
                    dialog.dismiss()
                }.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun deleteUser(userId: String) {
        userService.deleteUser(userId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@MainActivity, "Deleted user successfully", Toast.LENGTH_SHORT
                    ).show()
                    fetchDataAndPopulateRecyclerView() // Refresh RecyclerView after deletion
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(this@MainActivity, "Failed to delete user", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure
                Toast.makeText(
                    this@MainActivity, "Failed to delete user: ${t.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showUpdateUserDialog(user: UserWithId) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_update_user, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView).setTitle("Update User")
        val dialog = dialogBuilder.show()

        val etFirstName = dialogView.findViewById<EditText>(R.id.dialogUpdateUserFirstName)
        val etLastName = dialogView.findViewById<EditText>(R.id.dialogUpdateUserLastName)
        val etAge = dialogView.findViewById<EditText>(R.id.dialogUpdateUserAge)

        etFirstName.setText(user.firstName)
        etLastName.setText(user.lastName)
        etAge.setText(user.age.toString())

        dialogView.findViewById<Button>(R.id.UpdateUser).setOnClickListener {
            val updatedFirstName = etFirstName.text.toString()
            val updatedLastName = etLastName.text.toString()
            val updatedAge = etAge.text.toString().toInt()

            val updatedUser = UserWithId(user.id, updatedFirstName, updatedLastName, updatedAge)
            updateUser(updatedUser)
            dialog.dismiss()
        }
    }

    private fun updateUser(user: UserWithId) {
        userService.updateUser(user.id, user).enqueue(object : Callback<UserWithId> {
            override fun onResponse(call: Call<UserWithId>, response: Response<UserWithId>) {
                if (response.isSuccessful) {
                    // User updated successfully, you can handle UI updates here if needed
                    Toast.makeText(
                        this@MainActivity,
                        "User updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchDataAndPopulateRecyclerView() // Refresh RecyclerView after update
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to update user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserWithId>, t: Throwable) {
                // Handle failure
                Toast.makeText(
                    this@MainActivity,
                    "Failed to update user: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                fetchDataAndPopulateRecyclerView() // Refresh RecyclerView even on failure
            }
        })
    }

    override fun onEditClick(user: UserWithId) {
        showUpdateUserDialog(user)
    }

    override fun onBackPressed() {
        if (backButtonPressedOnce) {
            if (isTaskRoot) {
                // If this is the last activity in the stack
                showExitConfirmationDialog()
            } else {
                super.onBackPressed()
            }
        }else{
            backButtonPressedOnce = true
            fetchDataAndPopulateRecyclerView()
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit the app?")
            .setPositiveButton("Yes") { _, _ ->
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}