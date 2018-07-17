package de.nisnagel.iogo.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Enum;

public class EnumHomeListAdapter
        extends RecyclerView.Adapter<EnumHomeListAdapter.ViewHolder> {

    private EnumViewModel mViewModel;
    protected List<Enum> mValues;

    protected final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Enum item = (Enum) view.getTag();

            Context context = view.getContext();
            Intent intent = new Intent(context, EnumDetailActivity.class);
            intent.putExtra(EnumDetailFragment.ARG_ENUM_ID, item.getId());

            context.startActivity(intent);

        }
    };

    protected final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            Enum item = (Enum) view.getTag();
            if(item != null){
                if("true".equals(item.getFavorite())){
                    item.setFavorite("false");
                    Toast.makeText(view.getContext(),"unstarred",Toast.LENGTH_SHORT).show();
                }else{
                    item.setFavorite("true");
                    Toast.makeText(view.getContext(),"starred",Toast.LENGTH_SHORT).show();
                }
                mViewModel.saveEnum(item);

                return true;
            }

            return false;

        }
    };

    public EnumHomeListAdapter(List<Enum> list, EnumViewModel mViewModel) {
        this.mValues = list;
        this.mViewModel = mViewModel;
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
        holder.itemView.setOnLongClickListener(mOnLongClickListener);
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

        public void bindEnum(Enum anEnum) {
            mTitle.setText(anEnum.getName());
        }
    }

}