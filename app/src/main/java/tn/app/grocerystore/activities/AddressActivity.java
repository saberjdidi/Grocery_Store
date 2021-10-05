package tn.app.grocerystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tn.app.grocerystore.Fragments.NewAddressFragment;
import tn.app.grocerystore.R;
import tn.app.grocerystore.adapters.AddressAdapter;
import tn.app.grocerystore.adapters.MyCartAdapter;
import tn.app.grocerystore.models.Address;
import tn.app.grocerystore.models.Category;
import tn.app.grocerystore.models.MyCartModel;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.SelectedAddress {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;
    TextView emptyTv;
    Toolbar toolbar;
    Button continuePayment;
    List<Address> list;
    AddressAdapter adapter;

    FirebaseAuth auth;
    FirebaseFirestore db;

    String mAddress = "";
    //add address
    Dialog dialogAddress;
    EditText nameEt, addressEt, cityEt, postalCodeEt, phoneNumberEt;
    Button addAddressBtn;
    ProgressBar progressBarDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.address_rec);
        emptyTv = findViewById(R.id.emptyTv);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        continuePayment = findViewById(R.id.buy_now);
        fab = findViewById(R.id.fab);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        dialogAddress = new Dialog(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        continuePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAddress == ""){
                    Toast.makeText(AddressActivity.this, "Please Add your Address to continue the payment", Toast.LENGTH_SHORT).show();
                }
                else {
                    List<MyCartModel> listCart = (ArrayList<MyCartModel>) getIntent().getSerializableExtra("itemList");
                    Intent intent = new Intent(AddressActivity.this, PaymentActivity.class);
                    intent.putExtra("listCart", (Serializable) listCart);
                    intent.putExtra("address", mAddress);
                    startActivity(intent);
                }
            }
        });

        loadData();
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list = new ArrayList<>();
        adapter = new AddressAdapter(getApplicationContext(),list, this::setAddress);
        recyclerView.setAdapter(adapter);
        db.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("Address").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot ds : task.getResult().getDocuments()){

                        Address model = ds.toObject(Address.class);

                        list.add(model);
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if(list.size() == 0){
                        recyclerView.setVisibility(View.GONE);
                        emptyTv.setVisibility(View.VISIBLE);
                        continuePayment.setVisibility(View.GONE);
                        fab.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyTv.setVisibility(View.GONE);
                        continuePayment.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.GONE);
                    }
                }
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    private void openDialog(){


        dialogAddress.setContentView(R.layout.new_address_popup);
        dialogAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        nameEt = dialogAddress.findViewById(R.id.ad_name);
        addressEt = dialogAddress.findViewById(R.id.ad_address);
        cityEt = dialogAddress.findViewById(R.id.ad_city);
        postalCodeEt = dialogAddress.findViewById(R.id.ad_code);
        phoneNumberEt = dialogAddress.findViewById(R.id.ad_phone);
        addAddressBtn = dialogAddress.findViewById(R.id.add_address_btn);
        progressBarDialog = dialogAddress.findViewById(R.id.progressbar_dialog);
        progressBarDialog.setVisibility(View.GONE);

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addAddress();
            }
        });
        dialogAddress.show();
    }

    private void addAddress() {
        progressBarDialog.setVisibility(View.VISIBLE);
        String userName = nameEt.getText().toString();
        String userAddress = addressEt.getText().toString();
        String userCity = cityEt.getText().toString();
        String userPostalCode = postalCodeEt.getText().toString();
        String userPhoneNumber = phoneNumberEt.getText().toString();

        String final_address = "";
        if(!userName.isEmpty()){
            final_address += userName + " ";
        }
        if(!userAddress.isEmpty()){
            final_address += userAddress + " ";
        }
        if(!userCity.isEmpty()){
            final_address += userCity + " ";
        }
        if(!userPostalCode.isEmpty()){
            final_address += userPostalCode + " ";
        }
        if(!userPhoneNumber.isEmpty()){
            final_address += userPhoneNumber;
        }
        if(!userName.isEmpty() && !userAddress.isEmpty() && !userAddress.isEmpty() && !userCity.isEmpty() && !userPostalCode.isEmpty() && !userPhoneNumber.isEmpty()){
            Map<String, String> map = new HashMap<>();
            map.put("userAddress", final_address);

            String final_address1 = final_address;
            db.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                    .collection("Address").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(@NonNull DocumentReference documentReference) {
                        Toast.makeText(AddressActivity.this, "Address added successfully", Toast.LENGTH_SHORT).show();
                        list.add(new Address(final_address1));
                        adapter.notifyItemInserted(list.size());
                        dialogAddress.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddressActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    dialogAddress.dismiss();
                }
            });
        }
        else {
            Toast.makeText(AddressActivity.this, "Kindly Fill All Field", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setAddress(String address) {
       mAddress = address;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}