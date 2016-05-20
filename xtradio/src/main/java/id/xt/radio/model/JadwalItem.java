package id.xt.radio.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kido1611 on 20-May-16.
 */
public class JadwalItem {

    String hari;
    List<JadwalAcaraItem> listAcara;

    public JadwalItem(){
        listAcara = new ArrayList<JadwalAcaraItem>();
        listAcara.clear();
    }

    public void addAcara(JadwalAcaraItem item){
        listAcara.add(item);
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public List<JadwalAcaraItem> getListAcara() {
        return listAcara;
    }

    public void setListAcara(List<JadwalAcaraItem> listAcara) {
        this.listAcara = listAcara;
    }
}
