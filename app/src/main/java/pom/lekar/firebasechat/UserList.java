package pom.lekar.firebasechat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
    ArrayList<User>mUsers;
    UsersListAdaper usersListAdaper;
    ListView mListView;

    @Override
    protected void onPostResume() {

        usersListAdaper.notifyDataSetChanged();
        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mUsers= new ArrayList<>();

        mListView = (ListView) findViewById(R.id.user_list_list_view);

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);


        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                mListView.setAdapter( new ArrayAdapter<>(this,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                R.layout.item_users, mUsers));

        usersListAdaper = new  UsersListAdaper(getAllUsersFromFirebase(),this);
        usersListAdaper.notifyDataSetChanged();
        mMessageRecyclerView.setAdapter( usersListAdaper);




    }


    public  ArrayList<User> getAllUsersFromFirebase() {
        final ArrayList<User> userArrayList=new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.ARG_USERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren()
                                .iterator();
                        List<User> users = new ArrayList<>();
                        while (dataSnapshots.hasNext()) {
                            DataSnapshot dataSnapshotChild = dataSnapshots.next();
                            User user = dataSnapshotChild.getValue(User.class);
                            if (!TextUtils.equals(user.getUid(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                users.add(user);
                                userArrayList.add(user);

                                usersListAdaper.notifyDataSetChanged();
                                Toast.makeText(UserList.this, user.getName(), Toast.LENGTH_SHORT).show();
                            }
                        }

//                        usersListAdaper.notifyDataSetChanged();
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
