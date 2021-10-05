package tn.app.grocerystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import tn.app.grocerystore.models.User;

public class LoginActivity extends AppCompatActivity {

    Button signIn;
    EditText email, password;
    TextView signUp;
    CircleImageView fab;
    ProgressBar progressBar;

    FirebaseAuth auth;

    private String localeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        signIn = findViewById(R.id.login_btn);
        signUp = findViewById(R.id.sign_up);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
                progressBar.setVisibility(View.VISIBLE);
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
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if(TextUtils.isEmpty(userEmail)){
            email.setError("Email is required");
            return;
        }
        if(TextUtils.isEmpty(userPassword)){
            password.setError("Password is required");
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
}