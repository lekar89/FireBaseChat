

package pom.lekar.firebasechat.utils;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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

import pom.lekar.firebasechat.Constants;
import pom.lekar.firebasechat.models.FriendlyMessage;
import pom.lekar.firebasechat.ui.activities.ChatActivity;
import pom.lekar.firebasechat.ui.activities.LoginActivity;

import static com.google.android.gms.internal.zzt.TAG;
import static pom.lekar.firebasechat.Constants.ARG_MESSAGE;
import static pom.lekar.firebasechat.Constants.EXTRA_ID;
import static pom.lekar.firebasechat.Constants.REQUEST_AUDIO;
import static pom.lekar.firebasechat.Constants.REQUEST_IMAGE;
import static pom.lekar.firebasechat.Constants.REQUEST_LOCATION;
import static pom.lekar.firebasechat.Constants.REQUEST_VIDEO;

///**
// * Created by lekar on 12.05.17.
// */
//
//public class Utils {
//
//    FirebaseUser      mFirebaseUser;
//    DatabaseReference mFirebaseDatabaseReference;
//    Context           mContext;
//    MessageShower     mMessageShower;
//    String            mReceiver;
//    ChatActivity      mChatActivity;
//
//    public Utils( Context mContext, String mReceiver,ChatActivity mChatActivity) {
//
//        this.mContext      = mContext;
//        this.mChatActivity = mChatActivity;
//        this.mReceiver     = mReceiver;
//
//        initialize();
//    }
//
//    public Utils( Context mContext) {
//        this.mContext = mContext;
//    }
//
//    private void initialize(){
//
//        mReceiver                  = mChatActivity   .getIntent()  .getStringExtra(EXTRA_ID);
//        mFirebaseUser              = FirebaseAuth    .getInstance().getCurrentUser();
//        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        mMessageShower             = new MessageShower(mContext,mChatActivity);
//    }
//
//    public void isUserAuth( FirebaseUser mFireBaseUser) {
//
//        if (mFireBaseUser == null) {
//            mContext.startActivity(new Intent(mContext, LoginActivity.class));
//        }
//    }
//
//    private void putInStorage(StorageReference storageReference, Uri uri,  final int type) {
//
//        storageReference.putFile(uri).addOnCompleteListener(mChatActivity,
//                new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            FriendlyMessage friendlyMessage =
//                                    new FriendlyMessage(mFirebaseUser.getDisplayName(), mFirebaseUser.getPhotoUrl().toString());
//                            String fileURL = task.getResult().getMetadata().getDownloadUrl().toString();
//
//                            if (type == REQUEST_IMAGE) {
//                                friendlyMessage.setImageUrl(fileURL);
//                                sendMessageToFireBase(friendlyMessage);
//
//                            } else if (type == REQUEST_VIDEO) {
//                                friendlyMessage.setVideoUrl(fileURL);
//                                sendMessageToFireBase(friendlyMessage);
//
//                            } else if (type == REQUEST_AUDIO) {
//                                friendlyMessage.setAudioUrl(fileURL);
//                                sendMessageToFireBase(friendlyMessage);
//
//                                if (type == REQUEST_LOCATION) {
//
//                                    Place place = PlacePicker.getPlace(mContext,mChatActivity.getIntent());
//
//                                    // friendlyMessage.setPlace(place);
//                                    sendMessageToFireBase(friendlyMessage);
//                                    //Place place = PlacePicker.getPlace(data, this);
//
//                                }
//                            } else {
//                                Log.w(TAG, "Image upload task was not successful.",
//                                        task.getException());
//                            }
//                        }
//
//                    }
//                });
//    }
//    public  void roomChooser() {
//
//        final String room_type_1 = mFirebaseUser.getUid() + "_" + mReceiver;
//        final String room_type_2 = mReceiver + "_" + mFirebaseUser.getUid();
//
//        mFirebaseDatabaseReference.child(Constants.ARG_ROOMS)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.hasChild(room_type_1)) {
//                            Log.e(TAG,  " room_type_1 isExist");
//                            mMessageShower.showMessage(room_type_1);
//
//                        } else if (dataSnapshot.hasChild(room_type_2)) {
//                            Log.e(TAG,  " room_type_2   isExist ");
//                            mMessageShower.showMessage(room_type_2);
//
//                        } else {
//                            Log.e(TAG,  " case 3 createdRoom");
//                            mFirebaseDatabaseReference
//                                    .child(Constants.ARG_ROOMS)
//                                    .child(room_type_1)
//                                    .push()
//                                    .setValue(new FriendlyMessage(" Hi send you first  message ","","","",""));
//
//                            mMessageShower.showMessage(room_type_1);
//                        }
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) { }
//
//                });
//    }
//
//    public void sendFileMessage(final Uri uri, final int requestCode) {
//
//        FriendlyMessage tempMessage = new FriendlyMessage(null, mFirebaseUser.getDisplayName(), null,
//                null, null);
//
//        mFirebaseDatabaseReference.child(ARG_MESSAGE).push()
//                .setValue(tempMessage, new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(DatabaseError databaseError,
//                                           DatabaseReference databaseReference) {
//                        if (databaseError == null) {
//                            String key = databaseReference.getKey();
//                            StorageReference storageReference =
//                                    FirebaseStorage.getInstance()
//                                            .getReference(mFirebaseUser.getUid())
//                                            .child(key)
//                                            .child(uri.getLastPathSegment());
//
//                            putInStorage(storageReference, uri, requestCode);
//                        } else {
//                            Log.w(TAG, "Unable to write message to database.",
//                                    databaseError.toException());
//                        }
//                    }
//                });
//    }
//
//    public void sendMessageToFireBase(final FriendlyMessage friendlyMessage) {
//
//        final String room_type_1 = mFirebaseUser.getUid() + "_" + mReceiver;
//        final String room_type_2 = mReceiver + "_" + mFirebaseUser.getUid();
//
//        mFirebaseDatabaseReference.child(Constants.ARG_ROOMS)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.hasChild(room_type_1)) {
//                            Log.e(TAG, "sendMessageToFireBase: " + room_type_1 + " exists");
//                            mFirebaseDatabaseReference
//                                    .child(Constants.ARG_ROOMS)
//                                    .child(room_type_1)
//                                    .push()
//                                    .setValue(friendlyMessage);
//
//                        } else if (dataSnapshot.hasChild(room_type_2)) {
//                            Log.e(TAG, "sendMessageToFireBase: " + room_type_2 + " exists");
//                            mFirebaseDatabaseReference
//                                    .child(Constants.ARG_ROOMS)
//                                    .child(room_type_2)
//                                    .push()
//                                    .setValue(friendlyMessage);
//
//                        } else {
//                            Log. e(TAG, "Create newRoom");
//                            mFirebaseDatabaseReference
//                                    .child(Constants.ARG_ROOMS)
//                                    .child(room_type_1)
//                                    .push()
//                                    .setValue(friendlyMessage);
//                        }
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        // Unable to send message.
//                    }
//                });
//    }
//
//    public void buttonShow(EditText mMessageEditText, final Button mSendButton) {
//
//        mMessageEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.toString().trim().length() > 0) { mSendButton.setEnabled(true);
//                }
//                else { mSendButton.setEnabled(false);  }
//            }
//            @Override
//            public void afterTextChanged(Editable editable) {  }
//        });
//    }
//
//}
public class Utils {

