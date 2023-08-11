package com.example.archirectureobjects;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final ArrayList<HistoryBuildTitle> mTitles;
    private HistoryBuildTitle mHistoryBuildTitle;
    private final HistoryFragment mContext;
    public HistoryAdapter(HistoryFragment context, ArrayList<HistoryBuildTitle> historyBuildTitlesList) {
        mTitles = historyBuildTitlesList;
        mContext = context;
    }
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View historyView = inflater.inflate(R.layout.history_title_view, parent, false);
        HistoryAdapter.ViewHolder viewHolder = new HistoryAdapter.ViewHolder(historyView);
        return viewHolder;
    }
    public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position) {
        this.mHistoryBuildTitle = mTitles.get(position);
        TextView historyObjectTitle = holder.historyObjectTitle;
        historyObjectTitle.setText(mHistoryBuildTitle.gettitle());
    }
    public int getItemCount() {
        return mTitles.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView historyObjectTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            historyObjectTitle = itemView.findViewById(R.id.historyTitle);
        }
    }
}

