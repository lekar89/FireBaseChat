package pom.lekar.firebasechat.ui.Dialogs;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import pom.lekar.firebasechat.R;

/**
 * Created by lekar on 19.05.17.
 */

public class DialogAudio extends DialogFragment implements View.OnClickListener {


    MediaPlayer mPlayer;
    Button      mBtnPlay;
    Button      mBtnPause;
    ProgressBar mProgressBar;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_item_audio, null);
        getDialog().setTitle("Audio");


        mPlayer = new MediaPlayer();


        mBtnPlay     = (Button)      v.findViewById(R.id.dialog_play_button);
        mBtnPause    = (Button)      v.findViewById(R.id.dialog_pause_button);
        mProgressBar = (ProgressBar) v.findViewById(R.id.dialog_progress);

        mBtnPlay .setOnClickListener(this);
        mBtnPause.setOnClickListener(this);

        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(getArguments().getString("URL"));
            mPlayer.prepareAsync();

            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mProgressBar.setVisibility(View.GONE);
                    mBtnPlay    .setVisibility(View.VISIBLE);
                    mBtnPause   .setVisibility(View.VISIBLE);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    public void onClick(View v) {

        switch (v.getId()){
            case R.id.dialog_play_button:
                mPlayer.start();
                break;
            case R.id.dialog_pause_button:
                mPlayer.pause();
                break;
        }
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mPlayer.stop();

    }


}
