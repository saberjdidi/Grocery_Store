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
import tn.app.grocerystore.models.OrderModel;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyHolder> {

    Context context;
    List<OrderModel> list;

    public OrderAdapter(Context context, List<OrderModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        holder.email.setText(list.get(position).getEmail());
        holder.name.setText(list.get(position).getName());
        holder.address.setText(list.get(position).getAddress());
        holder.date.setText(list.get(position).getCurrentDate());
        holder.time.setText(list.get(position).getCurrentTime());
        holder.amount.setText(list.get(position).getAmount());
        holder.currency.setText(list.get(position).getCurrency());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView email, name, address, date, time, amount, currency;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.order_email);
            name = itemView.findViewById(R.id.order_name);
            address = itemView.findViewById(R.id.order_address);
            date = itemView.findViewById(R.id.current_date);
            time = itemView.findViewById(R.id.current_time);
            amount = itemView.findViewById(R.id.order_amount);
            currency = itemView.findViewById(R.id.order_currency);
        }
    }
}
