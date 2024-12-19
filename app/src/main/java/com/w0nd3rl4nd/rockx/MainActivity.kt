package com.w0nd3rl4nd.rockx

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.w0nd3rl4nd.rockx.fragments.AddRocketFragment
import com.w0nd3rl4nd.rockx.fragments.LoginFragment
import com.w0nd3rl4nd.rockx.fragments.RocketDetailFragment
import com.w0nd3rl4nd.rockx.fragments.RocketsRecyclerFragment
import com.w0nd3rl4nd.rockx.fragments.SplashFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            replaceFragment(SplashFragment())
        } else {
            val currentFragment = supportFragmentManager.findFragmentById(android.R.id.content)
            if (currentFragment is LoginFragment) {
                replaceFragment(LoginFragment())
            } else if (currentFragment is RocketsRecyclerFragment) {
                replaceFragment(RocketsRecyclerFragment())
            }
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        when (fragment) {
            is RocketsRecyclerFragment -> {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                supportActionBar?.show()
            }

            is SplashFragment -> {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                supportActionBar?.hide()
            }

            else -> {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }

        transaction.replace(android.R.id.content, fragment)

        if (fragment is RocketDetailFragment || fragment is AddRocketFragment) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

}