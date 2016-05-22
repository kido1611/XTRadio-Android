package id.xt.radio.fragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.xt.radio.R;
import id.xt.radio.Utility.JSONParser;
import id.xt.radio.model.FacebookFeed;
import id.xt.radio.model.JadwalAcaraItem;
import id.xt.radio.model.JadwalAdapter;
import id.xt.radio.model.JadwalItem;

/**
 * Created by Kido1611 on 20-May-16.
 */
public class FragmentJadwal extends BaseFragment {

    public FragmentJadwal(){

    }

    String url = "http://kido1611.id/xt/jadwal.php";

    @BindView(R.id.jadwal_list)
    RecyclerView mJadwalList;

    JadwalAdapter mJadwalAdapter;
    private List<JadwalItem> mItems = new ArrayList<>();

    private JSONParser mJSONParser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJadwalAdapter = new JadwalAdapter(getActivity(), mItems);

        mJSONParser = new JSONParser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_jadwal, container, false);
        ButterKnife.bind(this, rootView);

        mJadwalList.setAdapter(mJadwalAdapter);
        mJadwalList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        new FetchJadwal().execute();

        return rootView;
    }

    class FetchJadwal extends AsyncTask<Void, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(Void... voids) {
            Uri buildUri = Uri.parse(url).buildUpon().build();

            String jsonString = mJSONParser.makeHTTPRequest(buildUri, "GET");
            if(jsonString==null) return null;
            try {
                JSONObject obj = new JSONObject(jsonString);
                return obj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            if(jsonObject==null) return;

            try {
                if(jsonObject.getInt("sukses")==0) return;

                JSONArray jadwalList = jsonObject.getJSONArray("data");
                for(int i=0;i<jadwalList.length();i++){
                    JSONObject obj = jadwalList.getJSONObject(i);
                    fetchJadwalItem(obj);
                }
                mJadwalAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void fetchJadwalItem(JSONObject obj){
            JadwalItem item = new JadwalItem();
            try {
                item.setHari(obj.getString("hari"));
                JSONArray acaraArray = obj.getJSONArray("acara");

                for(int i=0;i<acaraArray.length();i++){
                    JSONObject acaraobj = acaraArray.getJSONObject(i);

                    JadwalAcaraItem itemAcara = new JadwalAcaraItem();
                    itemAcara.setNamaAcara(acaraobj.getString("acara"));
                    itemAcara.setKeteranganAcara(acaraobj.getString("keterangan"));
                    itemAcara.setStartAcara(acaraobj.getString("startClock"));
                    itemAcara.setFinishAcara(acaraobj.getString("finishClock"));
                    itemAcara.setPj(acaraobj.getString("pj"));

                    item.addAcara(itemAcara);
                }

                mItems.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
