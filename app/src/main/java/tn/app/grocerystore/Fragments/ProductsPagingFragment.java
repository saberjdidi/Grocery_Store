package tn.app.grocerystore.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tn.app.grocerystore.R;
import tn.app.grocerystore.activities.DetailsProductActivity;
import tn.app.grocerystore.adapters.ProductsAdapter;
import tn.app.grocerystore.models.ViewAllModel;

public class ProductsPagingFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    List<ViewAllModel> list;
    TextView emptyTv;
    FirestorePagingAdapter adapter;
    ProductsAdapter productsAdapter;
    SearchView searchView;

    FloatingActionMenu floatingActionMenu;
    com.github.clans.fab.FloatingActionButton fabBtnSearch;


    FirebaseAuth auth;
    FirebaseFirestore firestore;
    DocumentSnapshot lastVisible;

    LinearLayoutManager linearLayoutManager;
    boolean isScrolling;
    boolean isLastItemReached;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products_paging, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.product_rec);
        emptyTv = view.findViewById(R.id.emptyTv);
        searchView = view.findViewById(R.id.search_view);
        searchView.setVisibility(View.GONE);

        floatingActionMenu = view.findViewById(R.id.menu);
        fabBtnSearch = view.findViewById(R.id.menu_button2);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        linearLayoutManager = new LinearLayoutManager(getContext());

        loadData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        fabBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchView.getVisibility() == View.GONE){
                    searchView.setVisibility(View.VISIBLE);
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            if(!TextUtils.isEmpty(query.trim())){
                                searchData(query);
                            } else {
                                loadData();
                            }
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String query) {
                            if(!TextUtils.isEmpty(query.trim())){
                                searchData(query);
                            } else {
                                loadData();
                            }
                            return false;
                        }
                    });
                } else {
                    searchView.setVisibility(View.GONE);
                    loadData();
                }
            }
        });

        return view;
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        //Query
        // Create a reference to the products collection
        CollectionReference productsRef = firestore.collection("AllProducts");
       // Create a query against the collection.
        Query query = productsRef.orderBy("name", Query.Direction.ASCENDING).limit(5);
        //Query query = productsRef.whereEqualTo("name", "%herbes%").limit(5);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot ds : task.getResult()){
                        ViewAllModel model = ds.toObject(ViewAllModel.class);
                        list.add(model);
                    }
                    productsAdapter = new ProductsAdapter(getContext(), list);
                    recyclerView.setAdapter(productsAdapter);
                    if(list.size() == 0){
                        recyclerView.setVisibility(View.GONE);
                        emptyTv.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyTv.setVisibility(View.GONE);
                    }
                    // Get the last visible document
                    lastVisible = task.getResult().getDocuments()
                            .get(task.getResult().size() -1);
                    //Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();

                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);

                            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                                isScrolling = true;
                            }
                        }

                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                            int visibleItemCount = linearLayoutManager.getChildCount();
                            int totalItemCount = linearLayoutManager.getItemCount();

                            if(isScrolling && (firstVisibleItem + visibleItemCount == totalItemCount) && !isLastItemReached ){
                                isScrolling = false;
                                Query nextQuery = productsRef
                                        .orderBy("name", Query.Direction.ASCENDING)
                                        .startAfter(lastVisible)
                                        .limit(5);
                                nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (DocumentSnapshot ds : task.getResult()){
                                            ViewAllModel model = ds.toObject(ViewAllModel.class);
                                            list.add(model);
                                        }
                                        productsAdapter.notifyDataSetChanged();

                                        lastVisible = task.getResult().getDocuments()
                                                .get(task.getResult().size() -1);
                                        Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();

                                        if (task.getResult().size() < 5){
                                            isLastItemReached = true;
                                        }
                                    }
                                });
                            }
                        }
                    };
                    recyclerView.addOnScrollListener(onScrollListener);
                }
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    private void searchData(final String search) {
        swipeRefreshLayout.setRefreshing(true);
        list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        //Query
        // Create a reference to the products collection
        CollectionReference productsRef = firestore.collection("AllProducts");
        // Create a query against the collection.
        Query query = productsRef.orderBy("name", Query.Direction.ASCENDING).limit(5);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot ds : task.getResult()){
                        ViewAllModel model = ds.toObject(ViewAllModel.class);
                        if(model.getName().toLowerCase().contains(search.toLowerCase()) ||
                                model.getDescription().toLowerCase().contains(search.toLowerCase())){
                            list.add(model);
                        }
                    }
                    productsAdapter = new ProductsAdapter(getContext(), list);
                    recyclerView.setAdapter(productsAdapter);
                    if(list.size() == 0){
                        recyclerView.setVisibility(View.GONE);
                        emptyTv.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyTv.setVisibility(View.GONE);
                    }
                    // Get the last visible document
                    lastVisible = task.getResult().getDocuments()
                            .get(task.getResult().size() -1);
                    //Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();

                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);

                            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                                isScrolling = true;
                            }
                        }

                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                            int visibleItemCount = linearLayoutManager.getChildCount();
                            int totalItemCount = linearLayoutManager.getItemCount();

                            if(isScrolling && (firstVisibleItem + visibleItemCount == totalItemCount) && !isLastItemReached ){
                                isScrolling = false;
                                Query nextQuery = productsRef
                                        .orderBy("name", Query.Direction.ASCENDING)
                                        .startAfter(lastVisible)
                                        .limit(5);
                                nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (DocumentSnapshot ds : task.getResult()){
                                            ViewAllModel model = ds.toObject(ViewAllModel.class);
                                            if(model.getName().toLowerCase().contains(search.toLowerCase()) ||
                                                    model.getDescription().toLowerCase().contains(search.toLowerCase())){
                                                list.add(model);
                                            }
                                        }
                                        productsAdapter.notifyDataSetChanged();

                                        lastVisible = task.getResult().getDocuments()
                                                .get(task.getResult().size() -1);
                                        Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();

                                        if (task.getResult().size() < 5){
                                            isLastItemReached = true;
                                        }
                                    }
                                });
                            }
                        }
                    };
                    recyclerView.addOnScrollListener(onScrollListener);
                }
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }
}