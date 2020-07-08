package club.akivs.kasirscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.greendao.query.WhereCondition;

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
import club.akivs.kasirscanner.utils.database.TblStokDao;

public class TransaksiActivity extends AppCompatActivity implements TransaksiAdapter.TransaksiAdapterCallback{

    private RecyclerView rvTrans;
    private DaoSession daoSession;
    private TblKasirDao transDao;
    private List<TblKasir> tblTransList;
    private TransaksiAdapter transAdapter;
    private EditText etcari;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);

        FunctionHelper.benner(this);
        //header();
        FunctionHelper.header(this,getString(R.string.mntrans),getString(R.string.sp_profil));
        deklarasi();
        listdao();
        etcari = (EditText) findViewById(R.id.etcari);
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
    private void listdao() {
        rvTrans=(RecyclerView)findViewById(R.id.rvTrans);

        // ButterKnife.bind(this);
        daoSession = DaoHandler.getInstance(this);
        transDao = daoSession.getTblKasirDao();
        TblKasirDao.createTable(daoSession.getDatabase(), true);

        tblTransList = transDao.queryBuilder()
                .where(new WhereCondition.StringCondition( "1 GROUP BY kode_kasir"))
                .orderDesc(TblKasirDao.Properties.Tanggal).build().list();
        Log.d("sqlllll", String.valueOf(transDao.getTablename().isEmpty()));
        transAdapter = new TransaksiAdapter(tblTransList, this);
        rvTrans.setLayoutManager(new LinearLayoutManager(this));
        rvTrans.setItemAnimator(new DefaultItemAnimator());
        rvTrans.setAdapter(transAdapter);
        transAdapter.notifyDataSetChanged();

        //tvkode.setText(ge);
        /* tvkode.setText(FunctionHelper.convertRupiah(getTotal()));
        tvkode.setText(FunctionHelper.convertRupiah(getTotal()));
        tvkode.setText(FunctionHelper.convertRupiah(getTotal()));*/


    }

    private void filter(String text) {
        ArrayList<TblKasir> filteredList = new ArrayList<>();
        for (TblKasir item : tblTransList) {
            if (item.getNama_kasir().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }else if (item.getTanggal().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        transAdapter.filterList(filteredList);
    }

    private void deklarasi() {



    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        finish();
    }

    @Override
    public void onLongClick(int position) {
        String id = tblTransList.get(position).getKode_kasir();
        Intent i = new Intent(TransaksiActivity.this, TransaksiDetActivity.class);
        i.putExtra("idfx", id);
        startActivity(i);
    }

    @Override
    public void onDelete(int position) {
        String name = tblTransList.get(position).getKode_kasir();
        showDialogDelete(position, name);
    }

    @Override
    public void onView(int position) {

    }

    private void showDialogDelete(final int position, String name){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(TransaksiActivity.this);
        builder1.setMessage(getString(R.string.hapus)+ name + " ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*
                        Fungsi delete suatu data bedasarkan idnya.
                         */
                        String idTbl = tblTransList.get(position).getKode_kasir();
                        //daoSession.getTblKasirDao().deleteByKey(Long.valueOf(idTbl));
                        daoSession.getTblKasirDao().queryBuilder()
                                .where(TblKasirDao.Properties.Kode_kasir.eq(idTbl))
                                .buildDelete().executeDeleteWithoutDetachingEntities();
                        daoSession.clear();

                        tblTransList.remove(position);
                        transAdapter.notifyItemRemoved(position);
                        transAdapter.notifyItemRangeChanged(position, tblTransList.size());

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
    public Iterator<TblKasir> iterator() {
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
    public boolean add(TblKasir tblKasir) {
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
    public boolean addAll(@NonNull Collection<? extends TblKasir> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends TblKasir> c) {
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
    public TblKasir get(int index) {
        return null;
    }

    @Override
    public TblKasir set(int index, TblKasir element) {
        return null;
    }

    @Override
    public void add(int index, TblKasir element) {

    }

    @Override
    public TblKasir remove(int index) {
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
    public ListIterator<TblKasir> listIterator() {
        return null;
    }

    @NonNull
    @Override
    public ListIterator<TblKasir> listIterator(int index) {
        return null;
    }

    @NonNull
    @Override
    public List<TblKasir> subList(int fromIndex, int toIndex) {
        return null;
    }

    public void gokembali(View view) {
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        finish();
    }
}
