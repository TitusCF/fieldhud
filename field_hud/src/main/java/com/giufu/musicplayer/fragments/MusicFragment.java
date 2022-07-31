package com.giufu.musicplayer.fragments;

import static android.app.Activity.RESULT_OK;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.giufu.musicplayer.R;
import com.giufu.musicplayer.menu.MenuActivity;
import com.giufu.musicplayer.model.MusicItem;

public class MusicFragment  extends BaseFragment{
    /*
    private TextView textView, footer, timestamp;
    private static final int BODY_TEXT_SIZE = 40;
    private boolean mBound = false;
    private MusicMenuEvents listener;

    public static MusicFragment newInstance(Integer menu) {
        final MusicFragment myFragment = new MusicFragment();
        final Bundle args = new Bundle();
        args.putInt(MENU_KEY, menu);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onSingleTapUp() {
        //super.onSingleTapUp();
        if (getArguments() != null) {
            int menu = getArguments().getInt(MENU_KEY, MENU_DEFAULT_VALUE);
            if (menu != MENU_DEFAULT_VALUE) {
                Intent intent = new Intent(getActivity(), MenuActivity.class);
                intent.putExtra(MENU_KEY, menu);
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MusicMenuEvents){
            listener = (MusicMenuEvents) context;
        }else {
            throw new RuntimeException(context.toString()+" must implement listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            final int id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
                    MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE);
            switch (id) {
                case R.id.action_play_pause:
                    listener.onPlayPauseSelected();
                    break;
                case R.id.action_next_track:
                    listener.onNextTrackSelected();
                    break;
                case R.id.previous__track:
                    listener.onPreviousTrackSelected();
                    break;
                case R.id.action_stop_music:
                    listener.onStopMusicServiceSelected();
                    break;
            }
        }
    }
    public interface MusicMenuEvents {
        void onPlayPauseSelected();
        void onNextTrackSelected();
        void onPreviousTrackSelected();
        void onStopMusicServiceSelected();
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.music_layout, container, false);
        if (getArguments() != null) {
            textView = new TextView(getContext());
            textView.setText("Music service not started");
            textView.setTextSize(BODY_TEXT_SIZE);
            textView.setTypeface(Typeface.create(getString(R.string.thin_font), Typeface.NORMAL));
            final FrameLayout bodyLayout = view.findViewById(R.id.body_layout);
            bodyLayout.addView(textView);
            footer = view.findViewById(R.id.footer);
            footer.setText("");
            timestamp = view.findViewById(R.id.timestamp);
            timestamp.setText("");
        }
        return view;
    }

    public void stopView(){
        textView.setText("Service stopped");
        footer.setText("");
        timestamp.setText("");
    }
    public void updateView(MusicItem m){
        if (isAdded() && isVisible() && getUserVisibleHint()) {
            try {
                textView.setText("Playing\n\n"+m.getTitle());
                footer.setText(m.getArtist());
                //timestamp.setText(getHumanReadableTime(mService.getCurrentPosition())+ " - "+getHumanReadableTime(m.getDuration()));
            }
            catch (Exception e){
            }
        }
    }
     */
}
