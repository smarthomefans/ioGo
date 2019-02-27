/*
 * ioGo - android app to control ioBroker home automation server.
 *
 * Copyright (C) 2018  Nis Nagel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nisnagel.iogo.ui.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Message;

public class MessageListAdapter
        extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private MessageViewModel mViewModel;
    private List<Message> mValues;

    MessageListAdapter(List<Message> list, MessageViewModel mViewModel) {
        this.mValues = list;
        this.mViewModel = mViewModel;
    }

    public void clearList() {
        this.mValues.clear();
    }

    public void addAll(List<Message> list) {
        this.mValues = list;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bindMessage(mValues.get(position));
        holder.mCard.setTag(mValues.get(position));
        holder.btnDelete.setOnClickListener((View v) -> {
            Message item = mValues.remove(position);
            mViewModel.deleteMessage(item);
            Toast.makeText(holder.itemView.getContext(), R.string.main_message_deleted, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_view)
        CardView mCard;
        @BindView(R.id.title)
        TextView mTitle;
        @BindView(R.id.subtitle)
        TextView mSubtitle;
        @BindView(R.id.img)
        ImageView mImage;
        @BindView(R.id.text)
        TextView mText;
        @BindView(R.id.btnDelete)
        ImageButton btnDelete;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        void bindMessage(Message anMessage) {
            mTitle.setText(anMessage.getTitle());
            long time = anMessage.getTimestamp();
            long now = System.currentTimeMillis();
            CharSequence relativeTimeStr = DateUtils.getRelativeTimeSpanString(time,
                    now, DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_NUMERIC_DATE);
            mSubtitle.setText(relativeTimeStr);
            mText.setText(anMessage.getText());
            if (anMessage.getImage() != null) {
                File root = itemView.getContext().getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS);
                File file = new File(root, anMessage.getImage());
                Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                mImage.setImageBitmap(bmp);
                mImage.setVisibility(View.VISIBLE);
            } else {
                mImage.setVisibility(View.GONE);
            }
        }
    }

}