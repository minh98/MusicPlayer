package com.example.minh98.musicplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 * Created by minh98 on 22/08/2017.
 */
data class Song(val name:String,val path:String,val album:String,val artist:String,val duration:Int)

class AdapterSong(val mcontext: Context,val resource: Int, val list: List<Song>)
    : ArrayAdapter<Song>(mcontext, resource, list){

    override fun getItem(position: Int): Song {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val song=list[position]
        val inflater:LayoutInflater= mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewRoot= inflater.inflate(resource,parent,false)
        viewRoot.findViewById<TextView>(R.id.tvTacGia).text=song.artist
        viewRoot.findViewById<TextView>(R.id.tvTenBai).text=song.name
        return viewRoot
    }
}
