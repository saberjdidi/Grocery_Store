package tn.app.grocerystore;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import tn.app.grocerystore.adapters.OfferAdapter;
import tn.app.grocerystore.models.Category;
import tn.app.grocerystore.models.Offer;
import tn.app.grocerystore.models.User;

public class OffersFragment extends Fragment {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;
    List<Offer> list;
    OfferAdapter adapter;
    ConstraintLayout emptyList;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;

    //add offer
    Dialog dialogOffer;
    CircleImageView offerImg;
    EditText nameEt, descriptionEt, discountEt, priceEt, dateDebitEt, dateFinEt;
    Button saveBtn;
    ProgressBar progressBarDialog;
    Uri image_uri;
    int year, month, day;
    ImageView dateDebitIv, dateFinIv;

    String compare_role;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offers, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        recyclerView = view.findViewById(R.id.offer_rec);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        fab = view.findViewById(R.id.fab);
        emptyList = view.findViewById(R.id.emptyList);

        loadData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
                            compare_role = user.getRole();
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

        dialogOffer = new Dialog(getContext());

        return view;
    }

    private void loadData() {
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        swipeRefreshLayout.setRefreshing(true);
        firestore.collection("Offers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Offer model = document.toObject(Offer.class);
                        try {

                            String dateNow = new SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance().getTime());
                            Date date_now = new SimpleDateFormat("MM-dd-yyyy").parse(dateNow);
                            Date date_debit = new SimpleDateFormat("MM-dd-yyyy").parse(model.getDate_debut());
                            Date date_fin = new SimpleDateFormat("MM-dd-yyyy").parse(model.getDate_fin());
                            //Toast.makeText(getActivity(), "Date now "+date_now+"\n date debit "+date_debit, Toast.LENGTH_LONG).show();
                                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                User user = snapshot.getValue(User.class);
                                                if(user.getRole().equals("ROLE_CLIENT")){
                                                    if(date_now.after(date_debit)  && date_now.before(date_fin)){
                                                        list.add(model);
                                                    }
                                                }
                                                else {
                                                    list.add(model);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                           /* if(date_now.after(date_debit)  && date_now.before(date_fin)){
                                list.add(model);
                            }*/

                            /*else if(!compare_role.equals("ROLE_CLIENT")) {
                                list.add(model);
                            } */

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //list.add(model);
                    }
                    adapter = new OfferAdapter(getActivity(), list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    Toast.makeText(getActivity(), "Error "+task.getException(), Toast.LENGTH_SHORT).show();
                    Log.w("TAG", "Error getting documents.", task.getException());
                    swipeRefreshLayout.setRefreshing(false);
                }
                if(list.size() == 0){
                    recyclerView.setVisibility(View.GONE);
                    emptyList.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyList.setVisibility(View.GONE);
                }
            }
        });
    }

    private void openDialog() {
       dialogOffer.setContentView(R.layout.new_offer_popup);
        dialogOffer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        offerImg = dialogOffer.findViewById(R.id.offer_img);
        nameEt = dialogOffer.findViewById(R.id.offer_name);
        descriptionEt = dialogOffer.findViewById(R.id.offer_description);
        discountEt = dialogOffer.findViewById(R.id.offer_discount);
        priceEt = dialogOffer.findViewById(R.id.offer_price);
        dateDebitEt = dialogOffer.findViewById(R.id.offer_date_debit);
        dateFinEt = dialogOffer.findViewById(R.id.offer_date_fin);
        dateDebitIv = dialogOffer.findViewById(R.id.dateDebitIv);
        dateFinIv = dialogOffer.findViewById(R.id.dateFinIv);
        saveBtn = dialogOffer.findViewById(R.id.offer_btn);
        progressBarDialog = dialogOffer.findViewById(R.id.progressbar_dialog);
        progressBarDialog.setVisibility(View.GONE);

        priceEt.setText("10");

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dateDebitIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                       dateDebitEt.setText(dayOfMonth+"-"+month+"-"+year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        dateFinIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        dateFinEt.setText(dayOfMonth+"-"+month+"-"+year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        offerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
               addOffer();
            }
        });
        dialogOffer.show();
    }

    private void addOffer() {
        String name = nameEt.getText().toString();
        String description = descriptionEt.getText().toString();
        String discount = discountEt.getText().toString();
        int price = Integer.parseInt(priceEt.getText().toString());
        String date_debit = dateDebitEt.getText().toString();
        String date_fin = dateFinEt.getText().toString();

        progressBarDialog.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(name)) {
            nameEt.setError("Name is required");
            progressBarDialog.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(description)) {
            descriptionEt.setError("Description is required");
            progressBarDialog.setVisibility(View.GONE);
            return;
        }
        if(TextUtils.isEmpty(discount)){
            discountEt.setError("Discount is required");
            progressBarDialog.setVisibility(View.GONE);
            return;
        }
        if(TextUtils.isEmpty(date_debit)){
            dateDebitEt.setError("Discount is required");
            progressBarDialog.setVisibility(View.GONE);
            return;
        }
        if(TextUtils.isEmpty(date_fin)){
            dateFinEt.setError("Discount is required");
            progressBarDialog.setVisibility(View.GONE);
            return;
        }
        if(image_uri == null){
            Toast.makeText(getContext(), "Image is required", Toast.LENGTH_SHORT).show();
            progressBarDialog.setVisibility(View.GONE);
            return;
        }

        final StorageReference reference = storage.getReference().child("offers_picture")
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
                        hashMap.put("discount", discount + "% OFF");
                        hashMap.put("Date_debut", date_debit);
                        hashMap.put("Date_fin", date_fin);
                        hashMap.put("img_url", uri.toString());
                        hashMap.put("price", price);

                        // Add a new document with a generated ID
                        firestore.collection("Offers")
                                .add(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("Product", "DocumentSnapshot added with ID: " + documentReference.getId());
                                        Toast.makeText(getContext(), "Offer Added" , Toast.LENGTH_SHORT).show();
                                        //getActivity().onBackPressed();
                                        nameEt.setText("");
                                        descriptionEt.setText("");
                                        discountEt.setText("");

                                        list.add(new Offer(name, description, discount+"% OFF", date_debit, date_fin, uri.toString(), price));
                                        adapter.notifyItemInserted(list.size());
                                        dialogOffer.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Product", "Error adding document", e);
                                        Toast.makeText(getContext(), "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        dialogOffer.dismiss();
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
            offerImg.setImageURI(image_uri);

        }
    }
}