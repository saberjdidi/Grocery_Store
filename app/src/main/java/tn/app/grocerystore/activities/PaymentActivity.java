package tn.app.grocerystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.PaymentResultListener;
import com.razorpay.Checkout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tn.app.grocerystore.R;
import tn.app.grocerystore.models.MyCartModel;
import tn.app.grocerystore.models.OrderModel;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    Toolbar toolbar;
    TextView totalAmount, totalPrice, totalQuantity, name;
    Button paymentBtn;
    ProgressBar progressbar;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    String product_name;
    int price = 0;
    int quantity = 0;
    double total_amount = 0.0;
    JSONObject options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        toolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        totalAmount = findViewById(R.id.total_amount);
        totalPrice = findViewById(R.id.price);
        totalQuantity = findViewById(R.id.quantity);
        name = findViewById(R.id.name);
        paymentBtn = findViewById(R.id.pay_btn);
        progressbar = findViewById(R.id.progressbar);
        progressbar.setVisibility(View.GONE);

        List<MyCartModel> list = (ArrayList<MyCartModel>) getIntent().getSerializableExtra("itemList");
        if(list != null && list.size() > 0) {

            for (MyCartModel model : list) {
                    price += model.getTotalPrice();
                int qty = Integer.parseInt(model.getTotalQuantity());
                    quantity += qty;
                total_amount += model.getTotalPrice();
            }

            totalPrice.setText(String.format("%d$",price));
            totalQuantity.setText(String.valueOf(quantity));
            totalAmount.setText(String.valueOf(total_amount));

            product_name = list.get(0).getProductName();
            name.setText(product_name);
        }

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMethod();
            }
        });

    }

    private void paymentMethod() {
        progressbar.setVisibility(View.VISIBLE);
        // initialize Razorpay account.
        Checkout checkout = new Checkout();
        // set your id as below
        checkout.setKeyID("rzp_test_ZUlTzA2L1CtPVr");
        // set image
        checkout.setImage(R.mipmap.regbg);

        //You need to pass current activity in order to let Razorpay create CheckoutActivity
        final Activity activity = PaymentActivity.this;

        try {
            options = new JSONObject();
            options.put("name", product_name);
            options.put("description", "Reference Num. #123654");
            options.put("currency", "USD");
            //options.put("quantity", quantity);
            options.put("amount", total_amount);
            //Toast.makeText(this, total_amount+"$", Toast.LENGTH_SHORT).show();

            // open razorpay to checkout activity
            checkout.open(activity, options);
            //progressbar.setVisibility(View.GONE);

        } catch (Exception e){
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onPaymentSuccess(String s) {
        progressbar.setVisibility(View.GONE);
        Toast.makeText(this, "Payment successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        progressbar.setVisibility(View.GONE);
        String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", options.getString("name"));
            hashMap.put("description", options.getString("description"));
            hashMap.put("currency", options.getString("currency"));
            hashMap.put("amount", options.getString("amount"));
            hashMap.put("currentDate", saveCurrentDate);
            hashMap.put("currentTime", saveCurrentTime);
            //Toast.makeText(this, "Payment  : "+hashMap, Toast.LENGTH_LONG).show();

            //add data to firestore
            firestore.collection("Orders").add(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(@NonNull DocumentReference documentReference) {
                            Toast.makeText(PaymentActivity.this, "Payment successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PaymentActivity.this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}