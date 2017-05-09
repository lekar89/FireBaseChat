package pom.lekar.firebasechat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserList extends AppCompatActivity {
    private RecyclerView mMessageRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    ArrayList<User> mUsers;
    UsersListAdaper usersListAdaper;

    Context mContext= this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mUsers= new ArrayList<>();
        mUsers = getAllUsersFromFirebase();



        mMessageRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

//        usersListAdaper = new  UsersListAdaper(mUsers,this);
//        mMessageRecyclerView.setAdapter( usersListAdaper);
        //usersListAdaper.notifyDataSetChanged();


    }

    public ArrayList<User> getAllUsersFromFirebase() {
       final ArrayList<User> userArrayList =new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.ARG_USERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren()
                                .iterator();
                        List<User> users = new ArrayList<>();
                        int i = 0;
                        while (dataSnapshots.hasNext()) {;
                            DataSnapshot dataSnapshotChild = dataSnapshots.next();
                            User user = dataSnapshotChild.getValue(User.class);
                            if (!TextUtils.equals(user.getUid(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                users.add(user);
                                userArrayList.add(new User(user.getUid(), user.getName(), user.getPhotoUrl()));
                                Toast.makeText(UserList.this,
                                        users.get(i++).getName(), Toast.LENGTH_SHORT).show();
                               // Toast.makeText(UserList.this, userArrayList.size(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        userArrayList.add(new User("sdf","sdfs","http://www.simflight.com/wp-content/uploads/2015/07/11411901_1624097991165015_4700322721972491595_o.jpg"));
                        usersListAdaper = new  UsersListAdaper(userArrayList,mContext);
                        mMessageRecyclerView.setAdapter( usersListAdaper);
                        usersListAdaper.notifyDataSetChanged();
                        // All users are retrieved except the one who is currently logged
                        // in device.
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to retrieve the users.
                    }
                });

          return userArrayList;
    }

}
