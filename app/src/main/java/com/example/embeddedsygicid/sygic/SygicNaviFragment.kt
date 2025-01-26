package com.example.embeddedsygicid.sygic

import com.sygic.aura.embedded.SygicFragmentSupportV4

class SygicNaviFragment : SygicFragmentSupportV4() {
    override fun onResume() {
        startNavi()
        setCallback(SygicNaviCallback(requireActivity()))
        super.onResume()
    }
}