package com.example.area

import android.app.Application
import android.graphics.Bitmap
import com.example.area.model.UserInfo
import com.example.area.model.about.About
import com.example.area.model.about.AboutClass
import com.example.area.utils.SessionManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AREAApplication : Application() {
    var aboutClass: AboutClass? = null
    var aboutBitmapList: List<Bitmap>? = null
    var userInfo: UserInfo? = null
    var successOauth: Boolean? = null
    var reload: Boolean? = null
    var inApp: Boolean = false

    fun setAboutClass(about: About) {
        aboutClass = AboutClass(about)
    }

    fun setAboutBitmapList() {
        val sessionManager = SessionManager(this)
        val url = sessionManager.fetchAuthToken("url") ?: return
        GlobalScope.launch {
            if (aboutClass != null)
                aboutBitmapList = aboutClass!!.getBitmapList(url)
        }
    }

    fun setUserInfoInApp(user: UserInfo) {
        userInfo = user
    }

    fun setTokenInTokenTable(service: String, token: String?) {
        if (userInfo == null)
            return
        val tokensTable = userInfo!!.tokensTable.toMutableMap()
        tokensTable[service + "Token"] = token
        userInfo!!.tokensTable = tokensTable
    }

    fun isAboutClassSet() : Boolean {
        aboutClass ?: return false
        return true
    }
}