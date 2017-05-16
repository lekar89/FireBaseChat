package pom.lekar.firebasechat.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pom.lekar.firebasechat.Constants;
import pom.lekar.firebasechat.R;
import pom.lekar.firebasechat.models.FriendlyMessage;
import pom.lekar.firebasechat.utils.Utils;

import static pom.lekar.firebasechat.Constants.ANONYMOUS;
import static pom.lekar.firebasechat.Constants.REQUEST_AUDIO;
import static pom.lekar.firebasechat.Constants.REQUEST_IMAGE;
import static pom.lekar.firebasechat.Constants.REQUEST_LOCATION;
import static pom.lekar.firebasechat.Constants.REQUEST_VIDEO;

//jamDroidFireChat
public class ChatActivity extends AppCompatActivity
//                        extends   YouTubeBaseActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    ///Вынести логиуку в Адаптеры
    /// Андроид когд стаил
    //написать интефейчсы

    private static final String TAG = "ChatActyvity";


    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private String mUsername;
    private String mPhotoUrl;
    private String mReceiver;

    private Button    mSendButton;
    private EditText  mMessageEditText;
    private ImageView mAddMessageImage;
    private ImageView mAddMessageVideo;
    private ImageView mAddMessageAudio;
    private ImageView mAddMessageLocation;
    private ImageView mAddMessage;

    private Utils mUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R
                .layout.activity_chat);
        setTitle(getIntent().getStringExtra(Constants.EXTRA_USER_NAME));

        mSendButton          = (Button) findViewById(R.id.sendButton);
        mMessageEditText     = (EditText) findViewById(R.id.messageEditText);
        mAddMessageVideo = (ImageView) findViewById(R.id.addMessageVideo);
        mAddMessageImage = (ImageView) findViewById(R.id.addMessageImage);
        mAddMessageAudio = (ImageView) findViewById(R.id.addMessageAudio);
        mAddMessageLocation = (ImageView) findViewById(R.id.addMessageLocation);

        mSendButton.     setOnClickListener(this);
        mAddMessageVideo.setOnClickListener(this);
        mAddMessageImage.setOnClickListener(this);
        mAddMessageAudio.setOnClickListener(this);
        mAddMessageLocation.setOnClickListener(this);

        mUtils        = new Utils( this,  mReceiver, ChatActivity.this);
        mReceiver     = getIntent().getStringExtra(Constants.EXTRA_ID);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mUtils.buttonShow(mMessageEditText, mSendButton);
        mUtils.isUserAuth( mFirebaseUser);
        mUtils.roomChooser();

        mUsername = mFirebaseUser.getDisplayName();

        setPhtoto();

    }//end of onCreate

    private void setPhtoto() {
        if (mFirebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
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

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE || requestCode == REQUEST_VIDEO ||requestCode ==REQUEST_AUDIO) {

                if (data != null) {
                    Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());

                    mUtils.sendFileMessage(uri, requestCode);
                }
            }

            if (requestCode == REQUEST_LOCATION) {
                Place place = PlacePicker.getPlace(data, this);
                //Place place = PlacePicker.getPlace(this,data);

                FriendlyMessage message = new FriendlyMessage ( mUsername,mPhotoUrl);
                message.setLatLong(String.valueOf(place.getLatLng().latitude)+"!"+String.valueOf(place.getLatLng().longitude));

                mUtils.sendMessageToFirebase(message);
            }
        }

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
                startActivity(new Intent(this, LoginActivity.class));
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
                mUtils.sendMessageToFirebase(friendlyMessage);
                mMessageEditText.setText("");
                break;

            case R.id.addMessageVideo:
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("video/*");
                startActivityForResult(intent, REQUEST_VIDEO);
                Toast.makeText(this, "video", Toast.LENGTH_SHORT).show();
                break;

            case R.id.addMessageImage:
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
                Toast.makeText(this, "image", Toast.LENGTH_SHORT).show();
                break;

            case R.id.addMessageAudio:
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(intent, REQUEST_AUDIO);
                Toast.makeText(this, "audio", Toast.LENGTH_SHORT).show();
                break;

           case R.id.addMessageLocation:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(this), REQUEST_LOCATION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


        }
    }
}