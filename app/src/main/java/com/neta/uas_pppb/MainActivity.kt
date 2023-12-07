package com.neta.uas_pppb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.neta.uas_pppb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var prefManager: PrefManager
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollectionRef = firestore.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        prefManager = PrefManager.getInstance(this)
        checkLoginStatus()

        with(binding){
            val intentToRegisterActivity = Intent(this@MainActivity, RegisterActivity::class.java)

            registerButton.setOnClickListener{
                startActivity(intentToRegisterActivity)
            }

            loginButton.setOnClickListener{
                val username = usernameInput.text.toString()
                val password = passwordInput.text.toString()
                if (username.isEmpty() || password.isEmpty()){
                    Toast.makeText(this@MainActivity, "Mohon isi semua data", Toast.LENGTH_SHORT).show()
                } else {
                    if (isValidUsernamePassword()){
                        prefManager.setLoggedIn(true)
                        checkLoginStatus()
                    } else {
                        loginUser(username, password)
                    }
                }

            }
        }
    }

    private fun loginUser(username: String, password: String){
        usersCollectionRef
            .whereEqualTo("name", username)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDocument = querySnapshot.documents[0]
                    val userId = userDocument.id
                    prefManager.saveUsername(username)
                    prefManager.savePassword(password)
                    prefManager.setLoggedIn(true)
                    checkLoginStatus()
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "Username atau password salah bener-bener ye", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun isValidUsernamePassword(): Boolean{
        val username = prefManager.getUsername()
        val password = prefManager.getPassword()
        val inputUsername = binding.usernameInput.text.toString()
        val inputPassword = binding.passwordInput.text.toString()
        return username == inputUsername && password == inputPassword
    }
    private fun checkLoginStatus(){
        val isLoggedIn = prefManager.isLoggedIn()
        if (isLoggedIn) {
            Toast.makeText(this@MainActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
            if (prefManager.getUsername() == "adminkeren" && prefManager.getPassword() == "haloneta"){
                startActivity(Intent(this@MainActivity, AdminActivity::class.java))
            } else {
                startActivity(Intent(this@MainActivity, FragmentActivity::class.java))
            }
            finish()
        } else {
            Toast.makeText(this@MainActivity, "Login gagal", Toast.LENGTH_SHORT).show()
        }
    }
}