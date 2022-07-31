package com.giufu.musicplayer.viewmodel;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.giufu.musicplayer.model.MusicItem;
import com.giufu.musicplayer.services.MusicService;

public class MusicViewModel extends ViewModel {
    private MutableLiveData<MusicItem> currentSong = new MutableLiveData<>();
    private MutableLiveData<Boolean> isPaused = new MutableLiveData<>();
    private MutableLiveData<MusicService.LocalBinder> mBinder = new MutableLiveData<>();

    public LiveData<MusicItem> getCurrentSong() { return currentSong; }
    public LiveData<Boolean> getIsPaused() { return isPaused; }
    public LiveData<MusicService.LocalBinder> getBinder() { return mBinder; }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) iBinder;
            mBinder.postValue(binder);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBinder.postValue(null);
        }
    };
    public ServiceConnection getServiceConnection(){return serviceConnection;}
}
