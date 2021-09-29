package tn.app.grocerystore.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import tn.app.grocerystore.R;
import tn.app.grocerystore.models.MyCartModel;

public class PaymentActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView totalAmount, totalPrice, totalQuantity;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

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

        String total_amount = getIntent().getStringExtra("totalAmount");
        List<MyCartModel> list = (ArrayList<MyCartModel>) getIntent().getSerializableExtra("itemList");
        if(list != null && list.size() > 0) {
            int price = 0;
            int quantity = 0;
            for (MyCartModel model : list) {
                    price += model.getTotalPrice();
                int foo = Integer.parseInt(model.getTotalQuantity());
                    quantity += foo;
                Toast.makeText(PaymentActivity.this, String.valueOf(foo), Toast.LENGTH_SHORT).show();
            }
            totalPrice.setText(String.format("%d$",price));
            totalQuantity.setText(String.valueOf(quantity));
            totalAmount.setText(total_amount);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}