package pom.lekar.firebasechat.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import de.hdodenhof.circleimageview.CircleImageView;
import pom.lekar.firebasechat.R;

/**
 * Created by lekar on 12.05.17.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public  TextView        messageTextView;
    public  TextView        messengerTextView;
    public  ImageView       messageImageView;
    public  VideoView       messageVideoView;
    public  CircleImageView messengerImageView;

    public MessageViewHolder(View v) {
        super(v);
        messageTextView    = (TextView)         itemView.findViewById(R.id.messageTextView);
        messageImageView   = (ImageView)        itemView.findViewById(R.id.messageImageView);
        messageVideoView   = (VideoView)        itemView.findViewById(R.id.messageVideoView);
        messengerTextView  = (TextView)         itemView.findViewById(R.id.messengerTextView);
        messengerImageView = (CircleImageView)  itemView.findViewById(R.id.messengerImageView);
    }

}
