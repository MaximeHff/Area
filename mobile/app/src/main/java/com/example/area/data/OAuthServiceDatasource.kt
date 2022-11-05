package com.example.area.data

import android.graphics.Bitmap
import com.example.area.model.OAuthServiceListElement

class OAuthServiceDatasource {
    private var oauthServiceList: MutableList<OAuthServiceListElement> = mutableListOf<OAuthServiceListElement>()

    fun addOauthService(id: Int, name: String, oauthName: String, imageBitmap: Bitmap, connected: Boolean) {
        oauthServiceList += OAuthServiceListElement(id, name, oauthName, imageBitmap, connected)
    }
    fun clear() {
        oauthServiceList.clear()
    }
    fun loadOauthServiceInfo(): MutableList<OAuthServiceListElement> {
        return oauthServiceList
    }
}