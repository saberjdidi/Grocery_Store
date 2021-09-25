package tn.app.grocerystore.adapters;

import android.content.Context;
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
import tn.app.grocerystore.models.NavCategoryDetailedModel;

public class NavCategoryDetailedAdapter extends RecyclerView.Adapter<NavCategoryDetailedAdapter.MyHolder> {

    Context context;
    List<NavCategoryDetailedModel> list;

    public NavCategoryDetailedAdapter(Context context, List<NavCategoryDetailedModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.nav_category_detailed_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.imageView);
        holder.name.setText(list.get(position).getName());
        holder.price.setText(list.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, price;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.nav_cat_detail_img);
            name = itemView.findViewById(R.id.nav_cat_detail_name);
            price = itemView.findViewById(R.id.nav_cat_detail_price);
        }
    }
}
