package com.tirtawahyu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Fadhil on 31-Oct-18.
 */

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private ArrayList<Ticket> ticketList;
    private Updateable context;

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tipeInfoViewHolder)
        TextView tipeInfo;

        @BindView(R.id.jumlahInfoViewHolder)
        TextView jumlahInfo;

        @BindView(R.id.totalInfoViewHolder)
        TextView totalInfo;

        @BindView(R.id.deleteButton)
        Button deleteButton;

        private TicketAdapter adapter;

        public TicketViewHolder(View view, TicketAdapter adapter) {
            super(view);
            ButterKnife.bind(this, view);

            this.adapter = adapter;

            initComponent();
        }

        private void initComponent() {
            initDeleteButton();
        }

        private void initDeleteButton() {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    adapter.delete(index);
                }
            });
        }
    }

    public TicketAdapter(Updateable context, ArrayList<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ticket_viewholder, viewGroup, false);
        return new TicketAdapter.TicketViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder ticketViewHolder, int i) {
        Ticket ticket = ticketList.get(i);

        String tipe = ticket.getTipe();
        String jumlah = String.valueOf(ticket.getJumlah());
        String total = Util.formatPrice(ticket.getTotal());

        ticketViewHolder.tipeInfo.setText(tipe);
        ticketViewHolder.jumlahInfo.setText(jumlah);
        ticketViewHolder.totalInfo.setText(total);
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public void addData(Ticket ticket) {
        if (ticketList.contains(ticket)) {
            int index = ticketList.indexOf(ticket);

            Ticket existingTicket = ticketList.get(index);
            int jumlah = existingTicket.getJumlah();

            jumlah += ticket.getJumlah();
            int total = jumlah * Constants.TIKET_UMUM;

            ticket.setJumlah(jumlah);
            ticket.setTotal(total);

            ticketList.set(index, ticket);
        } else {
            ticketList.add(ticket);
        }
        notifyDataSetChanged();
    }

    public int getTotalPrice() {
        int total = 0;
        for (Ticket ticket: ticketList) {
            total += ticket.getTotal();
        }
        return total;
    }

    private void delete(int index) {
        ticketList.remove(index);
        notifyDataSetChanged();
        context.updateUI();
    }
}
