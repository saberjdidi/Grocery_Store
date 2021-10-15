package tn.app.grocerystore.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import tn.app.grocerystore.MyDialog;
import tn.app.grocerystore.R;
import tn.app.grocerystore.adapters.ListCategoryAdapter;
import tn.app.grocerystore.adapters.ProductsAdapter;
import tn.app.grocerystore.models.Category;
import tn.app.grocerystore.models.User;
import tn.app.grocerystore.models.ViewAllModel;

public class ProductFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView, recyclerView_cat;
    List<ViewAllModel> list;
    List<Category> listCategory;
    ListCategoryAdapter adapterCategory;
    ProductsAdapter adapter;
    FloatingActionButton fab;
    ProgressBar progressBarDialog;
    TextView emptyTv;
    SearchView searchView;

    FloatingActionMenu floatingActionMenu;
    com.github.clans.fab.FloatingActionButton fabBtnAdd, fabBtnSearch;

    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseDatabase database;

    //add product
    Dialog dialogProduct, dialogSearch;
    MyDialog dialogListCategory;
    CircleImageView productImg;
    EditText nameEt, descriptionEt, ratingEt, priceEt, cat_search;
    Spinner typeEt;
    Button saveBtn, searchBtn;

    Uri image_uri;
    String[] typeList = {"fruit", "egg", "proteine"};
    ArrayAdapter<String> adapterType;
    List<String> category;
    String nameCat;

    //search product
    EditText search_nameEt, search_descriptionEt;
    Button search_dialog_btn;
    String nameSearch = "";
    String descriptionSearch = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.product_rec);
        fab = view.findViewById(R.id.fab);
        emptyTv = view.findViewById(R.id.emptyTv);
        searchView = view.findViewById(R.id.search_view);
        searchView.setVisibility(View.GONE);

        floatingActionMenu = view.findViewById(R.id.menu);
        fabBtnAdd = view.findViewById(R.id.menu_button1);
        fabBtnSearch = view.findViewById(R.id.menu_button2);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();

        dialogProduct = new Dialog(getContext());
        dialogSearch = new Dialog(getContext());
        dialogListCategory = new MyDialog(getContext());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchView.setVisibility(View.GONE);
                loadData();
            }
        });

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if(user.getRole().equals("ROLE_CLIENT")){
                            fab.setVisibility(View.GONE);
                            fabBtnAdd.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        fabBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        fabBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogSearch();
               /* if(searchView.getVisibility() == View.GONE){
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
                } */
            }
        });

        loadData();

        return view;
    }

    private void loadData() {
        //Popular items
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        list = new ArrayList<>();
        swipeRefreshLayout.setRefreshing(true);

        db.collection("AllProducts").orderBy("name")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String productId = document.getId();
                                ViewAllModel model = document.toObject(ViewAllModel.class);
                                model.setProductId(productId);
                                list.add(model);
                            }

                            adapter = new ProductsAdapter(getActivity(), list);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            Toast.makeText(getActivity(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "Error getting documents.", task.getException());
                            swipeRefreshLayout.setRefreshing(false);
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
                });
    }


    private void searchData(String query_name, String query_description) {
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        list = new ArrayList<>();

        db.collection("AllProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String productId = document.getId();
                                ViewAllModel model = document.toObject(ViewAllModel.class);
                                model.setProductId(productId);

                                if(model.getName().toLowerCase().contains(query_name.toLowerCase()) ||
                                        model.getDescription().toLowerCase().contains(query_description.toLowerCase())){
                                    list.add(model);
                                }

                            }

                            adapter = new ProductsAdapter(getActivity(), list);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            Toast.makeText(getActivity(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "Error getting documents.", task.getException());
                            swipeRefreshLayout.setRefreshing(false);
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
                });
    }
    private void searchName(String query_name) {
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        list = new ArrayList<>();

        db.collection("AllProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String productId = document.getId();
                                ViewAllModel model = document.toObject(ViewAllModel.class);
                                model.setProductId(productId);

                                if(model.getName().toLowerCase().contains(query_name.toLowerCase())){
                                    list.add(model);
                                }

                            }

                            adapter = new ProductsAdapter(getActivity(), list);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            Toast.makeText(getActivity(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "Error getting documents.", task.getException());
                            swipeRefreshLayout.setRefreshing(false);
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
                });
    }
    private void searchDescription(String query_description) {
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        list = new ArrayList<>();

        db.collection("AllProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String productId = document.getId();
                                ViewAllModel model = document.toObject(ViewAllModel.class);
                                model.setProductId(productId);

                                if(model.getDescription().toLowerCase().contains(query_description.toLowerCase())){
                                    list.add(model);
                                }

                            }

                            adapter = new ProductsAdapter(getActivity(), list);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            Toast.makeText(getActivity(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "Error getting documents.", task.getException());
                            swipeRefreshLayout.setRefreshing(false);
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
                });
    }

    private void openDialog() {


        dialogProduct.setContentView(R.layout.new_product_popup);
        dialogProduct.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        productImg = dialogProduct.findViewById(R.id.poduct_img);
        nameEt = dialogProduct.findViewById(R.id.poduct_name);
        descriptionEt = dialogProduct.findViewById(R.id.poduct_description);
        ratingEt = dialogProduct.findViewById(R.id.product_rating);
        typeEt = dialogProduct.findViewById(R.id.product_type);
        priceEt = dialogProduct.findViewById(R.id.product_price);
        cat_search = dialogProduct.findViewById(R.id.cat_search);
        searchBtn = dialogProduct.findViewById(R.id.searchBtn);
        saveBtn = dialogProduct.findViewById(R.id.product_btn);
        progressBarDialog = dialogProduct.findViewById(R.id.progressbar_dialog);
        progressBarDialog.setVisibility(View.GONE);

        priceEt.setText("10");

        adapterType = new ArrayAdapter<>(getActivity()
                , android.R.layout.simple_dropdown_item_1line, typeList);
        typeEt.setAdapter(adapterType);

        //get image from gallery
        productImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show image pick dialog
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 33);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogCategory();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addProduct();
            }
        });
        dialogProduct.show();
    }

    private void openDialogCategory() {
        dialogListCategory.setContentView(R.layout.dialog_category_list_popup);
        dialogListCategory.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        recyclerView_cat = dialogListCategory.findViewById(R.id.cat_recyclerview);
        getListCategories();
        //pass data in dialog
        dialogListCategory.setOnDismissListener((d) -> {
            Category c = listCategory.get(dialogListCategory.getValeur());
            nameCat = c.getName();
            //Toast.makeText(ProductFragment.this.getContext(), c.getName(), Toast.LENGTH_LONG).show();
            cat_search.setText(nameCat);
        });

    }

    private void openDialogSearch() {


        dialogSearch.setContentView(R.layout.search_popup);
        dialogSearch.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        search_nameEt = dialogSearch.findViewById(R.id.search_name);
        search_descriptionEt = dialogSearch.findViewById(R.id.search_description);
        search_dialog_btn = dialogSearch.findViewById(R.id.search_btn);

        search_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameSearch = search_nameEt.getText().toString();
                descriptionSearch = search_descriptionEt.getText().toString();
                dialogSearch.dismiss();

                if(!TextUtils.isEmpty(nameSearch) && !TextUtils.isEmpty(descriptionSearch)){
                    searchData(nameSearch, descriptionSearch);
                }
                else if(!TextUtils.isEmpty(nameSearch) && TextUtils.isEmpty(descriptionSearch)){
                    searchName(nameSearch);
                }
                else if(TextUtils.isEmpty(nameSearch) && !TextUtils.isEmpty(descriptionSearch)){
                    searchDescription(descriptionSearch);
                }
                else {
                    loadData();
                }
                Toast.makeText(getContext(), nameSearch, Toast.LENGTH_SHORT).show();
            }
        });
        dialogSearch.show();
    }

    private void addProduct() {
        progressBarDialog.setVisibility(View.VISIBLE);
        String name = nameEt.getText().toString();
        String description = descriptionEt.getText().toString();
        String rating = ratingEt.getText().toString();
        //String type = typeEt.getSelectedItem().toString().trim();
        String type = cat_search.getText().toString();
        int price = Integer.parseInt(priceEt.getText().toString());

        if (TextUtils.isEmpty(name)) {
            nameEt.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(description)) {
            descriptionEt.setError("Description is required");
            return;
        }
        if (TextUtils.isEmpty(rating)) {
            ratingEt.setError("Rating is required");
            return;
        }

        final StorageReference reference = storage.getReference().child("products_picture")
                .child(FirebaseAuth.getInstance().getUid());

        reference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("name", name);
                        hashMap.put("description", description);
                        hashMap.put("img_url", uri.toString());
                        hashMap.put("rating", rating);
                        hashMap.put("type", type);
                        hashMap.put("price", price);

                        // Add a new document with a generated ID
                        db.collection("AllProducts")
                                .add(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("Product", "DocumentSnapshot added with ID: " + documentReference.getId());
                                        Toast.makeText(getContext(), "Product Added", Toast.LENGTH_SHORT).show();
                                        //getActivity().onBackPressed();
                                        nameEt.setText("");
                                        descriptionEt.setText("");
                                        ratingEt.setText("");
                                        priceEt.setText("");
                                        list.add(new ViewAllModel(name, description, rating, type, uri.toString(), price));
                                        adapter.notifyItemInserted(list.size());
                                        dialogProduct.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Product", "Error adding document", e);
                                        Toast.makeText(getContext(), "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        dialogProduct.dismiss();
                                        progressBarDialog.setVisibility(View.GONE);
                                    }
                                });
                    }
                });
            }
        });
    }

    private void getListCategories() {
        //Popular items
        recyclerView_cat.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        listCategory = new ArrayList<>();

        db.collection("NavCategory")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Category model = document.toObject(Category.class);
                                listCategory.add(model);
                            }

                            adapterCategory = new ListCategoryAdapter(getActivity(), listCategory, dialogListCategory);
                            recyclerView_cat.setAdapter(adapterCategory);
                            adapterCategory.notifyDataSetChanged();
                            dialogListCategory.show();
                        } else {
                            Toast.makeText(getActivity(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data.getData() != null) {
            image_uri = data.getData();
            productImg.setImageURI(image_uri);

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); //to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //inflater menu
        inflater.inflate(R.menu.main, menu);
        //menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_logout).setVisible(false);
        //searchView
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //called when user press search button from keybord
                if(!TextUtils.isEmpty(query.trim())){
                    ///searchData(query);
                } else {
                    loadData();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //called when user change text in searchView
                if(!TextUtils.isEmpty(query.trim())){
                    ///searchData(query);
                } else {
                    loadData();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}