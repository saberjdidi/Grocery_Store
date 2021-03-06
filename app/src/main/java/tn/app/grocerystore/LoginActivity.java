package tn.app.grocerystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import tn.app.grocerystore.activities.RegistrationActivity;

public class LoginActivity extends AppCompatActivity {

    Button signIn;
    EditText email, password;
    TextView signUp, forgetPasswordEt;
    CircleImageView fab;
    ProgressBar progressBar;
    ProgressDialog progressDialog;

    FirebaseAuth auth;

    private String localeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!isConnected(this)) {
            showInternetDialog();
        }

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        signIn = findViewById(R.id.login_btn);
        signUp = findViewById(R.id.sign_up);
        forgetPasswordEt = findViewById(R.id.forget_password);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
        //forgot password
        forgetPasswordEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showrecoverPasswordDialog();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        //multi-langage
        fab = findViewById(R.id.fab);
        setFlag();
        Locale locale = getResources().getConfiguration().locale;
        localeString = locale.toString().substring(0, 2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localeString.equals("en")) {
                    setLocale("fr");
                }
                else {
                    setLocale("en");
                }

            }
        });
    }

    private void loginUser() {
        progressBar.setVisibility(View.VISIBLE);
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if(TextUtils.isEmpty(userEmail)){
            progressBar.setVisibility(View.GONE);
            email.setError("Email is required");
            email.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(userPassword)){
            progressBar.setVisibility(View.GONE);
            password.setError("Password is required");
            password.requestFocus();
            return;
        }
        //login user
        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Error "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //method for forget password
    private void showrecoverPasswordDialog() {
        //alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Recover Password");

        //set layout linear layout
        LinearLayout linearLayout = new LinearLayout(this);
        //views to set in dialog
        final EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        //set the min width of EditView
        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10, 10, 10, 10);

        dialog.setView(linearLayout);


        //button recover
        dialog.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input email
                String email = emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });
        //button cancel
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //show dialog
        dialog.create().show();
    }

    //forget password
    private void beginRecovery(String email) {
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Email sent",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //multi langage
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        RestartActivity();
    }

    private void setFlag() {
        Locale locale = getResources().getConfiguration().locale;
        String lang = locale.toString().substring(0, 2);
        if (lang.equals("en")) {
            lang = "fr";
        } else if (lang.equals("fr")) {
            lang = "en";
        }
        String iconId = "ic_flag_" + lang;
        fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), getResources().getIdentifier(iconId, "drawable", getPackageName())));

    }
    private void RestartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
    //check internet connection
    private void showInternetDialog() {

        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        View view = LayoutInflater.from(this).inflate(R.layout.no_internet_dialog, findViewById(R.id.no_internet_layout));
        view.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected(LoginActivity.this)) {
                    showInternetDialog();
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        });

        dialog.setContentView(view);
        dialog.show();

    }

    private boolean isConnected(LoginActivity loginActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) loginActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }
}