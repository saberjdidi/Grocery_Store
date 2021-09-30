package tn.app.grocerystore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tn.app.grocerystore.activities.PaymentActivity;
import tn.app.grocerystore.activities.PlacedOrderActivity;
import tn.app.grocerystore.adapters.MyCartAdapter;
import tn.app.grocerystore.models.MyCartModel;

public class MyCartsFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    MyCartAdapter adapter;
    List<MyCartModel> list;
    TextView overTotalAmount;
    ProgressBar progressBar;

    AppCompatButton buyNow;
    int totalBill;
    double totalAmount = 0.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_carts, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        overTotalAmount = view.findViewById(R.id.total_amount);
        //pass data from adapter to fragment
        ///LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("MyTotalAmount"));

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        buyNow = view.findViewById(R.id.buy_now);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        loadData();

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getContext(), PlacedOrderActivity.class);
                Intent intent = new Intent(getContext(), PaymentActivity.class);
                intent.putExtra("itemList", (Serializable) list);
                //intent.putExtra("totalAmount", totalAmount);
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        list = new ArrayList<>();
        adapter = new MyCartAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
      db.collection("CurrentUser").document(auth.getCurrentUser().getUid())
              .collection("AddToCart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()){
                for (DocumentSnapshot ds : task.getResult().getDocuments()){

                    String documentId = ds.getId();

                    MyCartModel model = ds.toObject(MyCartModel.class);
                    model.setDocumentId(documentId);

                    list.add(model);
                    adapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
                calculTotalAmount(list);
            }
          }
      });
        swipeRefreshLayout.setRefreshing(false);
    }

    private void calculTotalAmount(List<MyCartModel> list) {

        for(MyCartModel model : list){
            totalAmount += model.getTotalPrice();
        }
        overTotalAmount.setText("Total Amount : "+totalAmount+"$");

    }

    //pass data from adapter to fragment
   /* public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          totalBill = intent.getIntExtra("totalAmount", 0);
          overTotalAmount.setText("Total Bill : "+totalBill+"$");
        }
    };*/
}