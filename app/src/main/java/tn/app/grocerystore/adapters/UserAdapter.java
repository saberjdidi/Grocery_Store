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
import tn.app.grocerystore.models.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyHolder> {

    Context context;
    List<User> list;

    public UserAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Glide.with(context).load(list.get(position).getProfileImg()).into(holder.profileIv);
        holder.nameEt.setText(list.get(position).getName());
        holder.emailEt.setText(list.get(position).getEmail());
        holder.addressEt.setText(list.get(position).getAddress());
        holder.phoneEt.setText(list.get(position).getNumber());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        ImageView profileIv;
        TextView nameEt, emailEt, addressEt, phoneEt;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            nameEt = itemView.findViewById(R.id.nameEt);
            emailEt = itemView.findViewById(R.id.emailEt);
            addressEt = itemView.findViewById(R.id.addressEt);
            phoneEt = itemView.findViewById(R.id.phoneEt);
        }
    }
}
