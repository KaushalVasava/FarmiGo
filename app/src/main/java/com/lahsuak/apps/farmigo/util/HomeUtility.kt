package com.lahsuak.apps.farmigo.util

import android.content.Context
import android.widget.Toast

object HomeUtility {
  fun notifyUser(context: Context,message: String){
      Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }
}