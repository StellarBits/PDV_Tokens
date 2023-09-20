package com.stellarbitsapps.androidpdv.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stellarbitsapps.androidpdv.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        // Do nothing
//    }
}