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

public class ViewAllAdapter extends RecyclerView.Adapter<ViewAllAdapter.ViewHolder> {

    Context context;
    List<ViewAllModel> list;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public ViewAllAdapter(Context context, List<ViewAllModel> list) {
        this.context = context;
        this.list = list;

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.view_all_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.image);
        holder.name.setText(list.get(position).getName());
        holder.description.setText(list.get(position).getDescription());
        holder.rating.setText(list.get(position).getRating());

        if(list.get(position).getType().equals("egg")){
            holder.price.setText(list.get(position).getPrice()+"/dozen");
        }
        else if(list.get(position).getType().equals("milk")){
            holder.price.setText(list.get(position).getPrice()+"/littre");
        }
        else {
            holder.price.setText(list.get(position).getPrice()+"/kg");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsProductActivity.class);
                intent.putExtra("detail", list.get(position));
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, description, rating, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.view_img);
            name = itemView.findViewById(R.id.view_name);
            description = itemView.findViewById(R.id.view_description);
            rating = itemView.findViewById(R.id.view_rating);
            price = itemView.findViewById(R.id.view_price);
        }
    }
}
