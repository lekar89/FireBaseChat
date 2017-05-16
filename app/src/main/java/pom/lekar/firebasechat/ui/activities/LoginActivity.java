package pom.lekar.firebasechat.ui.activities;

    import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import pom.lekar.firebasechat.Constants;
import pom.lekar.firebasechat.R;
import pom.lekar.firebasechat.models.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    Button       reg,logIN;
    EditText     etLoginEmail, etLoginPassword;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_login_test);

        etLoginEmail = (EditText) findViewById(R.id.et_login_email);
        etLoginPassword = (EditText) findViewById(R.id.et_login_password);

        reg = (Button) findViewById(R.id.email_create_account_button);
        logIN = (Button) findViewById(R.id.email_sign_in_button);
        firebaseAuth = FirebaseAuth.getInstance();
        logIN.setOnClickListener(this);
        reg.setOnClickListener(this);

    }

    public void login() {
        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait....", "Processing....", true);
        (firebaseAuth.signInWithEmailAndPassword(etLoginEmail.getText().toString(), etLoginPassword.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Registration successful", Toast.LENGTH_LONG).show();

                            String name  = etLoginEmail
                                    .getText()
                                    .toString()
                                    .substring(0,etLoginEmail
                                            .getText()
                                            .toString().indexOf("@"));

                            String tmpPhotoURL="https://pbs.twimg.com/profile_images/742262665574293504/Y95u94YS.jpg";

                            FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                            addUserToDatabase(mFirebaseUser.getUid()+"",name,tmpPhotoURL);

                            updateFireBaseUser(  mFirebaseUser,  name,  tmpPhotoURL );

                            Intent i = new Intent(LoginActivity.this, UserListActivity.class);
                           // i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                            startActivity(i);
                        } else {
                            Log.e("ERROR", task.getException().getMessage());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void registration() {
        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait....", "Processing....", true);
        (firebaseAuth.createUserWithEmailAndPassword(etLoginEmail.getText().toString(), etLoginPassword.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
                            login();
//                            Intent i = new Intent(LoginActivity.this, LoginActivity.class);
//                            startActivity(i);
                        } else {
                            Log.e("ERROR", task.getException().getMessage());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private  void updateFireBaseUser( FirebaseUser mFirebaseUser, String name, String tmpPhotoURL ){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(Uri.parse(tmpPhotoURL))
                .build();

        mFirebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });


    }
    public void addUserToDatabase( String id,String name,String photo) {
        User user= new User(id,name,photo);
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.ARG_USERS)
                .child(user.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void> () {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // successfully added user
                        } else {
                            // failed to add user
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_sign_in_button:
                login();
                break;case R.id.email_create_account_button:
                registration();
                break;
        }
    }
}
