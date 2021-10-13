package tn.app.grocerystore.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import tn.app.grocerystore.R;
import tn.app.grocerystore.models.User;

public class NewAddressFragment extends Fragment {

    EditText name, address, city, postalCode, phoneNumber;
    Button addAddressBtn;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_address, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        name = view.findViewById(R.id.ad_name);
        address = view.findViewById(R.id.ad_address);
        city = view.findViewById(R.id.ad_city);
        postalCode = view.findViewById(R.id.ad_code);
        phoneNumber = view.findViewById(R.id.ad_phone);
        addAddressBtn = view.findViewById(R.id.add_address_btn);

        //get phone number from database
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        phoneNumber.setText(user.getNumber());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newAddress();
            }
        });

        return view;
    }

    private void newAddress(){
        String userName = name.getText().toString();
        String userAddress = address.getText().toString();
        String userCity = city.getText().toString();
        String userPostalCode = postalCode.getText().toString();
        String userPhoneNumber = phoneNumber.getText().toString();

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

            firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                    .collection("Address").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getActivity(), "Address added successfully", Toast.LENGTH_SHORT).show();
                        name.setText("");
                        address.setText("");
                        city.setText("");
                        postalCode.setText("");
                        phoneNumber.setText("");
                    }
                }
            });
        }
        else {
            Toast.makeText(getActivity(), "Kindly Fill All Field", Toast.LENGTH_SHORT).show();
        }
    }
}