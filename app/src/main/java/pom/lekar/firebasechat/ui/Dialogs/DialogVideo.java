package pom.lekar.firebasechat.ui.Dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import pom.lekar.firebasechat.R;

/**
 * Created by lekar on 19.05.17.
 */

public class DialogVideo extends DialogFragment  {
    private VideoView mVideoView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Video");
        View v = inflater.inflate(R.layout.dialog_item_video, null);

        MediaController mediacontroller = new MediaController(getContext());
        mVideoView = (VideoView) v.findViewById(R.id.dialog_video_view);


        mVideoView.setVideoPath(getArguments().getString("URL"));
        //mediacontroller.setAnchorView( mVideoView);
        //mVideoView.setOnClickListener(this);

        mVideoView.start();
        mVideoView.setMediaController(mediacontroller);
        return v;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

    }
}