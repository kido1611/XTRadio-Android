package id.xt.radio.model;

import android.content.Context;
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
public class JadwalAcaraAdapter extends RecyclerView.Adapter<JadwalAcaraAdapter.MyViewHolder>{

    private Context mContext;
    private LayoutInflater inflater;

    private List<JadwalAcaraItem> mItems = null;

    public JadwalAcaraAdapter(Context context, List<JadwalAcaraItem> items){
        mContext = context;
        inflater = LayoutInflater.from(context);
        mItems = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = inflater.inflate(R.layout.jadwal_acara_item, parent, false);
        MyViewHolder holder = new MyViewHolder(rootView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JadwalAcaraItem item = mItems.get(position);
        holder.mAcaraTitle.setText(item.getNamaAcara());

        if(!item.getKeteranganAcara().equals(""))
            holder.mAcaraKeterangan.setText("("+item.getKeteranganAcara()+")");

        holder.mAcaraJam.setText(item.getStartAcara()+".00 - "+item.getFinishAcara()+".00");
        holder.mAcaraPj.setText(item.getPj());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.acara_title)
        TextView mAcaraTitle;

        @BindView(R.id.acara_keterangan)
        TextView mAcaraKeterangan;

        @BindView(R.id.acara_jam)
        TextView mAcaraJam;

        @BindView(R.id.acara_pj)
        TextView mAcaraPj;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
