package com.example.soham.remembrall;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Soham on 20-Mar-17.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder>{
    private int cardsNumber;
    private List<NoteHolder> noteHolderList = new ArrayList<NoteHolder>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cardText;
        public TextView cardTitle;

        public MyViewHolder(View view) {
            super(view);
            this.cardTitle = (TextView)view.findViewById(R.id.title_card);
            this.cardText = (TextView) view.findViewById(R.id.text_card);
        }
    }

    public CardAdapter(int cardsNumber, List<NoteHolder> noteHolderList)
    {
        this.cardsNumber = cardsNumber;
        this.noteHolderList = noteHolderList;
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
        NoteHolder noteHolder = noteHolderList.get(position);

        myViewHolder.cardText.setText(noteHolder.get_note());
        myViewHolder.cardTitle.setText(noteHolder.get_title());
    }
}
