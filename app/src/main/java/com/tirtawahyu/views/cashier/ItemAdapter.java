package com.tirtawahyu.views.cashier;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tirtawahyu.R;
import com.tirtawahyu.model.Item;
import com.tirtawahyu.util.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Fadhil on 31-Oct-18.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private ArrayList<Item> itemList;
    private OnAddItemClickListener context;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tipeInfoViewHolder)
        TextView tipeInfo;

        @BindView(R.id.totalInfoViewHolder)
        TextView priceInfo;

        @BindView(R.id.addButton)
        ImageButton addButton;

        private ItemAdapter adapter;

        public ItemViewHolder(View view, ItemAdapter adapter) {
            super(view);
            ButterKnife.bind(this, view);

            this.adapter = adapter;

            initComponent();
        }

        private void initComponent() {
            initAddButton();
        }

        private void initAddButton() {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    adapter.add(index);
                }
            });
        }
    }

    public ItemAdapter(OnAddItemClickListener context, ArrayList<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public ArrayList<Item> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_viewholder, viewGroup, false);
        return new ItemAdapter.ItemViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder ticketViewHolder, int i) {
        Item ticket = itemList.get(i);

        String tipe = ticket.getTipe();
        String total = Util.formatPrice(ticket.getPrice());

        ticketViewHolder.tipeInfo.setText(tipe);
        ticketViewHolder.priceInfo.setText(total);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void add(int index) {
        Item item = itemList.get(index);
        context.onItemAddClick(item);
    }

    public interface OnAddItemClickListener {
        void onItemAddClick(Item i);
    }
}
