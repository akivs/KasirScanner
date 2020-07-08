package club.akivs.kasirscanner;

import java.util.ArrayList;

public class Kasir {
    private String mNama;
    private int mHarga;
    private String mJum;
    private boolean mOnline;

    public Kasir(String nama,int harga,String jum) {
        mNama = nama;
        mHarga = harga;
        mJum = jum;
    }

    public String getmNama() {
        return mNama;
    }

    public int getmHarga() {
        return mHarga;
    }

    public String getmJum() {
        return mJum;
    }

    public boolean ismOnline() {
        return mOnline;
    }


    public void setmNama(String mNama) {
        this.mNama = mNama;
    }

    public void setmHarga(int mHarga) {
        this.mHarga = mHarga;
    }

    public void setmJum(String mJum) {
        this.mJum = mJum;
    }

    public void setmOnline(boolean mOnline) {
        this.mOnline = mOnline;
    }



}
