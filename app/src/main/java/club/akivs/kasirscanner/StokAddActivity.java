package club.akivs.kasirscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import club.akivs.kasirscanner.edit.EditDialogFragment;
import club.akivs.kasirscanner.home.HomeActivity;
import club.akivs.kasirscanner.utils.FunctionHelper;
import club.akivs.kasirscanner.utils.database.DaoHandler;
import club.akivs.kasirscanner.utils.database.DaoSession;
import club.akivs.kasirscanner.utils.database.TblPengeluaran;
import club.akivs.kasirscanner.utils.database.TblStok;
import club.akivs.kasirscanner.utils.database.TblStokDao;
import info.androidhive.barcode.BarcodeReader;

public class StokAddActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {


    private BarcodeDetector barcodedet;
    BarcodeReader barcodeReader;
    private EditText etstokkode,etstoknama,etstokjum,etstokharga,etKet,etstokhargabeli;
    private DaoSession daoSession;
    private Button btnsave;
    private RelativeLayout rltap;
    private Button btkamera;
    private ImageView ivkamera;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stok_add);
        etstokkode = (EditText) findViewById(R.id.etstokkode);
        etstoknama = (EditText) findViewById(R.id.etstoknama);
        etstokjum = (EditText) findViewById(R.id.etstokjum);
        etstokharga = (EditText) findViewById(R.id.etstokharga);
        etKet = (EditText) findViewById(R.id.etKet);
        etstokhargabeli = (EditText) findViewById(R.id.etstokhargabeli);
        btnsave = (Button) findViewById(R.id.btnsave);
        rltap = (RelativeLayout) findViewById(R.id.rltap);
        btkamera = (Button) findViewById(R.id.btkamera);
        ivkamera = (ImageView) findViewById(R.id.ivkamera);

        btkamera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FunctionHelper.checkPermissions(StokAddActivity.this,StokAddActivity.this);
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        daoSession = DaoHandler.getInstance(this);
        kodescanner();
        //header();
        FunctionHelper.header(this,getString(R.string.mnaddstok),getString(R.string.sp_profil));

        etstokkode.addTextChangedListener(new TextWatcher() {
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
                String aa = FunctionHelper.cekstokganda(s.toString(), StokAddActivity.this);
                if(!aa.equals("[]")){
                    Toast.makeText(getApplicationContext(), getString(R.string.dialog_stokganda), Toast.LENGTH_SHORT).show();
                    btnsave.setVisibility(View.GONE);
                }else{
                    btnsave.setVisibility(View.VISIBLE);
                }
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });

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

        rltap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeReader.resumeScanning();
                rltap.setVisibility(View.GONE);
            }
        });
   }


    private void header() {

        SharedPreferences prefx = this.getSharedPreferences(getString(R.string.sp_profil), Context.MODE_PRIVATE);
        String nama = prefx.getString("nama", "");


        TextView headmenu = (TextView) findViewById(R.id.headmenu);
        TextView headtoko = (TextView) findViewById(R.id.headtoko);
        headmenu.setText(getString(R.string.mnaddstok));
        headtoko.setText(nama);
    }

    public void savestok(View view) {

        final String gstoknama= String.valueOf(etstoknama.getText());
        final String gstokkode= String.valueOf(etstokkode.getText());
        final String gstokjum= String.valueOf(etstokjum.getText());
        final String gstokharga= String.valueOf(etstokharga.getText());
        final String gket= String.valueOf(etKet.getText());
        final String gstokhargabeli= String.valueOf(etstokhargabeli.getText());

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_stok))
                .setMessage(getString(R.string.dialog_ask))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        if(gstoknama.equals("") || gstokkode.equals("") || gstokjum.equals("") || gstokharga.equals("")){
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.dialog_null), Toast.LENGTH_LONG).show();
                            if(gstoknama.equals("")){
                                etstoknama.setError(getString(R.string.dialog_null));
                            }
                            if(gstokkode.equals("")){
                                etstokkode.setError(getString(R.string.dialog_null));
                            }
                            if(gstokjum.equals("")){
                                etstokjum.setError(getString(R.string.dialog_null));
                            }
                            if(gstokharga.equals("")){
                                etstokharga.setError(getString(R.string.dialog_null));
                            }
                        }else{
                            simpanstok(gstoknama,gstokkode,gstokjum,gstokharga,gket,gstokhargabeli);
                        }

                    }


                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void simpanstok(String gstoknama, String gstokkode, String gstokjum, String gstokharga, String gket , String gstokhargabeli) {
        TblStok tblstok = new TblStok();
        tblstok.setKode_stok(gstokkode);
        tblstok.setNama_stok(gstoknama);
        tblstok.setJum_stok(gstokjum);
        tblstok.setKet_stok(gket);
        tblstok.setHarga(gstokharga);
        tblstok.setValue1(gstokhargabeli);
        tblstok.setTanggal(FunctionHelper.tanggalnow());
        daoSession.getTblStokDao().insert(tblstok);

        BitmapDrawable drawable = (BitmapDrawable) ivkamera.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        //MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "_stok"+gstokkode , gket);

        //FunctionHelper.insertImage(getContentResolver(), bitmap, "_stok"+gstokkode , gket);
        try {
            FunctionHelper.saveImage(bitmap,"_stok"+gstokkode, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(StokAddActivity.this, getString(R.string.dialog_berhasil),
                Toast.LENGTH_SHORT).show();
        startActivity(new Intent(StokAddActivity.this, StokActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    @Override
    public void onScanned(final Barcode barcode) {
        Log.e("xxx", "onScanned: " + barcode.displayValue);
        barcodeReader.playBeep();
        //=======

        //======
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Barcode: " + barcode.displayValue, Toast.LENGTH_SHORT).show();
                etstokkode.setText(barcode.displayValue);
                barcodeReader.pauseScanning();
                rltap.setVisibility(View.VISIBLE);
                String aa = FunctionHelper.cekstokganda(barcode.displayValue, StokAddActivity.this);
                if(!aa.equals("[]")){
                    Toast.makeText(getApplicationContext(), getString(R.string.dialog_stokganda), Toast.LENGTH_SHORT).show();
                    btnsave.setVisibility(View.GONE);
                }else{
                    btnsave.setVisibility(View.VISIBLE);
                }
            }
        });

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivkamera.setImageBitmap(photo);
        }
    }

    public void gokembali(View view) {
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        finish();
    }

}
