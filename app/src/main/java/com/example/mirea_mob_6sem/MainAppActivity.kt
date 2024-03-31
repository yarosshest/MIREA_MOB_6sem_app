package com.example.mirea_mob_6sem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.mirea_mob_6sem.find.FindFragment
import com.example.mirea_mob_6sem.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_app)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val bnv = findViewById<BottomNavigationView >(R.id.bottomNavigationView)


        bnv.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.search -> {
                    navController.navigate(R.id.findFragment )
                    return@setOnItemSelectedListener true
                }

                R.id.recommendation -> {
                    navController.navigate(R.id.recommendationFragment)
                    return@setOnItemSelectedListener true
                }

                else -> {false}
            }
        }
    }
}