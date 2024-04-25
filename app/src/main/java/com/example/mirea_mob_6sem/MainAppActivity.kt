package com.example.mirea_mob_6sem

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.mirea_mob_6sem.find.FindFragment
import com.example.mirea_mob_6sem.R
import com.google.android.material.bottomnavigation.BottomNavigationView


const val Dark_theme = false
class MainAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_app)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val bnv = findViewById<BottomNavigationView >(R.id.bottomNavigationView)

        switchTheme()


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

                R.id.menuFragment -> {
                    navController.navigate(R.id.menuFragment)
                    return@setOnItemSelectedListener true
                }

                else -> {false}
            }
        }
    }

    private fun switchTheme() {
        val sharedPreferences = getSharedPreferences("Dark_theme", Context.MODE_PRIVATE)
        val darkTheme = sharedPreferences?.getBoolean("Dark_theme", false)
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme == true) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}