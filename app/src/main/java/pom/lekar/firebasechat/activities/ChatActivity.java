package pom.lekar.firebasechat.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import pom.lekar.firebasechat.Constants;
import pom.lekar.firebasechat.R;
import pom.lekar.firebasechat.models.FriendlyMessage;


import static pom.lekar.firebasechat.activities.MainActivity.MESSAGES_CHILD;

public class ChatActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private FirebaseAuth      mFirebaseAuth;
    private FirebaseUser      mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>
            mFirebaseAdapter;

    private static final String ANONYMOUS = "anonymous";
    private static final String TAG = "ChatActyvity";

    private static final int REQUEST_VIDEO = 1;
    private static final int REQUEST_IMAGE = 2;

    private String mUsername;
    private String mPhotoUrl;
    private String mReceiver;

    private Button              mSendButton;
    private ProgressBar         mProgressBar;
    private EditText            mMessageEditText;
    private ImageView           mAddMessageImageView;
    private ImageView           mAddMessageVideoView;
    private RecyclerView        mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle(getIntent().getStringExtra(Constants.USER_NAME));

        mSendButton          = (Button)       findViewById(R.id.sendButton);
        mProgressBar         = (ProgressBar)  findViewById(R.id.progressBar);
        mMessageEditText     = (EditText)     findViewById(R.id.messageEditText);
        mAddMessageVideoView = (ImageView)    findViewById(R.id.addMessageVideo);
        mAddMessageImageView = (ImageView)    findViewById(R.id.addMessageImageView);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);

        mSendButton         .setOnClickListener(this);
        mAddMessageVideoView.setOnClickListener(this);
        mAddMessageImageView.setOnClickListener(this);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mReceiver = getIntent().getStringExtra(Constants.ID);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        isUserAuth();
        roomChooser();

        //disable or enable button send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) { mSendButton.setEnabled(true);
                }
                else { mSendButton.setEnabled(false);  }
            }
            @Override
            public void afterTextChanged(Editable editable) {  }
        });

    }//end of onCreate

    public  void roomChooser() {

        final String room_type_1 = mFirebaseUser.getUid() + "_" + mReceiver;
        final String room_type_2 = mReceiver + "_" + mFirebaseUser.getUid();

        mFirebaseDatabaseReference.child(Constants.ARG_ROOMS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            Log.e(TAG,  " room_type_1 isExist");
                            showMessage(room_type_1);

                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            Log.e(TAG,  " room_type_2   isExist ");
                            showMessage(room_type_2);

                        } else {
                            Log.e(TAG,  " case 3 createdRoom");
                            mFirebaseDatabaseReference
                                    .child(room_type_1)
                                    .push()
                                    .setValue(new FriendlyMessage("","","","",""));

                            showMessage(room_type_1);

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }

                });
    }// end of roomChooser

    public void showMessage(String mRoomName)
    {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(
                FriendlyMessage.class,
                R.layout.item_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(Constants.ARG_ROOMS)
                        .child( mRoomName))
        {

            @Override
            protected FriendlyMessage parseSnapshot(DataSnapshot snapshot) {

                FriendlyMessage friendlyMessage = super.parseSnapshot(snapshot);
                if (friendlyMessage != null)
                { friendlyMessage.setId(snapshot.getKey());   }
                return friendlyMessage;
            }


            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder,
                                              FriendlyMessage friendlyMessage,
                                              int position) {

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.messengerTextView.setText(friendlyMessage.getName());

                if (friendlyMessage.getPhotoUrl() == null) {
                    //Сменинь на нормальную заглушку!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                            R.drawable.ic_add_black));
                } else {
                    Glide.with(ChatActivity.this)
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }

                //IF TEXT
                if (friendlyMessage.getText() != null) {
                    viewHolder.messageTextView.setText(friendlyMessage.getText());

                    viewHolder.messageTextView.  setVisibility(TextView.VISIBLE);
                    viewHolder.messageImageView. setVisibility(ImageView.GONE);
                    viewHolder.messageVideoeView.setVisibility(ImageView.GONE);

                    //IF  IMAGE
                } else if(friendlyMessage.getImageUrl()!= null) {

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
                    viewHolder.messageImageView. setVisibility(ImageView.VISIBLE);
                    viewHolder.messageTextView.  setVisibility(TextView.GONE);
                    viewHolder.messageVideoeView.setVisibility(ImageView.GONE);

                    //IF  VIDEO
                } else if (friendlyMessage.getVideoUrl() != null) {

///Видео сетить здесь бля !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    String videoUrl = friendlyMessage.getVideoUrl();

                    viewHolder.messageVideoeView.setVideoPath(videoUrl);
                    //viewHolder.messageVideoeView.start();
//                    viewHolder.messageVideoeView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            viewHolder.messageVideoeView.start();
//                        }
//                    });

                    viewHolder.messageVideoeView.setVisibility(VideoView.VISIBLE);
                    viewHolder.messageImageView. setVisibility(ImageView.GONE);
                    viewHolder.messageTextView.  setVisibility(TextView.GONE);
                }
            }
        };

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

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
    }// end of showMessage

    public void sendMessageToFirebase(final FriendlyMessage friendlyMessage) {

        final String room_type_1 = mFirebaseUser.getUid() + "_" + mReceiver;
        final String room_type_2 = mReceiver + "_" + mFirebaseUser.getUid();


        mFirebaseDatabaseReference.child(Constants.ARG_ROOMS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            Log.e(TAG, "sendMessageToFirebase: " + room_type_1 + " exists");
                            mFirebaseDatabaseReference
                                    .child(Constants.ARG_ROOMS)
                                    .child(room_type_1)
                                    .push()
                                    .setValue(friendlyMessage);

                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            Log.e(TAG, "sendMessageToFirebase: " + room_type_2 + " exists");
                            mFirebaseDatabaseReference
                                    .child(Constants.ARG_ROOMS)
                                    .child(room_type_2)
                                    .push()
                                    .setValue(friendlyMessage);

                        } else {
                            Log. e(TAG, "Create newRoom");
                            mFirebaseDatabaseReference
                                    .child(Constants.ARG_ROOMS)

                                    .child(room_type_1)
                                    .push()
                                    .setValue(friendlyMessage);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to send message.
                    }
                });
    }
    //end of sendMessage

    private void isUserAuth() {

        if (mFirebaseUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();

        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE || requestCode == REQUEST_VIDEO) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());

                    FriendlyMessage tempMessage = new FriendlyMessage(null, mUsername, mPhotoUrl,
                            null, null);

                    mFirebaseDatabaseReference.child(MESSAGES_CHILD).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(mFirebaseUser.getUid())
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());

                                        putInStorage(storageReference, uri, key,requestCode);
                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }
    }

    private void putInStorage(StorageReference storageReference, Uri uri, final String key, final int type) {
        storageReference.putFile(uri).addOnCompleteListener(ChatActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            FriendlyMessage friendlyMessage = null;
                            if (type == REQUEST_IMAGE) {
                                friendlyMessage =
                                        new FriendlyMessage(null, mUsername, mPhotoUrl,
                                                task.getResult().getMetadata().getDownloadUrl()
                                                        .toString(), null);
                            } else if (type == REQUEST_VIDEO) {
                                friendlyMessage =
                                        new FriendlyMessage(null, mUsername, mPhotoUrl, null,
                                                task.getResult().getMetadata().getDownloadUrl()
                                                        .toString());
                            }

                            sendMessageToFirebase(friendlyMessage);

                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_list:

                startActivity(new Intent(ChatActivity.this, UserListActivity.class));
                return true;

            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            case R.id.sign_in_menu:
                startActivity(new Intent(ChatActivity.this, UserListActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

        Intent intent;
        switch (v.getId()) {
            case R.id.sendButton:
                FriendlyMessage friendlyMessage = new FriendlyMessage
                        (mMessageEditText.getText().toString(), mUsername,
                                mPhotoUrl, null, null);
                sendMessageToFirebase(friendlyMessage);
                mMessageEditText.setText("");
                break;

            case R.id.addMessageVideo:
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("video/*");
                startActivityForResult(intent, REQUEST_VIDEO);

                Toast.makeText(this, "video", Toast.LENGTH_SHORT).show();
                break;

            case R.id.addMessageImageView:
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);

                Toast.makeText(this, "image", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView        messageTextView;
        TextView        messengerTextView;
        ImageView       messageImageView;
        VideoView       messageVideoeView;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView    = (TextView)         itemView.findViewById(R.id.messageTextView);
            messageImageView   = (ImageView)        itemView.findViewById(R.id.messageImageView);
            messageVideoeView  = (VideoView)        itemView.findViewById(R.id.messageVideoView);
            messengerTextView  = (TextView)         itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView)  itemView.findViewById(R.id.messengerImageView);
        }

    }
}
