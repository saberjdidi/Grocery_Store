package tn.app.grocerystore.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import tn.app.grocerystore.R;
import tn.app.grocerystore.adapters.ProductsAdapter;
import tn.app.grocerystore.adapters.UserAdapter;
import tn.app.grocerystore.models.User;
import tn.app.grocerystore.models.ViewAllModel;

public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView emptyTv;
    UserAdapter adapter;
    List<User> list;

    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.rec_user);
        emptyTv = view.findViewById(R.id.emptyTv);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        loadData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        return view;
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        list = new ArrayList<>();
        swipeRefreshLayout.setRefreshing(true);

        DatabaseReference reference = database.getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    //get all user except currently signed in user
                    if(!user.getEmail().equals(auth.getCurrentUser().getEmail())){
                        list.add(user);
                    }
                    adapter = new UserAdapter(getActivity(), list);
                    recyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);

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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
               Toast.makeText(getActivity(), "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}