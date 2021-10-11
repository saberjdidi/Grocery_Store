package tn.app.grocerystore.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tn.app.grocerystore.MainActivity;
import tn.app.grocerystore.R;
import tn.app.grocerystore.adapters.ContactAdapter;
import tn.app.grocerystore.models.Contact;
import tn.app.grocerystore.models.User;

public class ContactFragment extends Fragment {

    EditText nameEt, emailEt, phoneEt, messageEt;
    Button sendBtn;
    ConstraintLayout constraintLayout1, constraintLayout2;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    TextView emptyTv;
    List<Contact> list;
    ContactAdapter adapter;

    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        nameEt = view.findViewById(R.id.contact_name);
        emailEt = view.findViewById(R.id.contact_email);
        phoneEt = view.findViewById(R.id.contact_phone);
        messageEt = view.findViewById(R.id.contact_message);
        sendBtn = view.findViewById(R.id.sendBtn);
        constraintLayout1 = view.findViewById(R.id.constraint1);
        constraintLayout2 = view.findViewById(R.id.constraint2);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        emptyTv = view.findViewById(R.id.emptyTv);

        emailEt.setFocusable(false);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //get data from database of current user
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if(user.getRole().equals("ROLE_CLIENT")){
                            constraintLayout1.setVisibility(View.VISIBLE);
                            constraintLayout2.setVisibility(View.GONE);
                        }
                        else if(user.getRole().equals("ROLE_ADMIN")){
                            constraintLayout1.setVisibility(View.GONE);
                            constraintLayout2.setVisibility(View.VISIBLE);
                        }
                        nameEt.setText(user.getName());
                        emailEt.setText(user.getEmail());
                        phoneEt.setText(user.getNumber());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendContact();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        loadData();

        return view;
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        firestore.collection("Contacts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 if(task.isSuccessful()){
                   for (DocumentSnapshot ds : task.getResult().getDocuments()){
                       Contact model = ds.toObject(Contact.class);
                       list.add(model);
                       adapter = new ContactAdapter(getContext(), list);
                       recyclerView.setAdapter(adapter);
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
                 else {
                     Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                     swipeRefreshLayout.setRefreshing(false);
                 }
            }
        });
    }

    private void sendContact() {
        String name = nameEt.getText().toString();
        String email = emailEt.getText().toString();
        String phone = phoneEt.getText().toString();
        String message = messageEt.getText().toString();

        if(message.isEmpty()){
            messageEt.setError("Message is required");
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("phone", phone);
        map.put("message", message);
        firestore.collection("Contacts").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Contact added successfully", Toast.LENGTH_SHORT).show();
                    messageEt.setText("");
                }
                else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}