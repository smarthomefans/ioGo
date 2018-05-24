package com.example.nagel.io1.ui.function;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nagel.io1.R;
import com.example.nagel.io1.data.model.Function;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FunctionListAdapter
        extends RecyclerView.Adapter<FunctionListAdapter.ViewHolder> {

    private final FunctionListActivity mParentActivity;
    private final List<Function> mValues;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Function item = (Function) view.getTag();

            Context context = view.getContext();
            Intent intent = new Intent(context, FunctionDetailActivity.class);
            intent.putExtra(FunctionDetailFragment.ARG_FUNCTION_ID, item.getId());

            context.startActivity(intent);

        }
    };

    public FunctionListAdapter(FunctionListActivity parent,
                               List<Function> functionList) {
        mValues = functionList;
        mParentActivity = parent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_function, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bindFunction(mValues.get(position));
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

        public void bindFunction(Function function) {
            mTitle.setText(function.getName());
        }
    }

}