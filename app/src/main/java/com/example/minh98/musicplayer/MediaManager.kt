package com.example.minh98.musicplayer

import android.content.Context
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import java.text.SimpleDateFormat

/**
 * Created by minh98 on 22/08/2017.
 */
class MediaManager(val mcontext: Context,val IndexSong: Int,currentPosition:Int) : MediaPlayer.OnCompletionListener {


    private val mMediaPlayer: MediaPlayer
    private val mListSong: MutableList<Song>


    companion object {
        private val IDLE=0
        private val PLAYING=1
        private val PAUSE=2
        private val STOPED=3

        val REPEAT_ONE=0
        val REPEAT=1
    }
    private var state= IDLE
    private var mIndex=IndexSong


    private var repeatCurrent=REPEAT_ONE
    private var currentPosition=currentPosition


    init {
        mMediaPlayer = MediaPlayer()
        mListSong = mutableListOf()
        initData()
        Toast.makeText(mcontext,"data initing",Toast.LENGTH_LONG).show()
    }

    private fun initData() {
        val uriAudio: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val components = arrayOf(
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DURATION
        )
        val where = MediaStore.Audio.AudioColumns.IS_MUSIC + " !=0" // display_name+ LIKE '%.mp3'
        val sort = MediaStore.Audio.Media.TITLE + " ASC"
        val cur: Cursor = mcontext.contentResolver.query(uriAudio, components, where, null, null)

        cur.moveToFirst()

        while (cur.moveToNext()) {
            mListSong.add(Song(cur.getString(cur.getColumnIndex(components[0]))
                    , cur.getString(cur.getColumnIndex(components[1]))
                    , cur.getString(cur.getColumnIndex(components[2]))
                    , cur.getString(cur.getColumnIndex(components[3]))
                    , cur.getInt(cur.getColumnIndex(components[4]))))
        }
        cur.close()

    }

    fun getRepeatCurrent()=repeatCurrent
    fun setRepeatCurrent(mode:Int){
        repeatCurrent=mode
    }

    fun getListSong(): MutableList<Song> {
        return mListSong
    }

    fun play(position:Int):Boolean{
        mIndex=position
        stop()
        return play()
    }

    fun play():Boolean{

        if(state== IDLE || state== STOPED){
            //truong hop chua play hoac da dung lai
            val song:Song=mListSong[mIndex]
            mMediaPlayer.setDataSource(song.path)
            mMediaPlayer.setOnCompletionListener(this)
            mMediaPlayer.prepare() // vi play file trong device nen dung prepare cung duoc
            if(currentPosition>0){
                mMediaPlayer.seekTo(currentPosition)
                currentPosition=0
            }
            mMediaPlayer.start()
            state= PLAYING
            return true
        }else if(state== PLAYING){
            //truong hop dang choi thi se pause
            mMediaPlayer.pause()
            state= PAUSE
            return false
        }else{
            //truong hop con lai(choi tiep)
            mMediaPlayer.start()
            state= PLAYING
            return true
        }
    }

    fun stop(){
        if(state== PLAYING|| state== PAUSE){
            mMediaPlayer.stop()
            mMediaPlayer.reset()
            state= STOPED
        }
    }

    fun back():Boolean{
        if(mIndex==0){
            mIndex=mListSong.size
        }
        mIndex--
        stop()
        return play()
    }

    fun next():Boolean{
        //trong truong hop next toi bai tiep theo(lien tiep)
        mIndex++
        if(mIndex==mListSong.size){
            mIndex=0
        }
        (mcontext as MainActivity).updateSong()
        stop()
        return play()
    }

    override fun onCompletion(p0: MediaPlayer?) {
        when(repeatCurrent){
            REPEAT_ONE->{
                mMediaPlayer.seekTo(0)
                mMediaPlayer.start()
            }
            REPEAT->{
                next()
            }
        }
    }

    fun getCurrentSong(): Song =mListSong[mIndex]
    fun getCurrentIndexSong(): Int =mIndex+1
    fun isPlaying(): Boolean =state== PLAYING||state== PAUSE
    fun getTime(): CharSequence? {
        val format= SimpleDateFormat("mm:ss")
        return format.format(getCurrentPosition())+"/"+format.format(getCurrrentDuration())
    }

    private fun getCurrrentDuration(): Int =getCurrentSong().duration
    fun getCurrentPosition(): Int =mMediaPlayer.currentPosition
    fun seekTo(mProgress: Int) {
        mMediaPlayer.seekTo(mProgress)
    }

    fun getSongAt(currentIndexSong: Int): Song =mListSong[currentIndexSong]

}