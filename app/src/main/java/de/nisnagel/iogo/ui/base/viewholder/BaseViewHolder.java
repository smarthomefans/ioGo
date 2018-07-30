package de.nisnagel.iogo.ui.base.viewholder;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)
    TextView mSubtitle;
    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.sync)
    ProgressBar mSync;

    protected Context context;
    protected EnumViewModel mViewModel;
    SharedPreferences sharedPreferences;

    public BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = itemView.getContext();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void bindState(State state) {
        if (state.isSync()) {
            mSync.setVisibility(View.GONE);
            mIcon.setVisibility(View.VISIBLE);
        }else{
            mSync.setVisibility(View.VISIBLE);
            mIcon.setVisibility(View.GONE);
        }
    }

    String getSubtitle(State state) {
        if (state.isSync()) {
            String state_subtitle = sharedPreferences.getString("state_subtitle", "none");
            if (state_subtitle.equals("role")) {
                return state.getRole();
            }
            if (state_subtitle.equals("timestamp")) {
                Calendar cal = Calendar.getInstance(Locale.GERMAN);
                cal.setTimeInMillis(state.getTs());
                String date;
                date = DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();
                return date;
            }

        } else {
            return context.getResources().getText(R.string.syncing_data).toString();
        }
        return "";
    }
}
