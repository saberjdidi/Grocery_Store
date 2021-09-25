package tn.app.grocerystore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.app.grocerystore.R;
import tn.app.grocerystore.models.MyCartModel;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyHolder> {

    Context context;
    List<MyCartModel> list;
    int totalPrice = 0;

    public MyCartAdapter(Context context, List<MyCartModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.name.setText(list.get(position).getProductName());
        holder.price.setText(list.get(position).getProductPrice());
        holder.date.setText(list.get(position).getCurrentDate());
        holder.time.setText(list.get(position).getCurrentTime());
        holder.totalPrice.setText(String.valueOf(list.get(position).getTotalPrice()));
        holder.totalQuantity.setText(list.get(position).getTotalQuantity());

        //pass total amount to My Cart Fragment
        //pass data from adapter to fragment
        totalPrice = totalPrice + list.get(position).getTotalPrice();
        Intent intent = new Intent("MyTotalAmount");
        intent.putExtra("totalAmount", totalPrice);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        TextView name, price, date, time, totalPrice, totalQuantity;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            date = itemView.findViewById(R.id.current_date);
            time = itemView.findViewById(R.id.current_time);
            totalPrice = itemView.findViewById(R.id.total_price);
            totalQuantity = itemView.findViewById(R.id.total_quantity);
        }
    }
}
