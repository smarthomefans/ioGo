package de.nisnagel.iogo.ui.base.viewholder;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

import de.nisnagel.iogo.data.model.State;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    protected Context context;
    SharedPreferences sharedPreferences;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    String getSubtitle(State state){
        String state_subtitle = sharedPreferences.getString("state_subtitle","none");
        if(state_subtitle.equals("role")){
            return state.getRole();
        }
        if(state_subtitle.equals("timestamp")){
            Calendar cal = Calendar.getInstance(Locale.GERMAN);
            cal.setTimeInMillis(state.getTs() * 1000L);
            String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
            return date;
        }

        return "";
    }
}