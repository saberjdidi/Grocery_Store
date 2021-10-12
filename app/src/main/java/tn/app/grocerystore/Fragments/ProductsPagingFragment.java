package tn.app.grocerystore.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import tn.app.grocerystore.R;
import tn.app.grocerystore.activities.DetailsProductActivity;
import tn.app.grocerystore.models.ViewAllModel;

public class ProductsPagingFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    List<ViewAllModel> list;
    TextView emptyTv;
    FirestorePagingAdapter adapter;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products_paging, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.product_rec);
        emptyTv = view.findViewById(R.id.emptyTv);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loadData();

        return view;
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        //Query
        Query query = firestore.collection("AllProducts");

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        //RecyclerOptions
        FirestorePagingOptions<ViewAllModel> options = new FirestorePagingOptions.Builder<ViewAllModel>()
                .setLifecycleOwner(getActivity())
                .setQuery(query, config, new SnapshotParser<ViewAllModel>() {
                    @NonNull
                    @Override
                    public ViewAllModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        ViewAllModel model = snapshot.toObject(ViewAllModel.class);
                        String itemId = snapshot.getId();
                        model.setProductId(itemId);
                        return model;
                    }
                })
                .build();

        adapter = new FirestorePagingAdapter<ViewAllModel, ProductsViewHolder>(options) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_products, parent, false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ViewAllModel model) {
                Toast.makeText(getContext(), "Name "+model.getName(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Desc "+list.get(position).getName(), Toast.LENGTH_SHORT).show();
                Glide.with(getContext()).load(model.getImg_url()).into(holder.image);
                holder.name.setText(model.getName());
                holder.description.setText(model.getDescription());
                holder.price.setText(String.format("%d$",model.getPrice()));
                holder.rating.setText(model.getRating());
               /* holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), DetailsProductActivity.class);
                        intent.putExtra("detail", list.get(position));
                        getActivity().startActivity(intent);
                    }
                }); */
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                super.onLoadingStateChanged(state);
                switch (state){
                    case LOADING_INITIAL:
                        Log.d("PAGING LOG", "Loading initial data");
                        break;
                    case LOADING_MORE:
                        Log.d("PAGING LOG", "loading next Page");
                        break;
                    case FINISHED:
                        Log.d("PAGING LOG", "All data loaded");
                        break;
                    case ERROR:
                        Log.d("PAGING LOG", "Error loading data");
                        break;
                    case LOADED:
                        Log.d("PAGING LOG", "Total items loaded "+getItemCount());
                        break;
                }
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    private class ProductsViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, description, price, rating;
        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageIv);
            name = itemView.findViewById(R.id.nameTv);
            description = itemView.findViewById(R.id.descriptionTv);
            price = itemView.findViewById(R.id.priceTv);
            rating = itemView.findViewById(R.id.ratingTv);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}