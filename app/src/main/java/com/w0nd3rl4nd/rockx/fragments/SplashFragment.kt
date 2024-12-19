package com.w0nd3rl4nd.rockx.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.w0nd3rl4nd.rockx.MainActivity
import com.w0nd3rl4nd.rockx.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            return
        }

        lifecycleScope.launch {
            delay(2000)
            (activity as MainActivity).replaceFragment(LoginFragment())
        }
    }
}