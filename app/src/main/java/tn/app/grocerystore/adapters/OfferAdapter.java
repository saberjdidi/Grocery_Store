package tn.app.grocerystore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import tn.app.grocerystore.R;
import tn.app.grocerystore.activities.DetailsProductActivity;
import tn.app.grocerystore.models.Offer;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MyHolder> {

    Context context;
    List<Offer> list;

    public OfferAdapter(Context context, List<Offer> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.row_offer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
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
