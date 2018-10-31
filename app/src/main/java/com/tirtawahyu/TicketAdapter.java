package com.tirtawahyu;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Fadhil on 31-Oct-18.
 */

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private ArrayList<Ticket> ticketList;

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tipeInfoViewHolder)
        TextView tipeInfo;

        @BindView(R.id.jumlahInfoViewHolder)
        TextView jumlahInfo;

        @BindView(R.id.totalInfoViewHolder)
        TextView totalInfo;

        public TicketViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public TicketAdapter(ArrayList<Ticket> ticketList) {
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ticket_viewholder, viewGroup, false);
        return new TicketAdapter.TicketViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder ticketViewHolder, int i) {
        Ticket ticket = ticketList.get(i);

        String tipe = ticket.getTipe();
        String jumlah = String.valueOf(ticket.getJumlah());
        String total = String.valueOf(ticket.getTotal());

        ticketViewHolder.tipeInfo.setText(tipe);
        ticketViewHolder.jumlahInfo.setText(jumlah);
        ticketViewHolder.totalInfo.setText(total);
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }
}
