package tn.app.grocerystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import tn.app.grocerystore.R;
import tn.app.grocerystore.adapters.PopularAdapters;
import tn.app.grocerystore.adapters.ViewAllAdapter;
import tn.app.grocerystore.models.PopularModel;
import tn.app.grocerystore.models.ViewAllModel;

public class ViewAllActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ViewAllAdapter adapter;
    List<ViewAllModel> list;
    Toolbar toolbar;
    ProgressBar progressBar;

    FirebaseFirestore db;

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        db = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.view_all_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        list = new ArrayList<>();

        type = getIntent().getStringExtra("type");

        getData();
    }

    private void getData() {

        adapter = new ViewAllAdapter(this, list);
        recyclerView.setAdapter(adapter);

        //getting fruit
        if(type != null && type.equalsIgnoreCase("fruit")){
            db.collection("AllProducts").whereEqualTo("type", "fruit")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    ViewAllModel model = document.toObject(ViewAllModel.class);
                                    list.add(model);
                                    adapter.notifyDataSetChanged();

                                    progressBar.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(ViewAllActivity.this, "Error "+task.getException(), Toast.LENGTH_SHORT).show();
                                Log.w("TAG", "Error getting documents.", task.getException());
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }

        //getting proteine
        if(type != null && type.equalsIgnoreCase("proteine")){
            db.collection("AllProducts").whereEqualTo("type", "proteine")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    ViewAllModel model = document.toObject(ViewAllModel.class);
                                    list.add(model);
                                    adapter.notifyDataSetChanged();

                                    progressBar.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(ViewAllActivity.this, "Error "+task.getException(), Toast.LENGTH_SHORT).show();
                                Log.w("TAG", "Error getting documents.", task.getException());
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }

        //getting egg
        if(type != null && type.equalsIgnoreCase("egg")){
            db.collection("AllProducts").whereEqualTo("type", "egg")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    ViewAllModel model = document.toObject(ViewAllModel.class);
                                    list.add(model);
                                    adapter.notifyDataSetChanged();

                                    progressBar.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(ViewAllActivity.this, "Error "+task.getException(), Toast.LENGTH_SHORT).show();
                                Log.w("TAG", "Error getting documents.", task.getException());
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }

        //getting milk
        if(type != null && type.equalsIgnoreCase("milk")){
            db.collection("AllProducts").whereEqualTo("type", "milk")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    ViewAllModel model = document.toObject(ViewAllModel.class);
                                    list.add(model);
                                    adapter.notifyDataSetChanged();

                                    progressBar.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(ViewAllActivity.this, "Error "+task.getException(), Toast.LENGTH_SHORT).show();
                                Log.w("TAG", "Error getting documents.", task.getException());
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}