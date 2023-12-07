package com.neta.uas_pppb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.neta.uas_pppb.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollectionRef = firestore.collection("users")
    private lateinit var binding: ActivityRegisterBinding
    private val usersListLiveData: MutableLiveData<List<Users>> by lazy {
        MutableLiveData<List<Users>>()
    }

    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intentToFragmentActivity = Intent(this@RegisterActivity, FragmentActivity::class.java)

        // prefManager
        prefManager = PrefManager.getInstance(this)

        with(binding){
            registerButton.setOnClickListener{
                val name = usernameInput.text.toString()
                val email = emailInput.text.toString()
                val password = passwordInput.text.toString()
                val phone = phoneInput.text.toString()
                // logic prefManager untuk menyimpan data login
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(this@RegisterActivity, "Mohon isi semua data", Toast.LENGTH_SHORT).show()
                }else{
                    prefManager.saveUsername(name)
                    prefManager.savePassword(password)
                    prefManager.setLoggedIn(true)
                    checkLoginStatus()
                }

            }
        }
    }

    private fun addUser(user: Users){
        usersCollectionRef.add(user)
            .addOnSuccessListener { docRef ->
                val createUserId = docRef.id
                user.id = createUserId
                docRef.set(user)
                    .addOnFailureListener{
                        Log.d("RegisterActivity", "Error Updating User: ", it)
                    }
                resetForm()
            }
            . addOnFailureListener{
                Log.d("FormActivity", "Error adding budget ID: ", it)
            }
    }

    private fun resetForm(){
        with(binding){
            usernameInput.setText("")
            emailInput.setText("")
            passwordInput.setText("")
            phoneInput.setText("")
        }
    }

    private fun checkLoginStatus(){
        val isLoggedIn = prefManager.isLoggedIn()
        if (isLoggedIn){
            val name = binding.usernameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val phone = binding.phoneInput.text.toString()
            val newUser = Users(name = name, email = email, password = password, phone = phone)
            addUser(newUser)
            Toast.makeText(this@RegisterActivity, "Register Berhasil", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@RegisterActivity, FragmentActivity::class.java))
        } else {
            Toast.makeText(this@RegisterActivity, "Registrasi Gagal", Toast.LENGTH_SHORT).show()
        }
    }
}