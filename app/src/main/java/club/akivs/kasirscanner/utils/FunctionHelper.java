package club.akivs.kasirscanner.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import club.akivs.kasirscanner.Kasir;
import club.akivs.kasirscanner.R;
import club.akivs.kasirscanner.utils.database.DaoHandler;
import club.akivs.kasirscanner.utils.database.DaoMaster;
import club.akivs.kasirscanner.utils.database.DaoSession;
import club.akivs.kasirscanner.utils.database.TblKasir;
import club.akivs.kasirscanner.utils.database.TblKasirDao;
import club.akivs.kasirscanner.utils.database.TblStok;
import club.akivs.kasirscanner.utils.database.TblStokDao;

import static android.os.Environment.getExternalStorageDirectory;
import static java.io.File.separator;

public class FunctionHelper {

    private static String snama,salamat,stelepon,slogin;



    public static String convertRupiah(Double nominal, Activity acv, String stg){
        SharedPreferences prefx = acv.getSharedPreferences(stg, Context.MODE_PRIVATE);
        String snm_uang = prefx.getString("nm_uang", "");
        String sid_uang = prefx.getString("id_uang", "");

        String currentString = sid_uang;
        String[] separated = currentString.split("\\|");
        Locale localeID = new Locale(separated[0], separated[1]);
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(nominal);
    }
    private static final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";
    public static String tanggalnow(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_2);
        dateFormat.setTimeZone(TimeZone.getDefault());//getTimeZone("UTC")
        Date today = Calendar.getInstance().getTime();

        return dateFormat.format(today);
    }
    public static int randomnum(){
        final int min = 1000;
        final int max = 9999;
        final int random = new Random().nextInt((max - min) + 1) + min;

        return random;
    }
    public static String cekstokganda(String valu, Context cont){
        DaoSession daoSession = DaoHandler.getInstance(cont);
        TblStokDao stokDao = daoSession.getTblStokDao();
        QueryBuilder<TblStok> tblStokList = stokDao.queryBuilder().where(TblStokDao.Properties.Kode_stok.eq(valu));
        List list = tblStokList.list();
        Log.d("cari stok", String.valueOf(list));
        return String.valueOf(list);
    }

    public static Bitmap ambilgambar(String kode){
        String folder_main = "KasirBarcode";
        String mainpath = getExternalStorageDirectory() + separator + folder_main + separator ;
        String path = mainpath + "_stok" + kode + ".png";
        Bitmap bit = null;
        try {
            bit = BitmapFactory.decodeStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bit;
    }

    public static List<TblStok> caristok(String valu, Context cont){
        DaoSession daoSession = DaoHandler.getInstance(cont);
        TblStokDao stokDao = daoSession.getTblStokDao();
        QueryBuilder<TblStok> tblStokList = stokDao.queryBuilder().where(TblStokDao.Properties.Kode_stok.eq(valu));
        List list = tblStokList.list();

        Log.d("cek stok", String.valueOf(list));
        return list;
    }
    public static String kasir(String valu, Context cont){
        DaoSession daoSession = DaoHandler.getInstance(cont);
        TblStokDao stokDao = daoSession.getTblStokDao();
        QueryBuilder<TblStok> tblStokList = stokDao.queryBuilder().where(TblStokDao.Properties.Kode_stok.eq(valu));
        List list = tblStokList.list();
        Log.d("cek stok", String.valueOf(list));
        return String.valueOf(list);
    }
    public static void checkPermissions(Context context , Activity act){

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                //android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
        };

        if(!hasPermissions(context, PERMISSIONS)){
            ActivityCompat.requestPermissions(act, PERMISSIONS, PERMISSION_ALL);
        }

    }
    public static void benner( Activity act){

         //MobileAds.initialize(getApplicationContext(), "ca-app-pub-3750495824091468~2802287659");
        List<String> testDeviceIds = Arrays.asList("33BE2250B43518CCDA7DE426D04EE231");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
       /* MobileAds.initialize(act, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });*/
        AdView mAdView = (AdView) act.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }
    public static void header( Activity act, String str, String pro){

        SharedPreferences prefx = act.getSharedPreferences(pro, Context.MODE_PRIVATE);
        snama = prefx.getString("nama", "");
        salamat = prefx.getString("alamat", "");
        stelepon = prefx.getString("tlp", "");
        slogin = prefx.getString("login", "");

        TextView headmenu = (TextView) act.findViewById(R.id.headmenu);
        TextView headtoko = (TextView) act.findViewById(R.id.headtoko);
        headmenu.setText(str);
        headtoko.setText(snama);

    }
    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @SuppressLint("SQLiteString")
    public static void onUpgrade( int oldVersion, int newVersion, Context cont) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(cont, "kasirscanner_db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoSession daoSession = DaoHandler.getInstance(cont);
        TblKasirDao kasirDao = daoSession.getTblKasirDao();
        Log.i("update tabel", "Update Schema to version: "+Integer.toString(oldVersion)+"->"+Integer.toString(newVersion));
        switch (oldVersion) {
            case 1:
                /* v1->v2: all changes made in version 2 come here */
                db.execSQL("ALTER TABLE "+kasirDao.TABLENAME+" ADD COLUMN 'KODE_STOK' STRING;");
                db.execSQL("DROP TABLE IF EXISTS 'MY_OLD_ENTITY'");
                /* break was omitted by purpose. */
            case 2:
                /* v2->v3: all changes made in version 3 come here */
                TblKasirDao.createTable(daoSession.getDatabase(), true);
                db.execSQL("ALTER TABLE "+kasirDao.TABLENAME+" ADD COLUMN 'NEW_COL_2' TEXT;");
                /* break was omitted by purpose. */
        }
    }

    public static final String insertImage(ContentResolver cr,
                                           Bitmap source,
                                           String title,
                                           String description) {
        String folder_main = "KasirBarcode";
        File f = new File(getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }
        String mainpath = getExternalStorageDirectory() + separator + folder_main + separator ;
        String path = null;//

        path = mainpath + "note_" + title + ".png";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(Uri.parse(path), values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.PNG, 50, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    /**
     * A copy of the Android internals StoreThumbnail method, it used with the insertImage to
     * populate the android.provider.MediaStore.Images.Media#insertImage with all the correct
     * meta data. The StoreThumbnail method is private so it must be duplicated here.
     * @see android.provider.MediaStore.Images.Media (StoreThumbnail private method)
     */
    private static final Bitmap storeThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width,
            float height,
            int kind) {

        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND,kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID,(int)id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT,thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH,thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    public static void saveImage(Bitmap bitmap, @NonNull String name, Context cont) throws IOException {
        boolean saved;
        OutputStream fos;
        String folder_main = "KasirBarcode";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = cont.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,  folder_main);//"DCIM/" +
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStorageDirectory().toString() + File.separator + folder_main;

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, name + ".png");
            fos = new FileOutputStream(image);

        }

        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }

}
