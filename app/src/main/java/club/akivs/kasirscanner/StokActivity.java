package club.akivs.kasirscanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import club.akivs.kasirscanner.create.CreateActivity;
import club.akivs.kasirscanner.edit.EditDialogFragment;
import club.akivs.kasirscanner.utils.FunctionHelper;
import club.akivs.kasirscanner.utils.database.DaoHandler;
import club.akivs.kasirscanner.utils.database.DaoSession;
import club.akivs.kasirscanner.utils.database.TblStok;
import club.akivs.kasirscanner.utils.database.TblStokDao;

import static android.os.Environment.getExternalStorageDirectory;
import static java.io.File.separator;

public class StokActivity extends AppCompatActivity  implements StokAdapter.StokAdapterCallback,
        StokEditFragment.EditDialogListener {

    private FloatingActionButton fabAddStok;
    private RecyclerView rvStok;
    private DaoSession daoSession;
    private List<TblStok> tblStokList;
    private StokAdapter stokAdapter;
    private TextView tvkode,tvnamastok,tvjumstok,tvhargastok;
    private TblStokDao stokDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stok);
        FunctionHelper.benner(this);
        //header();
        FunctionHelper.header(this,getString(R.string.mnstok),getString(R.string.sp_profil));
        deklarasi();
        listdao();

    }

    private void listdao() {
        rvStok=(RecyclerView)findViewById(R.id.rvStok);

        // ButterKnife.bind(this);
        daoSession = DaoHandler.getInstance(this);
        stokDao = daoSession.getTblStokDao();
        TblStokDao.createTable(daoSession.getDatabase(), true);
        tblStokList = stokDao.queryBuilder().list();
        Log.d("sqlllll", String.valueOf(stokDao.getTablename().isEmpty()));
        stokAdapter = new StokAdapter(tblStokList, this,this);
        rvStok.setLayoutManager(new LinearLayoutManager(this));
        rvStok.setItemAnimator(new DefaultItemAnimator());
        rvStok.setAdapter(stokAdapter);
        stokAdapter.notifyDataSetChanged();

       //tvkode.setText(ge);
        /* tvkode.setText(FunctionHelper.convertRupiah(getTotal()));
        tvkode.setText(FunctionHelper.convertRupiah(getTotal()));
        tvkode.setText(FunctionHelper.convertRupiah(getTotal()));*/

        fabAddStok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StokActivity.this, StokAddActivity.class));
            }
        });
    }

    private void deklarasi() {

        tvkode=(TextView)findViewById(R.id.tvkode);
        tvnamastok=(TextView)findViewById(R.id.tvnamastok);
        tvjumstok=(TextView)findViewById(R.id.tvjumstok);
        tvhargastok=(TextView)findViewById(R.id.tvhargastok);
        fabAddStok=(FloatingActionButton)findViewById(R.id.fabAddStok);



    }

    @SuppressLint("SetTextI18n")
    private void header() {

        SharedPreferences prefx = this.getSharedPreferences(getString(R.string.sp_profil), Context.MODE_PRIVATE);
        String nama = prefx.getString("nama", "");


        TextView headmenu = (TextView) findViewById(R.id.headmenu);
        TextView headtoko = (TextView) findViewById(R.id.headtoko);
        headmenu.setText(getString(R.string.mnstok));
        headtoko.setText(nama);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        finish();
    }

    @Override
    public void onLongClick(int position) {
        long id = tblStokList.get(position).getIdTblStok();
        String kode = tblStokList.get(position).getKode_stok();
        String nama = tblStokList.get(position).getNama_stok();
        String jum = tblStokList.get(position).getJum_stok();
        String ket = tblStokList.get(position).getKet_stok();
        String harga = tblStokList.get(position).getHarga();
        String hargabeli = tblStokList.get(position).getValue1();

        // Bitmap bitn = FunctionHelper.ambilgambar(kode);

        FragmentManager fm = getSupportFragmentManager();
        StokEditFragment stokDialogFragment = StokEditFragment.newInstance(id, kode, nama,jum,harga,ket,hargabeli);
        stokDialogFragment.show(fm, "dialog_edit");
    }

    /*
    Fungsi delete data. Sebelum menghapus data ada semacam popup terlebih dahulu agar meyakinkan user.
     */
    @Override
    public void onDelete(int position) {
        String name = tblStokList.get(position).getKode_stok();
        showDialogDelete(position, name);
    }

    /*
    Fungsi untuk men-totalkan semua nominal yang ada didalam tabel TblPengeluaran.

    private int getTotal(){
        int total = 0;
        for (int i = 0; i < tblPengeluaranList.size(); i++){
            int nominal = tblPengeluaranList.get(i).getNominal();
            total = total + nominal;
        }
        return total;
    }*/

    /*
    Fungsi untuk memanggil Alert Dialog. Alert dialog ini berfungsi untuk meyakinkan user kembali
    apakah datanya ingin dihapus atau tidak.
     */
    private void showDialogDelete(final int position, String name){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(StokActivity.this);
        builder1.setMessage(getString(R.string.hapus)+ name + " ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*
                        Fungsi delete suatu data bedasarkan idnya.
                         */
                        long idTbl = tblStokList.get(position).getIdTblStok();
                        daoSession.getTblStokDao().deleteByKey(idTbl);

                        tblStokList.remove(position);
                        stokAdapter.notifyItemRemoved(position);
                        stokAdapter.notifyItemRangeChanged(position, tblStokList.size());

                        //tvTotal.setText(FunctionHelper.convertRupiah(getTotal()));

                        dialog.dismiss();
                    }
                });

        builder1.setNegativeButton(
                getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    /*
    Fungsi ini untuk menerima data yang dikirimkan dari EditDialogFragment ke HomeActivity.
    Data yang dikirimkan dari EditDialogFragment ini ada id, pembelian, dan nominal. Lalu
    setelah mendapatkan datanya panggil fungsi update dari Greendao.
     */
    @Override
    public void requestUpdate(long id, String kode, String nama, String jum, String harga, String ket, String hargabeli,Bitmap gambar) {
        TblStok tblStok = daoSession.getTblStokDao().load(id);
        tblStok.setKode_stok(kode);
        tblStok.setNama_stok(nama);
        tblStok.setJum_stok(jum);
        tblStok.setKet_stok(ket);
        tblStok.setHarga(harga);
        tblStok.setValue1(hargabeli);
        daoSession.getTblStokDao().update(tblStok);

        try {
            FunctionHelper.saveImage(gambar,"_stok"+kode, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        stokAdapter.notifyDataSetChanged();
        //tvTotal.setText(FunctionHelper.convertRupiah(getTotal()));
    }

    public void gokembali(View view) {
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        finish();
    }

}
