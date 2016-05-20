package id.xt.radio.model;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.xt.radio.R;

/**
 * Created by Kido1611 on 20-May-16.
 */
public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.MyViewHolder>{

    private Context mContext;
    private LayoutInflater inflater;

    private List<JadwalItem> mItems = null;

    public JadwalAdapter(Context context, List<JadwalItem> items){
        mContext = context;
        inflater = LayoutInflater.from(context);
        mItems = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = inflater.inflate(R.layout.jadwal_item, parent, false);

        MyViewHolder holder = new MyViewHolder(rootView);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JadwalItem item = mItems.get(position);
        holder.mHariTitle.setText(item.getHari());
        JadwalAcaraAdapter adapter = new JadwalAcaraAdapter(mContext, item.getListAcara());
        holder.mAcaraList.setAdapter(adapter);
        holder.mAcaraList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.title)
        TextView mHariTitle;

        @BindView(R.id.acara_list)
        RecyclerView mAcaraList;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
