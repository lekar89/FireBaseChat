package pom.lekar.firebasechat.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pom.lekar.firebasechat.Constants;
import pom.lekar.firebasechat.R;
import pom.lekar.firebasechat.adapters.UsersListAdaper;
import pom.lekar.firebasechat.models.User;

public class UserListActivity extends AppCompatActivity {
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
        isUserAuth();

    }

    private void isUserAuth() {

        FirebaseUser mFirebaseUser =  FirebaseAuth.getInstance().getCurrentUser();

        if (mFirebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();

        }
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

                        while (dataSnapshots.hasNext()) {
                            DataSnapshot dataSnapshotChild = dataSnapshots.next();
                            User user = dataSnapshotChild.getValue(User.class);
                            if (!TextUtils.equals(user.getUid(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                users.add(user);
                                userArrayList.add(new User(user.getUid(), user.getName(), user.getPhotoUrl()));

                            }
                        }

                        usersListAdaper = new  UsersListAdaper(userArrayList,mContext);
                        mMessageRecyclerView.setAdapter( usersListAdaper);
                        usersListAdaper.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to retrieve the users.
                    }
                });

          return userArrayList;
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
                startActivity(new Intent(UserListActivity.this, UserListActivity.class));
                return true;
            case R.id.sign_out_menu:
//                mFirebaseAuth.signOut();
//                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
