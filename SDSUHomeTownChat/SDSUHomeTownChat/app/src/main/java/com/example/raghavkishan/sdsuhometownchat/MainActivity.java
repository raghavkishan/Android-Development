package com.example.raghavkishan.sdsuhometownchat;
/* reference for the entore application:
1)from: https://developer.android.com
2)Class slides
*/
/*
The application begins with this activity.
 */
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    Button registerButton;
    EditText loginIdView,loginPasswordView;
    String enteredLoginId,enteredLoginPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginIdView = (EditText) findViewById(R.id.login_id_value);
        loginPasswordView = (EditText) findViewById(R.id.login_password_value);
        registerButton = (Button)findViewById(R.id.register_button);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.i("assign5", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.i("assign5", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void registerUser(View view){
        Intent go = new Intent(this,RegisterUser.class);
        startActivity(go);
    }

    public void signIn(View view){
        enteredLoginId = loginIdView.getText().toString();
        enteredLoginPassword = loginPasswordView.getText().toString();
        mAuth.signInWithEmailAndPassword(enteredLoginId,enteredLoginPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.i("assign5", "signInWithEmail:onComplete:" + task.isSuccessful());
                if(task.isSuccessful()){
                    Intent go = new Intent(getBaseContext(),FilterView.class);
                    startActivity(go);
                }
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.i("assign5", "signInWithEmail:failed", task.getException());
                    Toast.makeText(getBaseContext(), R.string.auth_failed,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
