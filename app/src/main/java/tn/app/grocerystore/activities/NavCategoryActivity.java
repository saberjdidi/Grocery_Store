package tn.app.grocerystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import tn.app.grocerystore.R;
import tn.app.grocerystore.adapters.NavCategoryDetailedAdapter;
import tn.app.grocerystore.adapters.ViewAllAdapter;
import tn.app.grocerystore.models.NavCategoryDetailedModel;
import tn.app.grocerystore.models.ViewAllModel;

public class NavCategoryActivity extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView recyclerView;
    List<NavCategoryDetailedModel> list;
    NavCategoryDetailedAdapter adapter;

    Toolbar toolbar;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_category);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.nav_cat_det_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        type = getIntent().getStringExtra("type");

        loadData();
    }

    private void loadData() {
        adapter = new NavCategoryDetailedAdapter(this, list);
        recyclerView.setAdapter(adapter);
        //getting fruit
        if(type != null && type.equalsIgnoreCase("fruit")){
            db.collection("NavCategoryDetailed").whereEqualTo("type", "fruit")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    NavCategoryDetailedModel model = document.toObject(NavCategoryDetailedModel.class);
                                    list.add(model);
                                    adapter.notifyDataSetChanged();

                                }
                            } else {
                                Toast.makeText(NavCategoryActivity.this, "Error "+task.getException(), Toast.LENGTH_SHORT).show();
                                Log.w("TAG", "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
        //getting proteine
        if(type != null && type.equalsIgnoreCase("proteine")){
            db.collection("NavCategoryDetailed").whereEqualTo("type", "proteine")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    NavCategoryDetailedModel model = document.toObject(NavCategoryDetailedModel.class);
                                    list.add(model);
                                    adapter.notifyDataSetChanged();

                                }
                            } else {
                                Toast.makeText(NavCategoryActivity.this, "Error "+task.getException(), Toast.LENGTH_SHORT).show();
                                Log.w("TAG", "Error getting documents.", task.getException());
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