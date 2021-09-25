package tn.app.grocerystore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import tn.app.grocerystore.adapters.MyCartAdapter;
import tn.app.grocerystore.models.MyCartModel;

public class MyCartsFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;

    RecyclerView recyclerView;
    MyCartAdapter adapter;
    List<MyCartModel> list;
    TextView overTotalAmount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_carts, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        overTotalAmount = view.findViewById(R.id.total_amount);
        //pass data from adapter to fragment
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("MyTotalAmount"));

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadData();

        return view;
    }

    private void loadData() {
        list = new ArrayList<>();
        adapter = new MyCartAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
      db.collection("AddToCart").document(auth.getCurrentUser().getUid())
              .collection("CurrentUser").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()){
                for (DocumentSnapshot ds : task.getResult().getDocuments()){
                    MyCartModel model = ds.toObject(MyCartModel.class);
                    list.add(model);
                    adapter.notifyDataSetChanged();
                }
            }
          }
      });
    }

    //pass data from adapter to fragment
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          int totalBill = intent.getIntExtra("totalAmount", 0);
          overTotalAmount.setText("Total Bill : "+totalBill+"$");
        }
    };
}