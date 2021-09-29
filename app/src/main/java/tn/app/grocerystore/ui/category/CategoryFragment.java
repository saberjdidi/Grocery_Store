package tn.app.grocerystore.ui.category;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
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
import tn.app.grocerystore.R;
import tn.app.grocerystore.adapters.NavCategoryAdapter;
import tn.app.grocerystore.adapters.RecommendedAdapter;
import tn.app.grocerystore.models.NavCategoryModel;
import tn.app.grocerystore.models.RecommendedModel;

public class CategoryFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    List<NavCategoryModel> list;
    NavCategoryAdapter adapter;
    ProgressBar progressBar, progressBarDialog;
    FloatingActionButton fab;

    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseFirestore db;

    //add category
    Dialog dialogCategory;
    CircleImageView categoryImg;
    EditText nameEt, descriptionEt, discountEt;
    Spinner typeEt;
    Button saveBtn;

    Uri image_uri;

    String[] typeList = {"fruit", "egg", "proteine"};
    ArrayAdapter<String> adapterType;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        View root = inflater.inflate(R.layout.fragment_category, container, false);

        db = FirebaseFirestore.getInstance();

        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        recyclerView = root.findViewById(R.id.cat_rec);
        progressBar = root.findViewById(R.id.progressbar);
        fab = root.findViewById(R.id.fab);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        dialogCategory = new Dialog(getContext());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogCategory();
            }
        });

        loadData();

        return root;
    }

    private void loadData() {
        //Popular items
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        list = new ArrayList<>();
        swipeRefreshLayout.setRefreshing(true);

        db.collection("NavCategory")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                NavCategoryModel model = document.toObject(NavCategoryModel.class);
                                list.add(model);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }

                            adapter = new NavCategoryAdapter(getActivity(), list);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            Toast.makeText(getActivity(), "Error "+task.getException(), Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "Error getting documents.", task.getException());
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    private void openDialogCategory(){


        dialogCategory.setContentView(R.layout.new_category_popup);
        dialogCategory.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        categoryImg = dialogCategory.findViewById(R.id.cat_img);
        nameEt = dialogCategory.findViewById(R.id.cat_name);
        descriptionEt = dialogCategory.findViewById(R.id.cat_description);
        discountEt = dialogCategory.findViewById(R.id.cat_discount);
        typeEt = dialogCategory.findViewById(R.id.cat_type);
        saveBtn = dialogCategory.findViewById(R.id.cat_btn);
        progressBarDialog = dialogCategory.findViewById(R.id.progressbar_dialog);
        progressBarDialog.setVisibility(View.GONE);

        adapterType = new ArrayAdapter<>(getActivity()
                ,android.R.layout.simple_dropdown_item_1line, typeList);
        typeEt.setAdapter(adapterType);

        //get image from gallery
        categoryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show image pick dialog
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 33);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addCategory();
            }
        });
        dialogCategory.show();
    }

    private void addCategory() {
        String name = nameEt.getText().toString();
        String description = descriptionEt.getText().toString();
        String discount = discountEt.getText().toString();
        String type = typeEt.getSelectedItem().toString().trim();

        progressBarDialog.setVisibility(View.VISIBLE);

        if(TextUtils.isEmpty(name)){
            nameEt.setError("Name is required");
            return;
        }
        if(TextUtils.isEmpty(description)){
            descriptionEt.setError("Description is required");
            return;
        }
        if(TextUtils.isEmpty(discount)){
            discountEt.setError("Discount is required");
            return;
        }
        //Toast.makeText(getContext(), "Category "+name+" "+discount+" "+type, Toast.LENGTH_SHORT).show();
        final StorageReference reference = storage.getReference().child("categories_picture")
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
                        hashMap.put("discount", discount + "% OFF");
                        hashMap.put("type", type);

                        // Add a new document with a generated ID
                        db.collection("NavCategory")
                                .add(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("Product", "DocumentSnapshot added with ID: " + documentReference.getId());
                                        Toast.makeText(getContext(), "Category Added" , Toast.LENGTH_SHORT).show();
                                        //getActivity().onBackPressed();
                                        nameEt.setText("");
                                        descriptionEt.setText("");
                                        discountEt.setText("");

                                        list.add(new NavCategoryModel(name, description, discount+"% OFF", uri.toString(), type));
                                        adapter.notifyItemInserted(list.size());
                                        dialogCategory.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Product", "Error adding document", e);
                                        Toast.makeText(getContext(), "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        dialogCategory.dismiss();
                                        progressBarDialog.setVisibility(View.GONE);
                                    }
                                });
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null){
            image_uri = data.getData();
            categoryImg.setImageURI(image_uri);

        }
    }
}