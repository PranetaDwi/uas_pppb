package com.neta.uas_pppb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.neta.uas_pppb.databinding.ActivityAdminBinding
import com.neta.uas_pppb.databinding.ActivityFragmentBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            viewPager.adapter = TabAdapter(supportFragmentManager)
            tabLayout.setupWithViewPager(viewPager)
        }
    }
}