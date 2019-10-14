package com.minsait.onesaitplatform.digitaltwins.comun.helpers;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.minsait.onesaitplatform.digitaltwins.R;

public class TemperaturaViewHolder extends RecyclerView.ViewHolder {
    public String mBoundString;

    public final View mView;
    public final ImageView mImageView;
    public final TextView mTextViewFecha;
    public final TextView mTextViewGrados;

    public TemperaturaViewHolder(View view) {
        super(view);
        mView = view;
        mImageView = view.findViewById(R.id.img_temperatura);
        mTextViewFecha = view.findViewById(R.id.txt_fecha);
        mTextViewGrados = view.findViewById(R.id.txt_grados);
    }

    public void clearAnimation() {
        mView.clearAnimation();
    }

    @Override
    public String toString() {
        return super.toString() + " " + mTextViewFecha.getText();
    }
}