package pom.lekar.firebasechat.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import pom.lekar.firebasechat.R;


/**
 * Created by lekar on 12.05.17.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public  WebView         messengerWebView;
    public  TextView        messageTextView;
    public  TextView        messengerTextView;
    public  TextView        messengerTime;
    public  ImageView       messageImageView;
   // public  VideoView       messageVideoView;
    public  ImageButton    messengerPlayButton;
    public  MediaController messengerPlayer;
    public  CircleImageView messengerImageView;
    public RelativeLayout messengerRelativeLayout;
    public  TextView messageTextForLink;

   // public YouTubePlayerView youTubePlayerView;




    public MessageViewHolder(View v) {
        super(v);
        messageTextView       = (TextView)         itemView.findViewById(R.id.messageTextView);
        messageImageView      = (ImageView)        itemView.findViewById(R.id.messageImageView);
       // messageVideoView      = (VideoView)        itemView.findViewById(R.id.messageVideoView);
        messengerTextView     = (TextView)         itemView.findViewById(R.id.messengerTextView);
        messengerTime         = (TextView)         itemView.findViewById(R.id.message_time);
        messageTextForLink = (TextView)         itemView.findViewById(R.id.message_text_for_link);
        messengerImageView    = (CircleImageView)  itemView.findViewById(R.id.messengerImageView);
        messengerPlayer       = (MediaController)  itemView.findViewById(R.id.message_audio_controller);
        messengerPlayButton   = (ImageButton)     itemView.findViewById(R.id.message_play_button);
        messengerWebView      = (WebView)          itemView.findViewById(R.id.message_web_view);
        messengerRelativeLayout = (RelativeLayout)     itemView.findViewById(R.id.message_layout_for_web);

       // youTubePlayerView   = (YouTubePlayerView)   itemView.findViewById(R.id.messengerYoutube);
    }

}
