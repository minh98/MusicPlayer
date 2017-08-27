package com.example.minh98.musicplayer

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat

class MainActivity :
        AppCompatActivity(), View.OnClickListener, AdapterView.OnItemClickListener, SeekBar.OnSeekBarChangeListener {


    private val PAUSE=0
    private val PLAY=1
    private var playCurrent=PAUSE

    private lateinit var imgPrevious: ImageView
    private lateinit var imgPlay: ImageView
    private lateinit var imgNext: ImageView
    private lateinit var imgRepeat: ImageView
    private lateinit var imgStop:ImageView

    private lateinit var tvTenBai: TextView
    private lateinit var tvTenTacGia: TextView
    private lateinit var tvIndexBai: TextView
    private lateinit var tvThoiGian: TextView
    private lateinit var listSong:ListView
    private lateinit var seekBar:SeekBar

    private lateinit var mMediaManager:MediaManager
    private val dataSong= mutableListOf<Song>()

    private var mProgress=0

    lateinit var mSharedPreference:SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()
    }

    private fun initData() {
        mSharedPreference=SharedPreference(this)
        mMediaManager= MediaManager(this,mSharedPreference.getCurrentIndexSong(),mSharedPreference.getCurrentPosition())

        val song:Song=mMediaManager.getSongAt(mSharedPreference.getCurrentIndexSong())
        initInfoSong(song,mSharedPreference.getCurrentIndexSong(),mSharedPreference.getCurrentPosition())


        dataSong.addAll(mMediaManager.getListSong())
        Toast.makeText(this," "+mMediaManager.getListSong().size,Toast.LENGTH_LONG).show()
        val adapter=AdapterSong(this,R.layout.item_song,dataSong)
        listSong.adapter=adapter
    }

    private fun initInfoSong(song: Song,index:Int,position: Int) {
        tvTenBai.text=song.name
        tvTenTacGia.text=song.artist
        tvIndexBai.text=""+index+"/"+mMediaManager.getListSong().size
        val formatTime=SimpleDateFormat("mm:ss")
        tvThoiGian.text=formatTime.format(position)+"/"+formatTime.format(song.duration)
        seekBar.max=song.duration
        seekBar.setProgress(position)
        Log.e("position",""+position)
    }

    private fun initView() {
        imgPrevious = find<ImageView>(R.id.imgPrevious)
        imgPlay = find<ImageView>(R.id.imgPlay)
        imgNext = find<ImageView>(R.id.imgNext)
        imgRepeat = find<ImageView>(R.id.imgRepeat)
        imgStop=find<ImageView>(R.id.imgStop)

        tvTenBai = find<TextView>(R.id.tvTenBai)
        tvTenTacGia = find<TextView>(R.id.tvTacGia)
        tvIndexBai = find<TextView>(R.id.tvIndexBai)
        tvThoiGian = find<TextView>(R.id.tvThoiGian)

        listSong=find<ListView>(R.id.listBai)

        seekBar=find<SeekBar>(R.id.seekBar)
        //add listener
        setListener()
    }

    private fun setListener() {
        imgPrevious.setOnClickListener(this)
        imgPlay.setOnClickListener(this)
        imgNext.setOnClickListener(this)
        imgRepeat.setOnClickListener(this)
        imgStop.setOnClickListener(this)

        listSong.setOnItemClickListener(this)

        seekBar.setOnSeekBarChangeListener(this)
    }

    private fun <T> find(id: Int): T {
        return findViewById(id) as T
    }

    override fun onClick(p0: View?) {
        if(dataSong.size<1){
            return
        }
        when (p0) {
            imgPrevious -> {
                //do skip previous
                doSkipPrevious()
            }

            imgPlay -> {
                //handle click play
                doPlay()
            }

            imgNext -> {
                //do skip next
                doSkipNext()
            }

            imgRepeat -> {
                //handle click repeat
                doRepeat()
            }
            imgStop->{
                doStop()
            }

        }
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        doPlay(p2)
    }

    private fun doStop() {
        mMediaManager.stop()
    }

    private fun doRepeat() {
        if(mMediaManager.getRepeatCurrent()==MediaManager.REPEAT){
            mMediaManager.setRepeatCurrent(MediaManager.REPEAT_ONE)
        }else{
            mMediaManager.setRepeatCurrent(MediaManager.REPEAT)
        }
        imgRepeat.setImageLevel(mMediaManager.getRepeatCurrent())
    }

    private fun doSkipNext() {
        if(mMediaManager.next()){
            updateSong()
            playCurrent=PLAY
            imgPlay.setImageLevel(playCurrent)
        }
    }

    private fun doPlay(position:Int){
        if(mMediaManager.play(position)){
            updateSong()
            playCurrent=PLAY
        }else{
            playCurrent=PAUSE
        }
        imgPlay.setImageLevel(playCurrent)
    }

    private fun doPlay() {
        if(mMediaManager.play()){
            updateSong()
            playCurrent=PLAY
        }else{
            playCurrent=PAUSE
        }
        imgPlay.setImageLevel(playCurrent)
    }

    private fun doSkipPrevious() {
        if(mMediaManager.back()){
            updateSong()
            playCurrent=PLAY
            imgPlay.setImageLevel(playCurrent)
        }
    }

    fun updateSong(){
        val song:Song=mMediaManager.getCurrentSong()
        UpdateSeekBar().execute()
        setInfoSong(song)
    }

    private fun setInfoSong(song: Song) {
        tvTenBai.text=song.name
        tvTenTacGia.text=song.artist
        tvIndexBai.text=""+mMediaManager.getCurrentIndexSong()+"/"+mMediaManager.getListSong().size
        seekBar.max=song.duration
    }

    private inner class UpdateSeekBar :AsyncTask<Unit,Unit,Unit>(){
        override fun doInBackground(vararg p0: Unit?): Unit {
            while(mMediaManager.isPlaying()){
                try{
                    publishProgress()
                    Thread.sleep(1000)

                }catch (e:Exception){
                    //loi
                }
            }
        }

        override fun onProgressUpdate(vararg values: Unit?) {
            tvThoiGian.text=mMediaManager.getTime()
            seekBar.setProgress(mMediaManager.getCurrentPosition())
            super.onProgressUpdate(*values)
        }

    }



    /**
     * seekbar
     */
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        mProgress=p1
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        //sau khi nha phim ra (cap nhat lai)
        mMediaManager.seekTo(mProgress)
    }

    override fun onDestroy() {
        mSharedPreference.setCurrentIndexSong(mMediaManager.getCurrentIndexSong()-1)
        mSharedPreference.setCurrentPosition(mMediaManager.getCurrentPosition())
        super.onDestroy()
    }
}