    FirebaseUser mFirebaseUser;
    DatabaseReference mFirebaseDatabaseReference;
    Context mContext;
    MessageShower mMessageShower;
    String mReceiver;
    ChatActivity mChatActivity;

    public Utils( Context mContext, String mReceiver,ChatActivity mChatActivity) {

        this.mContext = mContext;
        this.mChatActivity= mChatActivity;
        this.mReceiver = mReceiver;
        initialize();
    }

    public Utils( Context mContext) {
        this.mContext = mContext;
    }

    private void initialize(){

        mReceiver=mChatActivity.getIntent().getStringExtra(EXTRA_ID);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mMessageShower = new MessageShower(mContext,mChatActivity);
    }

    public void isUserAuth( FirebaseUser mFirebaseUser) {

        if (mFirebaseUser == null) {
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
        }
    }

    private void putInStorage(StorageReference storageReference, Uri uri, final int type) {

        storageReference.putFile(uri).addOnCompleteListener(mChatActivity,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            FriendlyMessage friendlyMessage =
                                    new FriendlyMessage(mFirebaseUser.getDisplayName(), mFirebaseUser.getPhotoUrl().toString());
                            String fileURL = task.getResult().getMetadata().getDownloadUrl().toString();

                            if (type == REQUEST_IMAGE) {
                                friendlyMessage.setImageUrl(fileURL);
                                sendMessageToFireBase(friendlyMessage);

                            } else if (type == REQUEST_VIDEO) {
                                friendlyMessage.setVideoUrl(fileURL);
                                sendMessageToFireBase(friendlyMessage);

                            } else if (type == REQUEST_AUDIO) {
                                friendlyMessage.setAudioUrl(fileURL);
                                sendMessageToFireBase(friendlyMessage);

                                if (type == REQUEST_LOCATION) {

                                    Place place = PlacePicker.getPlace(mContext,mChatActivity.getIntent());

                                    // friendlyMessage.setPlace(place);
                                    sendMessageToFireBase(friendlyMessage);
                                    //Place place = PlacePicker.getPlace(data, this);

                                }

                            } else {
                                Log.w(TAG, "Image upload task was not successful.",
                                        task.getException());
                            }
                        }

                    }
                });
    }
    public  void roomChooser() {

        final String room_type_1 = mFirebaseUser.getUid() + "_" + mReceiver;
        final String room_type_2 = mReceiver + "_" + mFirebaseUser.getUid();

        mFirebaseDatabaseReference.child(Constants.ARG_ROOMS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            Log.e(TAG,  " room_type_1 isExist");
                            mMessageShower.showMessage(room_type_1);

                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            Log.e(TAG,  " room_type_2   isExist ");
                            mMessageShower.showMessage(room_type_2);

                        } else {
                            Log.e(TAG,  " case 3 createdRoom");
                            mFirebaseDatabaseReference
                                    .child(Constants.ARG_ROOMS)
                                    .child(room_type_1)
                                    .push()
                                    .setValue(new FriendlyMessage("","","","",""));

                            mMessageShower.showMessage(room_type_1);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }

                });
    }

    public void sendFileMessage(final Uri uri, final int requestCode) {

        FriendlyMessage tempMessage = new FriendlyMessage(null, mFirebaseUser.getDisplayName(), null,
                null, null);

        mFirebaseDatabaseReference.child(ARG_MESSAGE).push()
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

                            putInStorage(storageReference, uri, requestCode);
                        } else {
                            Log.w(TAG, "Unable to write message to database.",
                                    databaseError.toException());
                        }
                    }
                });
    }

    public void sendMessageToFireBase(final FriendlyMessage friendlyMessage) {

        final String room_type_1 = mFirebaseUser.getUid() + "_" + mReceiver;
        final String room_type_2 = mReceiver + "_" + mFirebaseUser.getUid();

        mFirebaseDatabaseReference.child(Constants.ARG_ROOMS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            Log.e(TAG, "sendMessageToFireBase: " + room_type_1 + " exists");
                            mFirebaseDatabaseReference
                                    .child(Constants.ARG_ROOMS)
                                    .child(room_type_1)
                                    .push()
                                    .setValue(friendlyMessage);

                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            Log.e(TAG, "sendMessageToFireBase: " + room_type_2 + " exists");
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

    public void buttonShow(EditText mMessageEditText, final Button mSendButton) {

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
    }

}