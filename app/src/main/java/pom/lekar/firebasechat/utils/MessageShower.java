package pom.lekar.firebasechat.utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pom.lekar.firebasechat.Constants;
import pom.lekar.firebasechat.R;
import pom.lekar.firebasechat.models.FriendlyMessage;
import pom.lekar.firebasechat.ui.MessageViewHolder;
import pom.lekar.firebasechat.ui.activities.ChatActivity;

import static com.google.android.gms.internal.zzt.TAG;

/**
 * Created by lekar on 13.05.17.
 */

public class MessageShower {
    boolean isPlaing;
    private  Context             mContext;
    private  ProgressBar         mProgressBar;
    private  ChatActivity        mChatActivity;
    private  RecyclerView        mMessageRecyclerView;
    private  LinearLayoutManager mLinearLayoutManager;
    private  DatabaseReference   mFirebaseDatabaseReference;
    private  FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>  mFirebaseAdapter;

    public MessageShower(Context mContext, ChatActivity mChatActivity) {

        this.mContext = mContext;
        this.mChatActivity = mChatActivity;
    }

    private  void initialize(){

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mProgressBar         = (ProgressBar) mChatActivity.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) mChatActivity.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
    }


    public void showMessage(String mRoomName) {

        initialize();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(
                FriendlyMessage.class,
                R.layout.item_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference
                        .child(Constants.ARG_ROOMS)
                        .child(mRoomName)) {

            @Override
            protected FriendlyMessage parseSnapshot(DataSnapshot snapshot) {

                FriendlyMessage friendlyMessage = super.parseSnapshot(snapshot);
                if (friendlyMessage != null) {
                    friendlyMessage.setId(snapshot.getKey());
                }
                return friendlyMessage;
            }

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder,
                                              FriendlyMessage friendlyMessage,
                                              int position) {

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.messengerTextView.setText(friendlyMessage.getName());
                setUserPhoto(friendlyMessage,viewHolder);

                if (friendlyMessage.getText() != null) {
                    setText(friendlyMessage,viewHolder);

                } else if (friendlyMessage.getImageUrl() != null) {
                    setImage(friendlyMessage,viewHolder);

                } else if (friendlyMessage.getVideoUrl() != null) {
                    setVideo(friendlyMessage,viewHolder);

                }else if (friendlyMessage.getAudioUrl() != null) {
                    setAudio(friendlyMessage,viewHolder);

                }else if (friendlyMessage.getLatLong() != null) {
                    //setLocation(friendlyMessage, viewHolder);


                }

            }
        };
        scrollToLastMessage();
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
    }



    private void setLocation(FriendlyMessage mFriendlyMessage, MessageViewHolder mViewHolder) {
        GoogleMap map;


        mViewHolder.messengerMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mGoogleMap) {

            }
        });
        mViewHolder.messengerMapView.setVisibility(View.VISIBLE);
