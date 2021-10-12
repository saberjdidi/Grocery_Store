package tn.app.grocerystore.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tn.app.grocerystore.R;
import tn.app.grocerystore.adapters.AddressListAdapter;
import tn.app.grocerystore.models.Address;
import tn.app.grocerystore.models.User;

public class AddressFragment extends Fragment {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;
    TextView emptyTv;
    List<Address> list;
    AddressListAdapter adapter;

    FirebaseAuth auth;
    FirebaseFirestore db;

    //add address
    Dialog dialogAddress;
    EditText nameEt, addressEt, cityEt, postalCodeEt, phoneNumberEt;
    Button addAddressBtn;
    ProgressBar progressBarDialog;
    List<String> uid;
    String user_uid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_address, container, false);

        recyclerView = view.findViewById(R.id.address_rec);
        emptyTv = view.findViewById(R.id.emptyTv);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        fab = view.findViewById(R.id.fab);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        dialogAddress = new Dialog(getContext());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        loadData();

        return view;
    }
    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<>();
        adapter = new AddressListAdapter(list, getActivity());
        recyclerView.setAdapter(adapter);
        //get users
        uid = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    //get all user except currently signed in user
                    uid.add(user.getUid());
                    for(String str : uid){
                        //Toast.makeText(getActivity(), "Users : "+user.getUid(), Toast.LENGTH_LONG).show();
                        db.collection("CurrentUser").document(user_uid )
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
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                    else {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        emptyTv.setVisibility(View.GONE);
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
                    Toast.makeText(getContext(), "Address added successfully", Toast.LENGTH_SHORT).show();
                    list.add(new Address(final_address1));
                    adapter.notifyItemInserted(list.size());
                    dialogAddress.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    dialogAddress.dismiss();
                }
            });
        }
        else {
            Toast.makeText(getActivity(), "Kindly Fill All Field", Toast.LENGTH_SHORT).show();
        }
    }

}