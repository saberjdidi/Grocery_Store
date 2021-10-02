package tn.app.grocerystore.adapters;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import tn.app.grocerystore.R;
import tn.app.grocerystore.activities.DetailsProductActivity;
import tn.app.grocerystore.models.ViewAllModel;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.MyHolder> {

    Context context;
    List<ViewAllModel> list;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public ProductsAdapter(Context context, List<ViewAllModel> list) {
        this.context = context;
        this.list = list;

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.row_products, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.image);
        holder.name.setText(list.get(position).getName());
        holder.description.setText(list.get(position).getDescription());
        holder.price.setText(String.format("%d$",list.get(position).getPrice()));
        holder.rating.setText(list.get(position).getRating());

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
                //Toast.makeText(context, "Long click", Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure to delete this item ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        firestore.collection("AllProducts")
                                .document(list.get(position).getProductId())
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
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, description, price, rating;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageIv);
            name = itemView.findViewById(R.id.nameTv);
            description = itemView.findViewById(R.id.descriptionTv);
            price = itemView.findViewById(R.id.priceTv);
            rating = itemView.findViewById(R.id.ratingTv);
        }
    }
}
