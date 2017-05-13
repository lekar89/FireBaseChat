package pom.lekar.firebasechat.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

//                }else if (friendlyMessage.getAudio() != null) {
//                    setAudio(friendlyMessage,viewHolder);

//                }else if (friendlyMessage.getLocation() != null) {
//                    setLocation((friendlyMessage,viewHolder);
//
//
//                }else if (friendlyMessage.getLinc() != null) {
//                    setLinc(friendlyMessage,viewHolder);
                }
            }
        };
        scrollToLastMessage();
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
    }

    private void setUserPhoto( FriendlyMessage friendlyMessage,  MessageViewHolder viewHolder) {

        if (friendlyMessage.getPhotoUrl() == null) {
            viewHolder.messengerImageView.setImageResource(R.mipmap.ic_default_user);
        } else {
            Glide.with(mContext)
                    .load(friendlyMessage.getPhotoUrl())
                    .into(viewHolder.messengerImageView);
        }
    }

    private void setText(FriendlyMessage friendlyMessage, MessageViewHolder viewHolder) {

        viewHolder.messageTextView.setText(friendlyMessage.getText());

        viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
        viewHolder.messageImageView.setVisibility(ImageView.GONE);
        viewHolder.messageVideoView.setVisibility(ImageView.GONE);
    }

    private void setVideo(FriendlyMessage friendlyMessage, MessageViewHolder viewHolder) {

        viewHolder.messageVideoView.setVideoPath(friendlyMessage.getVideoUrl());
        viewHolder.messageVideoView.setMediaController(new MediaController(mContext));
        viewHolder.messageVideoView.requestFocus();
        //viewHolder.messageVideoView.start();

        viewHolder.messageVideoView.setVisibility(VideoView.VISIBLE);
        viewHolder.messageImageView.setVisibility(ImageView.GONE);
        viewHolder.messageTextView.setVisibility(TextView.GONE);
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
