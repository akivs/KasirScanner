package club.akivs.kasirscanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import club.akivs.kasirscanner.utils.FunctionHelper;
import club.akivs.kasirscanner.utils.database.DaoHandler;
import club.akivs.kasirscanner.utils.database.DaoSession;
import club.akivs.kasirscanner.utils.database.TblKasir;
import club.akivs.kasirscanner.utils.database.TblKasirDao;

import static android.os.Environment.getExternalStorageDirectory;
import static java.io.File.separator;

public class TransaksiDetActivity extends AppCompatActivity implements KasirEndAdapter.KasirEndAdapterCallback {

    private String snama,salamat,stelepon,skasir,slogin;
    private TextView nmkasir,alamatkasir,telpkasir,tvtotal,tvtunai,tvkembali,tvno;
    private RecyclerView rvkasir;
    private List<TblKasir> tblKasirList;
    private TblKasirDao kasirDao;
    private KasirEndAdapter kasirAdapter;
    private DaoSession daoSession;
    private Bitmap b;
    private ScrollView z;
    private String kodeu;
    String folder_main = "KasirBarcode";
    private TextView tvadmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi_det);
        deklarasi();
        header();
        datakasir();
        goviewscreen();
        TextView kembali = (TextView) findViewById(R.id.kembali);
        ImageView ivkembali = (ImageView) findViewById(R.id.ivkembali);
        kembali.setVisibility(View.GONE);
        ivkembali.setVisibility(View.GONE);
    }
    private void goviewscreen() {
        nmkasir.setText(snama);
        alamatkasir.setText(salamat);
        telpkasir.setText(stelepon);

        rvkasir=(RecyclerView)findViewById(R.id.rvkasirend);

        // ButterKnife.bind(this);
        daoSession = DaoHandler.getInstance(this);
        kasirDao = daoSession.getTblKasirDao();
        //TblKasirDao.dropTable(daoSession.getDatabase(), true);
        //FunctionHelper.onUpgrade(1,1,this);
        TblKasirDao.createTable(daoSession.getDatabase(), true);

        Log.d("sqlllll", String.valueOf(kasirDao.getSession()));
        //tblKasirList = kasirDao.queryBuilder().list();

        tblKasirList = kasirDao.queryBuilder().where(TblKasirDao.Properties.Kode_kasir.eq(kodeu)).list();

        kasirAdapter = new KasirEndAdapter(tblKasirList, this);
        rvkasir.setLayoutManager(new LinearLayoutManager(this));
        rvkasir.setItemAnimator(new DefaultItemAnimator());
        rvkasir.setAdapter(kasirAdapter);
        kasirAdapter.notifyDataSetChanged();
        String tuna = tblKasirList.get(0).getTunai();
        //================
        Double tot = getTotal();
        Double tun = Double.valueOf(tuna);
        tvtotal.setText(FunctionHelper.convertRupiah(tot,this,getString(R.string.sp_profil)));
        tvtunai.setText(FunctionHelper.convertRupiah(tun,this,getString(R.string.sp_profil)));
        tvkembali.setText(FunctionHelper.convertRupiah(tun-tot,this,getString(R.string.sp_profil)));



    }

    private void deklarasi() {
        nmkasir = (TextView) findViewById(R.id.nmkasir);
        alamatkasir = (TextView) findViewById(R.id.alamatkasir);
        telpkasir = (TextView) findViewById(R.id.telpkasir);
        z = (ScrollView) findViewById(R.id.takescreen);
        tvtotal = (TextView) findViewById(R.id.tvtotal);
        tvtunai = (TextView) findViewById(R.id.tvtunai);
        tvkembali = (TextView) findViewById(R.id.tvkembali);
        tvno = (TextView) findViewById(R.id.tvno);
        tvadmin = (TextView) findViewById(R.id.tvadmin);
    }

    private void datakasir() {

        Bundle extras = getIntent().getExtras();
        kodeu = extras.getString("idfx");
        //===========
        tvno.setText("No. : KB/"+kodeu);
        tvadmin.setText("Adm : "+skasir);

        // List<TblKasir> tblkas = daoSession.getTblKasirDao().queryBuilder()
        //        .where(TblKasirDao.Properties.Kode_kasir.eq(kodeu)).list();
        //Log.d("struk", String.valueOf(tblkas));

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
        headmenu.setText(getString(R.string.mntrans));
        headtoko.setText(snama);

        //====

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void gomenu(View view) {
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        finish();
    }

    public void gokasir(View view) {
        startActivity(new Intent(getApplicationContext(), KasirActivity.class));
        finish();
    }

    @Override
    public void onLongClick(int position) {

    }

    @Override
    public void onDelete(int position) {

    }

    public void gobagikan(View view) {
        getscrenn();
        String mainpath = getExternalStorageDirectory() + separator + folder_main + separator ;
        String path = null;//URLEncoder.encode(kodeu, "UTF-8")
        String r = kodeu.replace(" ", "").replace(":", "").replace("-", "");
        path = mainpath + "note_" + r + ".png";

        //

        Intent shareIntent = new Intent();
        shareIntent.setType("image/*");
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        //shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
        //Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT).show();
        Log.d("filesssss",path);

    }

    public void godownload(View view) {
        getscrenn();
        String mainpath = getExternalStorageDirectory() + separator + folder_main + separator ;
        String path = null;//URLEncoder.encode(kodeu, "UTF-8")
        String r = kodeu.replace(" ", "").replace(":", "").replace("-", "");
        path = mainpath + "note_" + r + ".png";
        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        photoPrinter.printBitmap(r+"print", bitmap);
    }

    private void getscrenn(){

        View u = findViewById(R.id.takescreen);
        u.setDrawingCacheEnabled(true);

        int totalHeight = z.getChildAt(0).getHeight();
        int totalWidth = z.getChildAt(0).getWidth();
        u.layout(0, 0, totalWidth, totalHeight);
        u.buildDrawingCache(true);
        b = Bitmap.createBitmap(u.getDrawingCache());
        u.setDrawingCacheEnabled(false);

        File f = new File(getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }
        String mainpath = getExternalStorageDirectory() + separator + folder_main + separator ;
        String path = null;//
        String r = kodeu.replace(" ", "").replace(":", "").replace("-", "");
        path = mainpath + "note_" + r + ".png";

        //Save bitmap
        //File mydir = this.getDir("KasirScanner", Context.MODE_PRIVATE); //Creating an internal dir;
        //File fileWithinMyDir = new File(mydir, "KasirScanner"); //Getting a file within the dir.
        //String extr = getExternalStorageDirectory().toString() +   separator + fileWithinMyDir;
        //String fileName = new SimpleDateFormat("yyyyMMddhhmm'_report.jpg'").format(new Date());
        File myPath = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), b, "Screen", "screen");
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_gambar))
                    .setMessage(path)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, null)
                    .show();
        }catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
}
