package pom.lekar.firebasechat.servises;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pom.lekar.firebasechat.Constants;
import pom.lekar.firebasechat.R;
import pom.lekar.firebasechat.ui.activities.UserListActivity;
import pom.lekar.firebasechat.models.FriendlyMessage;


public class MyIntentService extends IntentService {

    //ОСТОРОЖНО ГОВНОКОД !!!

    public MyIntentService() { super("MyIntentService");}

    final String TAG = "SEVISEEE";
    FirebaseUser      mFirebaseUser;
    FriendlyMessage   mFriendlyMessage;
    DatabaseReference mFirebaseDatabaseRef;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");

        mFirebaseUser        = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();

    }


    void addListenerToAllMyRooms() {

        mFirebaseDatabaseRef.child(Constants.ARG_ROOMS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //ПОЧТИ РАБОТАЮЩИЙ  ГОВНОКОД
                        List<String> set = new ArrayList<>();
                        List<String> set2 = new ArrayList<>();
                        Iterator iterator = dataSnapshot.getChildren().iterator();

                        while (iterator.hasNext()) {
                            set.add(((DataSnapshot) iterator.next()).getKey());
                        }

                        for (String s : set) {
                            if (s.contains(mFirebaseUser.getUid() + "_") || s.contains("_" + mFirebaseUser.getUid()))
                                set2.add(s);
                        }

                        for (String s : set2) {
                            mFirebaseDatabaseRef.child(Constants.ARG_ROOMS).child(s).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot mDataSnapshot) {
                                    Log.e(TAG, "");
                                    Map<String,String>  map = (Map) mDataSnapshot.getValue();
                                    sendNotification(map);
                                }

                                @Override
                                public void onCancelled(DatabaseError mDatabaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent");
        addListenerToAllMyRooms();
    }

    void sendNotification(Map<String, String> mMap) {
        if (mMap != null) {
            NotificationCompat.Builder mBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("FireBase Message")
                            .setContentText("У вас новое вообщение от " + mMap.get("name") + ":"
                                    + mMap.get("text"));

            Intent resultIntent = new Intent(this, UserListActivity.class);


            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(UserListActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(1, mBuilder.build());
        }
    }



}
