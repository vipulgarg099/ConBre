package com.conbre.vipul.conbre;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUp_Activity extends ActionBarActivity {


    private static final String TAG = SignUp_Activity.class.getSimpleName() ;
    protected EditText mUsername;
    protected EditText mEmail;
    protected EditText mPassword;
    protected EditText mConfirmPassword;
    protected Button mSignUpButton;
    protected ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_);

        mUsername = (EditText)findViewById(R.id.signUp_username);
        mEmail = (EditText) findViewById(R.id.signUp_email);
        mPassword = (EditText) findViewById(R.id.signUp_password);
        mConfirmPassword = (EditText) findViewById(R.id.signUp_confirmPassword);
        mSignUpButton = (Button) findViewById(R.id.signUp_button);
        mProgressBar = (ProgressBar) findViewById(R.id.signUp_progressBar);

        mProgressBar.setVisibility(View.INVISIBLE);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String confirmPassword = mConfirmPassword.getText().toString();
                String email = mEmail.getText().toString();

                username = username.trim();
                password = password.trim();
                confirmPassword = confirmPassword.trim();
                email = email.trim();


                if(!password.equals(confirmPassword)) {

                    //Set Message that password is not equal
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp_Activity.this);
                    builder.setMessage(getString(R.string.signup_incorrect_password_message));
                    builder.setTitle(getString(R.string.incorrect_password_title));
                    builder.setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    }
                else if(username.isEmpty() || email.isEmpty() || password.isEmpty()){

                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp_Activity.this);
                    builder.setMessage(getString(R.string.signUp_error_message));
                    builder.setTitle(getString(R.string.SignUp_error_tittle));
                    builder.setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    //Create new user
                    togglerefresh();
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {



                            if(e==null){
                                //success!
                                togglerefresh();
                                Intent intent = new Intent(SignUp_Activity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else{
                                togglerefresh();
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp_Activity.this);
                                builder.setMessage(e.getMessage());
                                builder.setTitle(getString(R.string.SignUp_error_tittle));
                                builder.setPositiveButton(android.R.string.ok, null);

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        }
                    });


                }
            }
        });
    }

    private void togglerefresh() {

        if(mProgressBar.getVisibility()==View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mSignUpButton.setVisibility(View.INVISIBLE);
        }
        else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mSignUpButton.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
