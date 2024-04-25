package com.example.mirea_mob_6sem.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.mirea_mob_6sem.MainAppActivity
import com.example.mirea_mob_6sem.R

class MenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonUpdate : Button = view.findViewById(R.id.button_theme)
        buttonUpdate.setOnClickListener { v ->
            switchTheme()
        }
    }

    private fun switchTheme() {
        val sharedPreferences = this.activity?.getSharedPreferences("Dark_theme", Context.MODE_PRIVATE)
        var darkTheme = sharedPreferences?.getBoolean("Dark_theme", false)
        darkTheme = !darkTheme!!
        sharedPreferences?.edit()?.putBoolean("Dark_theme",darkTheme)?.apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}