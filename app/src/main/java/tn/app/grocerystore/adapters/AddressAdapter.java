package tn.app.grocerystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.app.grocerystore.R;
import tn.app.grocerystore.models.Address;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyHolder> {

    Context context;
    List<Address> list;
    SelectedAddress selectedAddress;

    private RadioButton selectedRadioBtn;

    public AddressAdapter(Context context, List<Address> list, SelectedAddress selectedAddress) {
        this.context = context;
        this.list = list;
        this.selectedAddress = selectedAddress;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.address_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
       holder.address.setText(list.get(position).getUserAddress());
       holder.radioButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               for (Address address : list){
                   address.setSelected(false);
               }
               list.get(position).setSelected(true);

               if(selectedRadioBtn != null){
                   selectedRadioBtn.setChecked(false);
               }
               selectedRadioBtn = (RadioButton) view;
               selectedRadioBtn.setChecked(true);
               selectedAddress.setAddress(list.get(position).getUserAddress());
           }
       });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView address;
        RadioButton radioButton;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address_add);
            radioButton = itemView.findViewById(R.id.select_address);
        }
    }
    public interface SelectedAddress {
        void setAddress(String address);
    }
}
