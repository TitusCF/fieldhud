package com.giufu.musicplayer.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.browse.MediaBrowser
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.giufu.musicplayer.App
import com.giufu.musicplayer.MainActivity
import com.giufu.musicplayer.R
import com.giufu.musicplayer.model.MusicItem
import com.giufu.musicplayer.utils.MusicUtil
import java.io.File

class MusicService: Service() {

    var songs: ArrayList<MusicItem> = ArrayList()
    private var songIndex = 0
    private val binder: IBinder = LocalBinder()
    private var paused = true//false
    fun isPaused(): Boolean = paused
    fun getCurrentSong(): MusicItem = songs[songIndex];
    fun getCurrentPosition() = mediaPlayer!!.currentPosition
    private var lastError: Exception = Exception("no error")
    var mediaPlayer: MediaPlayer? = null
    private lateinit var listener: MusicServiceEvents

    fun registerEvents(listener: MusicServiceEvents){
        this.listener = listener;
        listener.onSongChanged(songs[songIndex])
    }

    public interface MusicServiceEvents{
        fun onSongChanged(song : MusicItem);
    }


    override fun onUnbind(intent: Intent?): Boolean {
        if (mediaPlayer!=null){
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            lastError = Exception("Service stopped")
        }
        return false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
            .setContentTitle("Music!")
            .setContentText("Serving music...")
            .setSmallIcon(R.drawable.ic_delete)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(0, notification)
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        try{
            getSongs()
            playMusic()
        }
        catch (e: Exception){
            lastError = e
        }
    }

    @SuppressLint("SdCardPath")
    private fun getSongs(){
        val path = File("/mnt/sdcard/Music")
        val musicFiles: Array<File> = path.listFiles()
        var count = 0L
        for(musicFile in musicFiles){
            if (!musicFile.isDirectory){
                val path: String = musicFile.path
                val mi = MusicItem(path)
                mi.id = count
                count++
                mi.album = MusicUtil.getAlbum(path)
                mi.artist = MusicUtil.getArtist(path)
                mi.title = MusicUtil.getSongTitle(path)
                mi.duration = MusicUtil.getDuration(path)
                songs.add(mi)
            }
        }
    }

    fun resumeMusic() {
        if (paused)
        {
            paused = false
            mediaPlayer!!.start()
        }
    }

    fun previousTrack(){
        if (songIndex - 1 > 0) {
            try {
                songIndex--
                mediaPlayer!!.stop()
                mediaPlayer!!.reset()
                mediaPlayer!!.setDataSource(songs[songIndex].location)
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
                paused = false
            } catch (e: Exception) {
            }
        } else {
            songIndex = songs.size
            previousTrack()
        }
        listener.onSongChanged(songs[songIndex])
    }

    fun nextTrack(){
        if (songIndex + 1 < songs.size) {
            try {
                songIndex++
                mediaPlayer!!.stop()
                mediaPlayer!!.reset()
                mediaPlayer!!.setDataSource(songs[songIndex].location)
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
                paused = false
            } catch (e: Exception) {
            }
        } else {
            songIndex = -1
            nextTrack()
        }
        listener.onSongChanged(songs[songIndex])
    }

    private fun playMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener { nextTrack() })
            mediaPlayer!!.setDataSource(songs[songIndex].location);
            mediaPlayer!!.prepare();
        }
        mediaPlayer!!.start()
        paused=false
        listener.onSongChanged(songs[songIndex])
    }

    fun pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer!!.pause()
            paused = true
        }
    }

    private fun stopPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer!!.release()
            mediaPlayer = null
            Toast.makeText(this, "media player killed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayer()
    }

    override fun onBind(intent: Intent): IBinder? {
        //super.onBind(intent)
        return binder
    }

    public inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

}