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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import tn.app.grocerystore.adapters.MyCartAdapter;
import tn.app.grocerystore.adapters.OrderAdapter;
import tn.app.grocerystore.models.MyCartModel;
import tn.app.grocerystore.models.OrderModel;

public class MyOrdersFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    OrderAdapter adapter;
    List<OrderModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.rec_order);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
        db.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("myPayments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot ds : task.getResult().getDocuments()){

                        OrderModel model = ds.toObject(OrderModel.class);

                        list.add(model);
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }
}