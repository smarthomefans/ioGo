package de.nisio.iobroker.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.Enum;

public class EnumListAdapter
        extends RecyclerView.Adapter<EnumListAdapter.ViewHolder> {

    private final List<Enum> mValues;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Enum item = (Enum) view.getTag();

            Context context = view.getContext();
            Intent intent = new Intent(context, EnumDetailActivity.class);
            intent.putExtra(EnumDetailFragment.ARG_ENUM_ID, item.getId());

            context.startActivity(intent);

        }
    };

    public EnumListAdapter(List<Enum> roomList) {
        mValues = roomList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_enum, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bindRoom(mValues.get(position));
        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)  TextView mTitle;
        @BindView(R.id.icon)
        ImageView mIcon;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        public void bindRoom(Enum anEnum) {
            mTitle.setText(anEnum.getName());
            if(anEnum.isFavorite()){
                mIcon.setImageResource(R.drawable.star);
            }
        }
    }

}