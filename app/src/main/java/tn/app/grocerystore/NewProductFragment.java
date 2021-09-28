package tn.app.grocerystore;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import tn.app.grocerystore.ui.home.HomeFragment;

public class NewProductFragment extends Fragment {

    CircleImageView productImg;
    EditText nameEt, descriptionEt, ratingEt, priceEt;
    Spinner typeEt;
    Button saveBtn;

    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseFirestore db;

    Uri image_uri;

    String[] typeList = {"fruit", "egg", "proteine"};
    ArrayAdapter<String> adapterType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_product, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        productImg = view.findViewById(R.id.poduct_img);
        nameEt = view.findViewById(R.id.poduct_name);
        descriptionEt = view.findViewById(R.id.poduct_description);
        ratingEt = view.findViewById(R.id.product_rating);
        typeEt = view.findViewById(R.id.product_type);
        priceEt = view.findViewById(R.id.product_price);
        saveBtn = view.findViewById(R.id.product_btn);

        priceEt.setText("10");

        adapterType = new ArrayAdapter<>(getActivity()
                ,android.R.layout.simple_dropdown_item_1line, typeList);
        typeEt.setAdapter(adapterType);

        //get image from camera/gallery
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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addProduct();
            }
        });

        return view;
    }

    private void addProduct() {
        String name = nameEt.getText().toString();
        String description = descriptionEt.getText().toString();
        String rating = ratingEt.getText().toString();
        String type = typeEt.getSelectedItem().toString().trim();
        int price = Integer.parseInt(priceEt.getText().toString());

        if(TextUtils.isEmpty(name)){
            nameEt.setError("Name is required");
            return;
        }
        if(TextUtils.isEmpty(description)){
            descriptionEt.setError("Description is required");
            return;
        }
        if(TextUtils.isEmpty(rating)){
            ratingEt.setError("Rating is required");
            return;
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("description", description);
        hashMap.put("img_url", image_uri.toString());
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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Product", "Error adding document", e);
                        Toast.makeText(getContext(), "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null){
            image_uri = data.getData();
            productImg.setImageURI(image_uri);

            final StorageReference reference = storage.getReference().child("products_picture")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            image_uri = uri;
                        }
                    });
                }
            });
        }
    }
}