package de.nisnagel.iogo.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pixplicity.sharp.Sharp;

import java.sql.Array;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.service.util.ImageUtils;
import de.nisnagel.iogo.ui.helper.ItemTouchHelperAdapter;
import de.nisnagel.iogo.ui.helper.ItemTouchHelperViewHolder;

public class EnumListAdapter
        extends RecyclerView.Adapter<EnumListAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private EnumViewModel mViewModel;
    private List<Enum> mValues;

    private final View.OnClickListener mOnClickListener = view -> {
        Enum item = (Enum) view.getTag();

        Context context = view.getContext();
        Intent intent = new Intent(context, EnumActivity.class);
        intent.putExtra(Constants.ARG_ENUM_ID, item.getId());

        context.startActivity(intent);

    };

    EnumListAdapter(List<Enum> list, EnumViewModel mViewModel) {
        this.mValues = list;
        this.mViewModel = mViewModel;
    }

    public void clearList() {
        this.mValues.clear();
    }

    public void addAll(List<Enum> list, boolean show_hidden) {
        if (!show_hidden) {
            for (Iterator iter = list.iterator();
                 iter.hasNext(); ) {
                Enum item = (Enum) iter.next();
                if (item.isHidden()) {
                    iter.remove();
                }
            }
        }
        this.mValues = list;
    }

    @Override
    public void onItemDismiss(int position) {
        //do nothing
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Enum prev = mValues.remove(fromPosition);
        mValues.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
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
        holder.mCard.setTag(mValues.get(position));
        holder.mCard.setOnClickListener(mOnClickListener);
        holder.favoriteButton.setOnClickListener(v -> {
            Enum item = mValues.get(position);
            if (item != null) {
                if (item.isFavorite()) {
                    item.setFavorite(false);
                    Toast.makeText(v.getContext(), "unstarred", Toast.LENGTH_SHORT).show();
                } else {
                    item.setFavorite(true);
                    Toast.makeText(v.getContext(), "starred", Toast.LENGTH_SHORT).show();
                }
                mViewModel.saveEnum(item);
            }
        });
        holder.hideButton.setOnClickListener((View v) -> {
            Enum item = mValues.remove(position);
            if (item != null) {
                item.setHidden(!item.isHidden());
                mViewModel.saveEnum(item);
                notifyItemRemoved(position);
                Toast.makeText(v.getContext(), "hidden", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        @BindView(R.id.card_view)
        CardView mCard;
        @BindView(R.id.title)
        TextView mTitle;
        @BindView(R.id.icon)
        ImageView mIcon;
        @BindView(R.id.color)
        View mColor;
        @BindView(R.id.favorite_button)
        ImageButton favoriteButton;
        @BindView(R.id.hide_button)
        ImageButton hideButton;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        void bindEnum(Enum anEnum) {
            mTitle.setText(anEnum.getName());
            if (anEnum.getColor() != null && anEnum.getColor().contains("#")) {
                mColor.setBackgroundColor(Color.parseColor(anEnum.getColor()));
            }
            if (anEnum.isHidden()) {
                mTitle.setTextColor(Color.BLACK);
                hideButton.setImageResource(R.drawable.eye_off);
            } else {
                hideButton.setImageResource(R.drawable.eye);
            }
            if (anEnum.getIcon() != null) {
                if (anEnum.getIcon().contains("svg+xml")) {
                    String pureBase64Encoded = anEnum.getIcon().substring(anEnum.getIcon().indexOf(",") + 1);
                    byte[] decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
                    String data = new String(decodedString);
                    Sharp.loadString(data).into(mIcon);
                } else {
                    mIcon.setImageBitmap(ImageUtils.convertToBitmap(anEnum.getIcon()));
                }
            }
            if (anEnum.isFavorite()) {
                favoriteButton.setImageResource(R.drawable.starred);
            } else {
                favoriteButton.setImageResource(R.drawable.unstarred);
            }
        }

        @Override
        public void onItemSelected() {
            mCard.setCardBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            mCard.setCardBackgroundColor(Color.DKGRAY);
            for (int i = 0; i < mValues.size(); i++) {
                mValues.get(i).setRank(i);
            }
            mViewModel.saveEnum((Enum[]) mValues.toArray(new Enum[mValues.size()]));
        }
    }

}