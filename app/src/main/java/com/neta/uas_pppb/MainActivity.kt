package com.neta.uas_pppb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.neta.uas_pppb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){
            val intentToRegisterActivity = Intent(this@MainActivity, RegisterActivity::class.java)

            registerButton.setOnClickListener{
                startActivity(intentToRegisterActivity)
            }
        }
    }
}