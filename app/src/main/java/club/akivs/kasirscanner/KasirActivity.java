package club.akivs.kasirscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import club.akivs.kasirscanner.utils.FunctionHelper;
import club.akivs.kasirscanner.utils.database.DaoHandler;
import club.akivs.kasirscanner.utils.database.DaoSession;
import club.akivs.kasirscanner.utils.database.TblKasir;
import club.akivs.kasirscanner.utils.database.TblKasirDao;
import club.akivs.kasirscanner.utils.database.TblStok;
import club.akivs.kasirscanner.utils.database.TblStokDao;
import info.androidhive.barcode.BarcodeReader;

public class KasirActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener, KasirAdapter.KasirAdapterCallback, KasirCariAdapter.KasirCariAdapterCallback {

    private BarcodeDetector barcodedet;
    BarcodeReader barcodeReader;
    private EditText etstokkode,etstoknama,etstokjum,etstokharga;
    private DaoSession daoSession;
    private Button btnsave;
    private RecyclerView rvkasir;
    private List<TblKasir> tblKasirList;
    private TblKasirDao kasirDao;
    private KasirAdapter kasirAdapter;

    private TextView tvtotalkasir;
    private EditText etkali;
    private String snama,salamat,stelepon,skasir,slogin;
    private String kodeu;
    private EditText ettunai;
    private SharedPreferences pref;
    private RelativeLayout rltap;
    private String ceku;
    private String sessionId ;
    private List<TblStok> laa;
    private RelativeLayout rlkasir;
    private RecyclerView rvkasircari;
    private TblStokDao stokDao;
    private List<TblStok> tblStokcariList;
    private KasirCariAdapter kasircariAdapter;
    private EditText etcari;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kasir);
        sessionId = getIntent().getStringExtra("EXTRA_ID");
        sessionkasir();

        SharedPreferences prefx = this.getSharedPreferences(getString(R.string.sp_kasir), Context.MODE_PRIVATE);
        kodeu = prefx.getString("kodeunik", "");
        ceku = prefx.getString("cek", "");

        daoSession = DaoHandler.getInstance(this);
        kodescanner();
        header();
        //FunctionHelper.header(this,getString(R.string.mnaddstok),getString(R.string.sp_profil));
        rvkasircari=(RecyclerView)findViewById(R.id.rvkasircari);
        //========
        listdao();

    }

    private void listkasircari() {

        // ButterKnife.bind(this);
        stokDao = daoSession.getTblStokDao();
        TblStokDao.createTable(daoSession.getDatabase(), true);
        tblStokcariList = stokDao.queryBuilder().list();
        Log.d("sqlllll", String.valueOf(stokDao.getTablename().isEmpty()));
        kasircariAdapter = new KasirCariAdapter(tblStokcariList, this);
        rvkasircari.setLayoutManager(new LinearLayoutManager(this));
        rvkasircari.setItemAnimator(new DefaultItemAnimator());
        rvkasircari.setAdapter(kasircariAdapter);
        kasircariAdapter.notifyDataSetChanged();

        etcari.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                //Toast.makeText(getApplicationContext(), "Sorry couldnt setup the detector "+s.toString(), Toast.LENGTH_LONG).show();
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });
    }

    private void filter(String text) {
        ArrayList<TblStok> filteredList = new ArrayList<>();
        for (TblStok item : tblStokcariList) {
            if (item.getNama_stok().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }else if (item.getKode_stok().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        kasircariAdapter.filterList(filteredList);
    }

    private void header() {

        SharedPreferences prefx = this.getSharedPreferences(getString(R.string.sp_profil), Context.MODE_PRIVATE);
        snama = prefx.getString("nama", "");
        salamat = prefx.getString("alamat", "");
        stelepon = prefx.getString("tlp", "");
        skasir = prefx.getString("kasir", "");
        slogin = prefx.getString("login", "");

        TextView headmenu = (TextView) findViewById(R.id.headmenu);
        TextView headtoko = (TextView) findViewById(R.id.headtoko);
        headmenu.setText(getString(R.string.mnkasir));
        headtoko.setText(snama);

    }

    private void sessionkasir() {
        rlkasir = (RelativeLayout) findViewById(R.id.rlkasir);
        TextView kode_kasir = (TextView) findViewById(R.id.kode_kasir);
        kode_kasir.setVisibility(View.GONE);
        etkali = (EditText) findViewById(R.id.etkali);
        ettunai = (EditText) findViewById(R.id.ettunai);
        tvtotalkasir = (TextView) findViewById(R.id.tvtotalkasir);
        rltap = (RelativeLayout) findViewById(R.id.rltap);
        etcari = (EditText) findViewById(R.id.etcari);

        rltap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeReader.resumeScanning();
                rltap.setVisibility(View.GONE);
            }
        });

        kode_kasir.setText(String.valueOf(FunctionHelper.tanggalnow()+FunctionHelper.randomnum()));
        pref = getSharedPreferences(getString(R.string.sp_kasir), MODE_PRIVATE);
        if(sessionId == null){
            SharedPreferences.Editor editorunik = pref.edit();
            editorunik.putString("kodeunik",FunctionHelper.tanggalnow()+FunctionHelper.randomnum());
            editorunik.putString("cek","0");
            editorunik.apply();//commit();
        }

    }

    private void listdao() {
        rvkasir=(RecyclerView)findViewById(R.id.rvkasir);

        // ButterKnife.bind(this);
        daoSession = DaoHandler.getInstance(this);
        kasirDao = daoSession.getTblKasirDao();
        //TblKasirDao.dropTable(daoSession.getDatabase(), true);
        //FunctionHelper.onUpgrade(1,1,this);
        TblKasirDao.createTable(daoSession.getDatabase(), true);

        Log.d("sqlllll", String.valueOf(kasirDao.getSession()));
        //tblKasirList = kasirDao.queryBuilder().list();

        tblKasirList = kasirDao.queryBuilder().where(TblKasirDao.Properties.Kode_kasir.eq(kodeu)).list();

        kasirAdapter = new KasirAdapter(tblKasirList, this);
        rvkasir.setLayoutManager(new LinearLayoutManager(this));
        rvkasir.setItemAnimator(new DefaultItemAnimator());
        rvkasir.setAdapter(kasirAdapter);
        kasirAdapter.notifyDataSetChanged();

        //tvkode.setText(ge);
        /* tvkode.setText(FunctionHelper.convertRupiah(getTotal()));
        tvkode.setText(FunctionHelper.convertRupiah(getTotal()));
        tvkode.setText(FunctionHelper.convertRupiah(getTotal()));

        fabAddStok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StokActivity.this, StokAddActivity.class));
            }
        });*/
    }

    private void kodescanner() {
        barcodedet = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE | Barcode.CODABAR| Barcode.ITF| Barcode.CODE_128| Barcode.CODE_93| Barcode.CODE_39| Barcode.AZTEC)
                .build();

        if (!barcodedet.isOperational()) {
            Toast.makeText(getApplicationContext(), "Sorry couldnt setup the detector", Toast.LENGTH_LONG).show();
            this.finish();
        }
        // get the barcode reader instance
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_scanner);

    }

    @Override
    public void onScanned(final Barcode barcode) {

        barcodeReader.playBeep();
        final String kls = String.valueOf(etkali.getText());

        //SharedPreferences prefx = this.getSharedPreferences(getString(R.string.sp_kasir), Context.MODE_PRIVATE);
        //final String kodeu = prefx.getString("kodeunik", "");
        //=======
        Log.e("xxx", kls+"onScanned: " + barcode.displayValue);
        //======
        if(kls.equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.jumisi), Toast.LENGTH_SHORT).show();
            etkali.setError(getString(R.string.jumisi));
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(getApplicationContext(), "Barcode: " + barcode.displayValue, Toast.LENGTH_SHORT).show();
                    //etstokkode.setText(barcode.displayValue);
                    barcodeReader.pauseScanning();
                    rltap.setVisibility(View.VISIBLE);
                    tambahbarang(barcode.displayValue,kls);
                }
            });
        }


    }

    private void tambahbarang(String displayValue, String kls) {
        laa = FunctionHelper.caristok(displayValue, KasirActivity.this);
        String cc = String.valueOf(laa);
        Log.e("xxx", "cekxx: " + laa);
        if(cc.equals("[]")){
            Toast.makeText(getApplicationContext(), getString(R.string.dialog_stoknull), Toast.LENGTH_SHORT).show();
        }else{
            int kl = Integer.parseInt(kls);
            String nmstok = laa.get(0).getNama_stok();
            Double hrgstok = Double.valueOf(laa.get(0).getHarga());
            Double hrgbeli = Double.valueOf(laa.get(0).getValue1());
            String kdstok = laa.get(0).getKode_stok();
            int jums = Integer.parseInt(laa.get(0).getJum_stok());
            Log.d("xxx", "cari: " + nmstok);
            if(jums < kl){
                Toast.makeText(getApplicationContext(), getString(R.string.dialog_stokhabis), Toast.LENGTH_SHORT).show();
            }else{
                tambahkasir(kdstok,nmstok,hrgstok,kl,kodeu,hrgbeli);
                kurangistok(kdstok,kl);
            }
        }
    }

    private void kurangistok(String kdstok, int jums) {

        TblStokDao stokDao = daoSession.getTblStokDao();
        List<TblStok> tblStokList = stokDao.queryBuilder().where(TblStokDao.Properties.Kode_stok.eq(kdstok)).list();

        for (int i = 0; i < tblStokList.size(); i++){
            Long idx = tblStokList.get(i).getIdTblStok();
            int jumst = Integer.parseInt(tblStokList.get(i).getJum_stok());
            TblStok tblStok = daoSession.getTblStokDao().load(idx);
            tblStok.setJum_stok(String.valueOf(jumst-jums));
            daoSession.getTblStokDao().update(tblStok);
            Log.d("update",idx+"xx"+jumst+"zz"+jums);
        }

    }

    private void tambahkasir(String kdstok, String nmstok, Double hrgstok, int kl, String kodeu, Double hrgbeli) {


        TblKasir tblkasir = new TblKasir();
        tblkasir.setKode_kasir(kodeu);
        tblkasir.setNama_kasir(skasir);
        tblkasir.setKode_stok(kdstok);
        tblkasir.setNama_stok(nmstok);
        tblkasir.setJum_stok(String.valueOf(kl));
        tblkasir.setHarga(String.valueOf(hrgstok));
        tblkasir.setValue1(String.valueOf(hrgbeli));
        tblkasir.setTanggal(FunctionHelper.tanggalnow());
        tblkasir.setStatus(0);
        daoSession.getTblKasirDao().insert(tblkasir);
        //loadMore();

        //tblKasirList = kasirDao.queryBuilder().list();
        tblKasirList = kasirDao.queryBuilder().where(TblKasirDao.Properties.Kode_kasir.eq(kodeu)).list();

        kasirAdapter = new KasirAdapter(tblKasirList, this);
        rvkasir.setLayoutManager(new LinearLayoutManager(this));
        rvkasir.setItemAnimator(new DefaultItemAnimator());
        rvkasir.setAdapter(kasirAdapter);
        kasirAdapter.notifyDataSetChanged();
        tvtotalkasir.setText(FunctionHelper.convertRupiah(getTotal(),this,getString(R.string.sp_profil)));
        Toast.makeText(KasirActivity.this, getString(R.string.dialog_berhasil),
                Toast.LENGTH_SHORT).show();
        etkali.setText("1");
        //startActivity(new Intent(StokAddActivity.this, StokActivity.class)
        //        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
       // finish();
    }

    private Double getTotal(){
        Double total = 0.0;
        for (int i = 0; i < tblKasirList.size(); i++){
            Double nominal = Double.valueOf(tblKasirList.get(i).getHarga());
            Double juml = Double.valueOf(tblKasirList.get(i).getJum_stok());
            total = total + (nominal*juml);
        }
        return total;
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {
        Toast.makeText(getApplicationContext(), "Error occurred while scanning " + errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraPermissionDenied() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        finish();
    }

    @Override
    public void onLongClick(int position) {

    }

    @Override
    public void onDelete(int position) {
        String name = tblKasirList.get(position).getNama_stok();
        showDialogDelete(position, name);
    }

    private void showDialogDelete(final int position, String name) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(KasirActivity.this);
        builder1.setMessage(getString(R.string.hapus)+ name + " ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*
                        Fungsi delete suatu data bedasarkan idnya.
                         */
                        long idTbl = tblKasirList.get(position).getIdTblKasir();
                        tambahstok(idTbl);
                        daoSession.getTblKasirDao().deleteByKey(idTbl);
                        tblKasirList.remove(position);
                        kasirAdapter.notifyItemRemoved(position);
                        kasirAdapter.notifyItemRangeChanged(position, tblKasirList.size());

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

    private void tambahstok(long idTbl) {

        List<TblKasir> tblKasirList = kasirDao.queryBuilder().where(TblKasirDao.Properties.IdTblKasir.eq(idTbl)).list();

        for (int i = 0; i < tblKasirList.size(); i++){
            String kodeidx = tblKasirList.get(i).getKode_stok();
            int jumst = Integer.parseInt(tblKasirList.get(i).getJum_stok());
            //----
            List<TblStok> tblStok = daoSession.getTblStokDao().queryBuilder().where(TblStokDao.Properties.Kode_stok.eq(kodeidx)).list();
            for (int i2 = 0; i2 < tblStok.size(); i2++){
                Long idx = tblStok.get(i2).getIdTblStok();
                int jumstoks = Integer.parseInt(tblStok.get(i2).getJum_stok());
                TblStok tblStok2 = daoSession.getTblStokDao().load(idx);
                tblStok2.setJum_stok(String.valueOf(jumstoks+jumst));
                daoSession.getTblStokDao().update(tblStok2);
                Log.d("update",idx+"xx"+jumst+"zz"+jumst);
            }

        }

    }

    public void goselesai(View view) {
        String tun = String.valueOf(ettunai.getText());
        if(tun.equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.tunaiisi), Toast.LENGTH_SHORT).show();
            ettunai.setError(getString(R.string.tunaiisi));
        }else{
            SharedPreferences.Editor editorunik = pref.edit();
            editorunik.putString("tunai",tun);
            editorunik.putString("cek","1");
            editorunik.apply();//commit();

            Intent intent = new Intent(KasirActivity.this, KasirEndActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void gocaristok(View view) {
        rlkasir.setVisibility(View.VISIBLE);
        FunctionHelper.benner(this);
        listkasircari();
    }
    public void closekasir(View view) {
        rlkasir.setVisibility(View.GONE);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onView(int position) {
        String name = tblStokcariList.get(position).getKode_stok();
        barcodeReader.playBeep();
        final String x = String.valueOf(etkali.getText());
        tambahbarang(name, x);
        rlkasir.setVisibility(View.GONE);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return false;
    }

    @NonNull
    @Override
    public Iterator<TblStok> iterator() {
        return null;
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return null;
    }

    @Override
    public boolean add(TblStok tblStok) {
        return false;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return false;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends TblStok> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends TblStok> c) {
        return false;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public TblStok get(int index) {
        return null;
    }

    @Override
    public TblStok set(int index, TblStok element) {
        return null;
    }

    @Override
    public void add(int index, TblStok element) {

    }

    @Override
    public TblStok remove(int index) {
        return null;
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        return 0;
    }

    @NonNull
    @Override
    public ListIterator<TblStok> listIterator() {
        return null;
    }

    @NonNull
    @Override
    public ListIterator<TblStok> listIterator(int index) {
        return null;
    }

    @NonNull
    @Override
    public List<TblStok> subList(int fromIndex, int toIndex) {
        return null;
    }

    public void gokembali(View view) {
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        finish();
    }
}

