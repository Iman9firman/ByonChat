package com.honda.android.FragmentDinamicRoom;

/**
 * Created by Iman Firmansyah on 8/29/2016.
 */
public class ModelWilayah {

    private String kodepos;
    private String prov;
    private String kab;
    private String jenis;
    private String kec;
    private String kel;

    public ModelWilayah(String kodePos, String provI, String kabU, String jen, String kecA, String kelU) {
        this.kodepos = kodePos;
        this.prov = provI;
        this.kab = kabU;
        this.jenis = jen;
        this.kec = kecA;
        this.kel = kelU;
    }

    public String getKodepos() {
        return kodepos;
    }

    public void setKodepos(String kodepos) {
        this.kodepos = kodepos;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getKab() {
        return kab;
    }

    public void setKab(String kab) {
        this.kab = kab;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getKec() {
        return kec;
    }

    public void setKec(String kec) {
        this.kec = kec;
    }

    public String getKel() {
        return kel;
    }

    public void setKel(String kel) {
        this.kel = kel;
    }
}