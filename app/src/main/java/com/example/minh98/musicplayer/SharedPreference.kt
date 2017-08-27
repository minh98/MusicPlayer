package com.example.minh98.musicplayer

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Created by minh98 on 23/08/2017.
 */
class SharedPreference(val mcontext : Context) {
    val mSharedPreference:SharedPreferences = mcontext.getSharedPreferences("data",Context.MODE_PRIVATE)




    fun getCurrentIndexSong():Int=mSharedPreference.getInt("indexSong",0)
    fun setCurrentIndexSong(index:Int){
        mSharedPreference.edit().putInt("indexSong",index).apply()
    }
    fun getCurrentPosition():Int=mSharedPreference.getInt("position",0)
    fun setCurrentPosition(position:Int){
        mSharedPreference.edit().putInt("position",position).apply()
        Log.e("position",""+position)
    }

}