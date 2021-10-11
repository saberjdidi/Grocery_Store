package tn.app.grocerystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.app.grocerystore.R;
import tn.app.grocerystore.models.Contact;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyHolder> {

    Context context;
    List<Contact> list;

    public ContactAdapter(Context context, List<Contact> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
       holder.nameTv.setText(list.get(position).getName());
        holder.emailTv.setText(list.get(position).getEmail());
        holder.phoneTv.setText(list.get(position).getPhone());
        holder.messageTv.setText(list.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView nameTv, emailTv, phoneTv, messageTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            phoneTv = itemView.findViewById(R.id.phoneTv);
            messageTv = itemView.findViewById(R.id.messageTv);
        }
    }
}
