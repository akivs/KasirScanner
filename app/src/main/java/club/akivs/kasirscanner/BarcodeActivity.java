package club.akivs.kasirscanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import club.akivs.kasirscanner.utils.FunctionHelper;
import club.akivs.kasirscanner.utils.database.DaoHandler;
import club.akivs.kasirscanner.utils.database.DaoSession;
import club.akivs.kasirscanner.utils.database.TblBarcode;
import club.akivs.kasirscanner.utils.database.TblBarcodeDao;
import club.akivs.kasirscanner.utils.database.TblStokDao;

import static android.graphics.Color.WHITE;
import static android.os.Environment.getExternalStorageDirectory;
import static java.io.File.separator;

public class BarcodeActivity extends AppCompatActivity implements BarcodeAdapter.BarcodeAdapterCallback {

    private DaoSession daoSession;
    private TblBarcodeDao barcodeDao;
    private RecyclerView rvBarcode;
    private List<TblBarcode> tblBarcodeList;
    private BarcodeAdapter barcodeAdapter;
    private FloatingActionButton fabAddStok;
    private RelativeLayout rlbarcode;
    private ImageView imageViewBitmap;
    private ImageView imageViewBitmap2;
    String folder_main = "KasirBarcode";
    private TextView tvkodetemp;
    private Bitmap b;
    private ScrollView z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        FunctionHelper.benner(this);
        //header();
        FunctionHelper.header(this,getString(R.string.mnbarcode),getString(R.string.sp_profil));
        deklarasi();
        listdao();

    }

    private void barkode(String id) {

        imageViewBitmap=(ImageView)findViewById(R.id.ivcode);
        imageViewBitmap2=(ImageView)findViewById(R.id.ivcode2);
        //setting size of qr code
        int width =400;
        int height = 400;
        int smallestDimension = width < height ? width : height;

        //setting size of bar code
        int width2 =200;
        int height2 = 400;
        int smallestDimension2 = width2 < height2 ? width2 : height2;

        //EditText editText=(EditText)findViewById(R.id.editText) ;
        //String qrCodeData = editText.getText().toString();
        //setting parameters for qr code
        String charset = "UTF-8";
        Map<EncodeHintType, ErrorCorrectionLevel> hintMap =new HashMap<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        CreateQRCode(id, charset, hintMap, smallestDimension, smallestDimension);
        CreateBarCode(id, charset, hintMap, smallestDimension2, smallestDimension2);
    }

    private void listdao() {
        rvBarcode=(RecyclerView)findViewById(R.id.rvBarcode);

        // ButterKnife.bind(this);
        daoSession = DaoHandler.getInstance(this);
        barcodeDao = daoSession.getTblBarcodeDao();
        TblBarcodeDao.createTable(daoSession.getDatabase(), true);
        tblBarcodeList = barcodeDao.queryBuilder().list();
        Log.d("sqlllll", String.valueOf(barcodeDao.getTablename().isEmpty()));
        barcodeAdapter = new BarcodeAdapter(tblBarcodeList, this);
        rvBarcode.setLayoutManager(new LinearLayoutManager(this));
        rvBarcode.setItemAnimator(new DefaultItemAnimator());
        rvBarcode.setAdapter(barcodeAdapter);
        barcodeAdapter.notifyDataSetChanged();

        //tvkode.setText(ge);
        /* tvkode.setText(FunctionHelper.convertRupiah(getTotal()));
        tvkode.setText(FunctionHelper.convertRupiah(getTotal()));
        tvkode.setText(FunctionHelper.convertRupiah(getTotal()));*/

        fabAddStok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BarcodeActivity.this, BarcodeAddActivity.class));
            }
        });
    }

    private void deklarasi() {

        /*tvkode=(TextView)findViewById(R.id.tvkode);
        tvnamastok=(TextView)findViewById(R.id.tvnamastok);
        tvjumstok=(TextView)findViewById(R.id.tvjumstok);
        tvhargastok=(TextView)findViewById(R.id.tvhargastok);*/
        tvkodetemp=(TextView)findViewById(R.id.tvkodetemp);
        rlbarcode=(RelativeLayout)findViewById(R.id.rlbarcode);
        fabAddStok=(FloatingActionButton)findViewById(R.id.fabAddStok);
        z = (ScrollView) findViewById(R.id.takescreen);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        finish();
    }

    public void closebarcode(View view) {
        rlbarcode.setVisibility(View.GONE);
    }

    @Override
    public void onLongClick(int position) {
        rlbarcode.setVisibility(View.VISIBLE);
        String id = tblBarcodeList.get(position).getKode();
        barkode(id);
        tvkodetemp.setText(id);
    }

    @Override
    public void onDelete(int position) {
        String name = tblBarcodeList.get(position).getKode();
        showDialogDelete(position, name);
    }

    private void showDialogDelete(final int position, String name){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(BarcodeActivity.this);
        builder1.setMessage(getString(R.string.hapus)+ name + " ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*
                        Fungsi delete suatu data bedasarkan idnya.
                         */
                        long idTbl = tblBarcodeList.get(position).getIdTblBarcode();
                        daoSession.getTblBarcodeDao().deleteByKey(idTbl);

                        tblBarcodeList.remove(position);
                        barcodeAdapter.notifyItemRemoved(position);
                        barcodeAdapter.notifyItemRangeChanged(position, tblBarcodeList.size());

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

    public  void CreateQRCode(String qrCodeData, String charset, Map hintMap, int qrCodeheight, int qrCodewidth){


        try {
            //generating qr code in bitmatrix type
            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
            //converting bitmatrix to bitmap

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    //pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                    pixels[offset + x] = matrix.get(x, y) ?
                            ResourcesCompat.getColor(getResources(),R.color.black,null) :WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            //setting bitmap to image view

            Bitmap overlay = BitmapFactory.decodeResource(getResources(), R.drawable.kasirlogo);

            //imageViewBitmap2.setImageBitmap(mergeBitmaps(overlay,bitmap));
            imageViewBitmap2.setImageBitmap(bitmap);

        }catch (Exception er){
            Log.e("QrGenerate",er.getMessage());
        }
    }

    public  void CreateBarCode(String qrCodeData, String charset, Map hintMap, int qrCodeheight, int qrCodewidth){


        try {
            //generating qr code in bitmatrix type
            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
                    BarcodeFormat.CODABAR, 600, qrCodeheight, hintMap);
            //converting bitmatrix to bitmap

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    //pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                    pixels[offset + x] = matrix.get(x, y) ?
                            ResourcesCompat.getColor(getResources(),R.color.black,null) :WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            //setting bitmap to image view

            Bitmap overlay = BitmapFactory.decodeResource(getResources(), R.drawable.kasirlogo);

           //imageViewBitmap.setImageBitmap(mergeBitmaps(overlay,bitmap));
            imageViewBitmap.setImageBitmap(bitmap);

        }catch (Exception er){
            Log.e("QrGenerate",er.getMessage());
        }
    }



    public Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth  - overlay.getWidth()) /3;
        int centreY = (canvasHeight - overlay.getHeight()) /3 ;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        return combined;
    }

    public void gobagikan(View view) {
        String idf = String.valueOf(tvkodetemp.getText());
        getscrenn();
        String mainpath = getExternalStorageDirectory() + separator + folder_main + separator ;
        String path = mainpath + "code_" + idf + ".png";
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
    }

    private void getscrenn() {
        String idf = String.valueOf(tvkodetemp.getText());

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
        String path = mainpath + "code_" + idf + ".png";

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

    public void gokembali(View view) {
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        finish();
    }
}
