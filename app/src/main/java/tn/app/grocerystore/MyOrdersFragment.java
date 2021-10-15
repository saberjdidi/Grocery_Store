package tn.app.grocerystore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import tn.app.grocerystore.adapters.MyCartAdapter;
import tn.app.grocerystore.adapters.OrderAdapter;
import tn.app.grocerystore.models.MyCartModel;
import tn.app.grocerystore.models.OrderModel;
import tn.app.grocerystore.models.User;

public class MyOrdersFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseDatabase database;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    OrderAdapter adapter;
    List<OrderModel> list;
    TextView emptyTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.rec_order);
        emptyTv = view.findViewById(R.id.emptyTv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();

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
        adapter = new OrderAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
        //get user
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if(user.getRole().equals("ROLE_CLIENT")){
                            db.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                                    .collection("myPayments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (DocumentSnapshot ds : task.getResult().getDocuments()){

                                            OrderModel model = ds.toObject(OrderModel.class);

                                            list.add(model);
                                            adapter.notifyDataSetChanged();
                                        }
                                        if(list.size() == 0){
                                            recyclerView.setVisibility(View.GONE);
                                            emptyTv.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            recyclerView.setVisibility(View.VISIBLE);
                                            emptyTv.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            });
                        }
                        else if(user.getRole().equals("ROLE_ADMIN")){
                            db.collection("Commandes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (DocumentSnapshot ds : task.getResult().getDocuments()){

                                            OrderModel model = ds.toObject(OrderModel.class);

                                            list.add(model);
                                            adapter.notifyDataSetChanged();
                                        }
                                        if(list.size() == 0){
                                            recyclerView.setVisibility(View.GONE);
                                            emptyTv.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            recyclerView.setVisibility(View.VISIBLE);
                                            emptyTv.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        swipeRefreshLayout.setRefreshing(false);
    }
}