package com.poultrypathsprint.arcade

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.poultrypathsprint.arcade.core.di.ServiceLocator
import com.poultrypathsprint.arcade.core.navigation.Navigator
import com.poultrypathsprint.arcade.databinding.ActivityMainBinding
import com.poultrypathsprint.arcade.presentation.splash.SplashFragment

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ServiceLocator.init(applicationContext)
        super.onCreate(savedInstanceState)
        val created = ActivityMainBinding.inflate(layoutInflater)
        binding = created
        setContentView(created.root)

        if (savedInstanceState == null) {
            Navigator.showSplash(supportFragmentManager, SplashFragment())
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
