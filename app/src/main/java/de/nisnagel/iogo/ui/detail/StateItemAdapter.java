package de.nisnagel.iogo.ui.detail;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.ui.base.StateItem;

public class StateItemAdapter
        extends RecyclerView.Adapter<StateItemAdapter.ViewHolder> {

    private StateViewModel mViewModel;
    protected List<StateItem> mValues;

    protected final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            StateItem item = (StateItem) view.getTag();
            mViewModel.changeValue(item.getId());
        }
    };

    public StateItemAdapter(List<StateItem> list, StateViewModel mViewModel) {
        this.mValues = list;
        this.mViewModel = mViewModel;
    }

    public void clearList() {
        this.mValues.clear();
    }

    public void addAll(List<StateItem> list) {
        this.mValues = list;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_state_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bindItem(mValues.get(position));
        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView mTitle;
        @BindView(R.id.card_view)
        CardView mCard;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(StateItem item) {

            if (mViewModel.getValue() != null && mViewModel.getValue().equals(item.getId())) {
                mTitle.setTextColor(Color.WHITE);
            } else {
                mTitle.setTextColor(Color.BLACK);
            }
            mTitle.setText(item.getName());
        }
    }

}