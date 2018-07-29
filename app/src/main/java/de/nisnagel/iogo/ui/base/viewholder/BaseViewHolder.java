package de.nisnagel.iogo.ui.base.viewholder;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    protected Context context;
    protected EnumViewModel mViewModel;
    SharedPreferences sharedPreferences;

    public BaseViewHolder(View itemView) {
        super(itemView);
        this.context = itemView.getContext();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