//        map = mapView.getMapAsync();
//
//        map.getUiSettings().setMyLocationButtonEnabled(false);
//        map.setMyLocationEnabled(true);
    }


    private void setAudio(FriendlyMessage friendlyMessage,MessageViewHolder viewHolder){

        isPlaing= false;


        final MediaPlayer mp = new MediaPlayer();
        try {
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(friendlyMessage.getAudioUrl());
            mp.prepareAsync();
            //mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.messengerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPlaing) {

                    mp.start();
                    isPlaing = true;
                }else if(isPlaing){
                    mp.pause();
                    isPlaing=false;

                }
            }
        });
        viewHolder.messengerMapView.setVisibility(View.GONE);

        viewHolder.messengerPlayButton.setVisibility(VideoView.VISIBLE);
        viewHolder.messageVideoView.setVisibility(VideoView.GONE);
        viewHolder.messageImageView.setVisibility(ImageView.GONE);
        viewHolder.messageTextView.setVisibility(TextView.GONE);
        viewHolder.messengerLinearLayout.setVisibility(TextView.GONE);

    }
    private void setUserPhoto( FriendlyMessage friendlyMessage,  MessageViewHolder viewHolder) {

        viewHolder.messengerWebView.setVisibility(TextView.GONE);
        if (friendlyMessage.getPhotoUrl() == null) {
            viewHolder.messengerImageView.setImageResource(R.mipmap.ic_default_user);
        } else {
            Glide.with(mContext)
                    .load(friendlyMessage.getPhotoUrl())
                    .into(viewHolder.messengerImageView);

        }
    }

    private void setText(final FriendlyMessage friendlyMessage, MessageViewHolder viewHolder) {


        if (isLic(friendlyMessage.getText())) {

            class MyWebViewClient extends WebViewClient {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            }
            //viewHolder.messengerWebView.getSettings().setJavaScriptEnabled(true);
            viewHolder.messengerWebView.loadUrl(friendlyMessage.getText());
            viewHolder.messengerWebView.setWebViewClient(new MyWebViewClient());


            viewHolder.messageTextView.setText(Uri.parse(friendlyMessage.getText()).getAuthority());

            viewHolder.messengerLinearLayout.setVisibility(TextView.VISIBLE);
            viewHolder.messengerWebView.setVisibility(TextView.VISIBLE);
            viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
            viewHolder.messageImageView.setVisibility(ImageView.GONE);
            viewHolder.messageVideoView.setVisibility(ImageView.GONE);
            viewHolder.messengerPlayButton.setVisibility(VideoView.GONE);

            viewHolder.messengerWebView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "YBхуя ", Toast.LENGTH_SHORT).show();
                    Intent openlinkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(friendlyMessage.getText()));
                    mContext.startActivity(openlinkIntent);
                }
            });
        } else {
            viewHolder.messageTextView.setText(friendlyMessage.getText());

            viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
            viewHolder.messengerLinearLayout.setVisibility(TextView.GONE);
            viewHolder.messageImageView.setVisibility(ImageView.GONE);
            viewHolder.messageVideoView.setVisibility(ImageView.GONE);
            viewHolder.messengerPlayButton.setVisibility(VideoView.GONE);
        }
    }
    private void setVideo(FriendlyMessage friendlyMessage, MessageViewHolder viewHolder) {

        viewHolder.messageVideoView.setVideoPath(friendlyMessage.getVideoUrl());
        viewHolder.messageVideoView.setMediaController(new MediaController(mContext));
        viewHolder.messageVideoView.requestFocus();
        //viewHolder.messageVideoView.start();

        viewHolder.messageVideoView.setVisibility(VideoView.VISIBLE);
        viewHolder.messageImageView.setVisibility(ImageView.GONE);
        viewHolder.messageTextView.setVisibility(TextView.GONE);
        viewHolder.messengerPlayButton.setVisibility(VideoView.GONE);
        viewHolder.messengerLinearLayout.setVisibility(TextView.GONE);
    }

    private void setImage(FriendlyMessage friendlyMessage, final MessageViewHolder viewHolder) {

        String imageUrl = friendlyMessage.getImageUrl();

        if (imageUrl.startsWith("gs://")) {

            StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(imageUrl);
            storageReference.getDownloadUrl().addOnCompleteListener(
                    new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String downloadUrl = task.getResult().toString();
                                Glide.with(viewHolder.messageImageView.getContext())
                                        .load(downloadUrl)
                                        .into(viewHolder.messageImageView);
                            } else {
                                Log.w(TAG, "Getting download url was not successful.",
                                        task.getException());
                            }
                        }
                    });
        } else {

            Glide.with(viewHolder.messageImageView.getContext())
                    .load(friendlyMessage.getImageUrl())
                    .into(viewHolder.messageImageView);
        }
        viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
        viewHolder.messageTextView.setVisibility(TextView.GONE);
        viewHolder.messageVideoView.setVisibility(ImageView.GONE);
        viewHolder.messengerPlayButton.setVisibility(VideoView.GONE);
        viewHolder.messengerLinearLayout.setVisibility(TextView.GONE);
    }
    private boolean isLic(String  text){

        String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(text);//replace with string to compare
        return m.find();
    }
    private void scrollToLastMessage(){
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            //Show last message
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition  = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

    }
}
