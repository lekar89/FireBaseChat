package pom.lekar.firebasechat.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
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

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pom.lekar.firebasechat.Constants;
import pom.lekar.firebasechat.R;
import pom.lekar.firebasechat.models.FriendlyMessage;
import pom.lekar.firebasechat.ui.Dialogs.DialogAudio;
import pom.lekar.firebasechat.ui.Dialogs.DialogMap;
import pom.lekar.firebasechat.ui.Dialogs.DialogVideo;
import pom.lekar.firebasechat.ui.MessageViewHolder;
import pom.lekar.firebasechat.ui.activities.ChatActivity;

import static com.google.android.gms.internal.zzt.TAG;



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
                viewHolder.messengerTime.setText(friendlyMessage.getTime());
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
                    setLocation(friendlyMessage, viewHolder);

                }

            }
        };

        scrollToLastMessage();
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
    }


    private void setLocation(FriendlyMessage mFriendlyMessage, MessageViewHolder viewHolder) {

        final String[] latLon = mFriendlyMessage.getLatLong().split("!",2);

        Glide.with(viewHolder.messageImageView.getContext())
                .load(viewHolder.messageImageView.getContext().getString(R.string.map_static_url,
                        latLon[0], latLon[1]))
                .into(viewHolder.messageImageView);

        viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
        viewHolder.messengerPlayButton.setVisibility(VideoView.GONE);
        viewHolder.messageTextView.setVisibility(TextView.GONE);
        viewHolder.messengerRelativeLayout.setVisibility(TextView.GONE);

        viewHolder.messageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("LAT",latLon[0]);
                bundle.putString("LON",latLon[1]);

                DialogMap dialogMap= new DialogMap();
                dialogMap.setArguments(bundle);
                dialogMap.show(mChatActivity.getSupportFragmentManager(), "dlg1");

            }
        });

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

    private void setAudio(final FriendlyMessage friendlyMessage, final MessageViewHolder viewHolder){

        viewHolder.messengerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("URL",friendlyMessage.getAudioUrl());
                DialogAudio dialogAudio = new DialogAudio();
                dialogAudio.setArguments(bundle);
                dialogAudio.show(mChatActivity.getSupportFragmentManager(), "DialogVideo");


            }
        });
        viewHolder.messengerPlayButton.setVisibility(View.VISIBLE);
        viewHolder.messageTextView.setText("Audio message");
        viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
        viewHolder.messageImageView.setVisibility(ImageView.GONE);
        viewHolder.messengerRelativeLayout.setVisibility(TextView.GONE);

    }

    private void setText(final FriendlyMessage friendlyMessage, MessageViewHolder viewHolder) {


        if (isLik(friendlyMessage.getText())) {

            class MyWebViewClient extends WebViewClient {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            }

            viewHolder.messengerWebView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if  (event.getAction() == MotionEvent.ACTION_UP) {
                        Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(friendlyMessage.getText()));
                        mContext.startActivity(openLinkIntent);
                        return false;
                    }
                    return false;
                }
            });
            viewHolder.messengerWebView.loadUrl(friendlyMessage.getText());
            viewHolder.messengerWebView.setWebViewClient(new MyWebViewClient());
            viewHolder.messageTextView.setText(Uri.parse(friendlyMessage.getText()).getAuthority());

            viewHolder.messengerRelativeLayout.setVisibility(TextView.VISIBLE);
            viewHolder.messengerWebView.setVisibility(TextView.VISIBLE);
            viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
            viewHolder.messageImageView.setVisibility(ImageView.GONE);
            viewHolder.messengerPlayButton.setVisibility(VideoView.GONE);



        } else {
            viewHolder.messageTextView.setText(friendlyMessage.getText());
            viewHolder.messageTextView.setVisibility(TextView.VISIBLE);

            viewHolder.messengerRelativeLayout.setVisibility(TextView.GONE);
            viewHolder.messageImageView.setVisibility(ImageView.GONE);
            viewHolder.messengerPlayButton.setVisibility(VideoView.GONE);
        }
    }

    private void setVideo(final FriendlyMessage friendlyMessage, final MessageViewHolder viewHolder) {


        viewHolder.messengerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("URL",friendlyMessage.getVideoUrl());
                DialogVideo dialogVideo = new DialogVideo();
                dialogVideo.setArguments(bundle);


                dialogVideo.show(mChatActivity.getSupportFragmentManager(), "DialogVideo");
            }
        });

        viewHolder.messageTextView.setText("Video message");
        viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
        viewHolder.messageImageView.setVisibility(ImageView.GONE);

        viewHolder.messengerPlayButton.setVisibility(VideoView.VISIBLE);
        viewHolder.messengerRelativeLayout.setVisibility(TextView.GONE);

    }

    private void setImage(final FriendlyMessage friendlyMessage, final MessageViewHolder viewHolder) {

        viewHolder.messageImageView.setMaxHeight(R.dimen.map_size);
        viewHolder.messageImageView.setMaxWidth(R.dimen.map_size);

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

        viewHolder.messengerPlayButton.setVisibility(VideoView.GONE);
        viewHolder.messengerRelativeLayout.setVisibility(TextView.GONE);


        viewHolder.messageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View imgEntryView = inflater.inflate(R.layout.dialog_item_image, null);

                final Dialog dialog=new Dialog(mContext,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                ImageView img = (ImageView) imgEntryView
                        .findViewById(R.id.dialog_image);
                Glide.with(viewHolder.messageImageView.getContext())
                        .load(friendlyMessage.getImageUrl())
                        .into(img);

                dialog.setContentView(imgEntryView);
                dialog.show();

                imgEntryView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramView) {
                        dialog.cancel();
                    }
                });
            }
        });
    }

    private boolean isLik(String text){

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


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/2011.kml");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded


        }

    }

}
