package de.nisnagel.iogo.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.service.Constants;

public class EnumHomeListAdapter
        extends RecyclerView.Adapter<EnumHomeListAdapter.ViewHolder> {

    private EnumViewModel mViewModel;
    private List<Enum> mValues;

    private final View.OnClickListener mOnClickListener = view -> {
        Enum item = (Enum) view.getTag();

        Context context = view.getContext();
        Intent intent = new Intent(context, EnumActivity.class);
        intent.putExtra(Constants.ARG_ENUM_ID, item.getId());

        context.startActivity(intent);

    };


    public EnumHomeListAdapter(List<Enum> list, EnumViewModel mViewModel) {
        this.mValues = list;
        this.mViewModel = mViewModel;
    }

    public void clearList(){
        this.mValues.clear();
    }

    public void addAll(List<Enum> list) {
        for(Iterator iter = list.iterator();
            iter.hasNext();) {
            Enum item = (Enum) iter.next();
            if(item.isHidden()){
                iter.remove();
            }
        }
        this.mValues = list;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_enum_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bindEnum(mValues.get(position));
        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)  TextView mTitle;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        void bindEnum(Enum anEnum) {
            mTitle.setText(anEnum.getName());
        }
    }

}