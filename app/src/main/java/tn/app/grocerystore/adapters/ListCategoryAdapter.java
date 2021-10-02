package tn.app.grocerystore.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.app.grocerystore.MyDialog;
import tn.app.grocerystore.R;
import tn.app.grocerystore.activities.NavCategoryActivity;
import tn.app.grocerystore.models.Category;

public class ListCategoryAdapter extends RecyclerView.Adapter<ListCategoryAdapter.ViewHolder> {

    Context context;
    List<Category> list;
    MyDialog dialog;

    public ListCategoryAdapter(Context context, List<Category> list, MyDialog dialog) {
        this.context = context;
        this.list = list;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_listcategory, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(list.get(position).getName());
        holder.description.setText(list.get(position).getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setValeur(position);
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.row_cat_name);
            description = itemView.findViewById(R.id.row_cat_description);
        }
    }
}
