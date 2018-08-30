package de.nisnagel.iogo.ui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pixplicity.sharp.Sharp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.service.util.ImageUtils;

public class EnumListAdapter
        extends RecyclerView.Adapter<EnumListAdapter.ViewHolder> {

    private EnumViewModel mViewModel;
    private List<Enum> mValues;

    private final View.OnClickListener mOnClickListener = view -> {
        Enum item = (Enum) view.getTag();

        Context context = view.getContext();
        Intent intent = new Intent(context, EnumActivity.class);
        intent.putExtra(Constants.ARG_ENUM_ID, item.getId());

        context.startActivity(intent);

    };

    private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
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

    EnumListAdapter(List<Enum> list, EnumViewModel mViewModel) {
        this.mValues = list;
        this.mViewModel = mViewModel;
    }

    public void clearList(){
        this.mValues.clear();
    }

    public void addAll(List<Enum> list){
        this.mValues = list;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_enum, parent, false);
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
        @BindView(R.id.icon)
        ImageView mIcon;
        @BindView(R.id.color)
        View mColor;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        void bindEnum(Enum anEnum) {
            mTitle.setText(anEnum.getName());
            if(anEnum.getColor() != null && anEnum.getColor().contains("#")){
                mColor.setBackgroundColor(Color.parseColor(anEnum.getColor()));
            }
            if(anEnum.getIcon() != null) {
                if(anEnum.getIcon().contains("svg+xml")){
                    String pureBase64Encoded = anEnum.getIcon().substring(anEnum.getIcon().indexOf(",") + 1);
                    byte[] decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
                    String data = new String(decodedString);
                    Sharp.loadString(data).into(mIcon);
                }else {
                    mIcon.setImageBitmap(ImageUtils.convertToBitmap(anEnum.getIcon()));
                }
            }
        }
    }

}