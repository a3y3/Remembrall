package com.example.soham.remembrall;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Soham on 20-Mar-17.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder>{
    private Context mContext;
    //private List<int> cardsList;
    private int cardsNumber;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cardText;

        public MyViewHolder(View view) {
            super(view);
            this.cardText = (TextView) view.findViewById(R.id.text_card);
        }
    }

    public CardAdapter(Context mContext, int cardsNumber)
    {
        this.mContext = mContext;
        this.cardsNumber = cardsNumber;
    }

    public int getItemCount() {
        return cardsNumber;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_card,parent,false);
        return  new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, int position)
    {
        myViewHolder.cardText.setText(("testing"));
    }
}
