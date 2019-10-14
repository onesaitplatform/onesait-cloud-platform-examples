package com.minsait.onesaitplatform.digitaltwins.comun.helpers;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.minsait.onesaitplatform.digitaltwins.R;
import com.minsait.onesaitplatform.digitaltwins.vo.Temperatura;

import java.util.List;

public class TemperaturaRecyclerViewAdapter
        extends RecyclerView.Adapter<TemperaturaViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<Temperatura> mValues;

    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    @Override
    public void onViewDetachedFromWindow(TemperaturaViewHolder holder) {
        holder.clearAnimation();
        super.onViewDetachedFromWindow(holder);
    }

    public TemperaturaRecyclerViewAdapter(Context context, List<Temperatura> items) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mValues = items;
    }

    @Override
    public TemperaturaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.temperatura_list_item, parent, false);
        view.setBackgroundResource(mBackground);
        return new TemperaturaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TemperaturaViewHolder holder, final int position) {
        holder.mBoundString = "mBoundString";
        holder.mImageView.setImageResource(R.drawable.logo_minsait);
        holder.mTextViewFecha.setText(mValues.get(position).getFecha());
        holder.mTextViewGrados.setText("" + mValues.get(position).getGrados() + " º");

        // Asignamos el nombre de la transición, debe ser único.
        //ViewCompat.setTransitionName(holder.mImageView, String.valueOf(position) + "_temperatura_image");

        // Animo la fila a mostrar, en el caso de que no se haya mostrado ya.
        //setOnScrollAnimation(holder.mView, position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * Realiza la animación de un elemento que no se haya mostrado ya al realizar el desplazamiento
     * del RecyclerView.
     *
     * @param viewToAnimate Vista a animar.
     * @param position      posición
     */
    private void setOnScrollAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.temperaturas_animation);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}