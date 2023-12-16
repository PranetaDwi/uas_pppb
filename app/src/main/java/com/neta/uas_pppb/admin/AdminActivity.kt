package com.neta.uas_pppb.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.neta.uas_pppb.PrefManager
import com.neta.uas_pppb.R
import com.neta.uas_pppb.adapter.TabAdapter
import com.neta.uas_pppb.auth.MainActivity
import com.neta.uas_pppb.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var prefManager: PrefManager

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        prefManager = PrefManager.getInstance(this)
        return when(item.itemId){
            R.id.logout->{
                prefManager.setLoggedIn(false)
                prefManager.clear()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                AdminActivity().finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



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