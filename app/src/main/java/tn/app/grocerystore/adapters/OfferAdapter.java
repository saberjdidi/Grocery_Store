package tn.app.grocerystore.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import tn.app.grocerystore.R;
import tn.app.grocerystore.activities.DetailsProductActivity;
import tn.app.grocerystore.models.Offer;
import tn.app.grocerystore.models.User;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MyHolder> {

    Context context;
    List<Offer> list;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseDatabase database;

    public OfferAdapter(Context context, List<Offer> list) {
        this.context = context;
        this.list = list;

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.row_offer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.imageView);
        holder.name.setText(list.get(position).getName());
        holder.description.setText(list.get(position).getDescription());
        holder.discount.setText(list.get(position).getDiscount());
        holder.price.setText(String.format("%d$", list.get(position).getPrice()));
        holder.date_debit.setText(list.get(position).getDate_debut());
        holder.date_fin.setText(list.get(position).getDate_fin());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsProductActivity.class);
                intent.putExtra("detail", list.get(position));
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                if(user.getRole().equals("ROLE_ADMIN")){
                                    //Toast.makeText(context, "Long click", Toast.LENGTH_LONG).show();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Are you sure to delete the "+list.get(position).getName());
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            firestore.collection("Offers")
                                                    .document(list.get(position).getOfferId())
                                                    .delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                list.remove(list.get(position));
                                                                notifyDataSetChanged();
                                                                Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                                                            }
                                                            else {
                                                                Toast.makeText(context, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                                    builder.setNegativeButton("No", null);
                                    builder.create().show();
                                }
                                else if(user.getRole().equals("ROLE_CLIENT")){
                                    Toast.makeText(context, "You don't have permission to delete this item", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name, description, discount, price, date_debit, date_fin;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageIv);
            name = itemView.findViewById(R.id.nameTv);
            description = itemView.findViewById(R.id.descriptionTv);
            discount = itemView.findViewById(R.id.discountTv);
            price = itemView.findViewById(R.id.priceTv);
            date_debit = itemView.findViewById(R.id.dateDebitTv);
            date_fin = itemView.findViewById(R.id.dateFinTv);
        }
    }
}
