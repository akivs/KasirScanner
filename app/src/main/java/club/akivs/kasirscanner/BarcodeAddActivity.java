package club.akivs.kasirscanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import club.akivs.kasirscanner.utils.FunctionHelper;
import club.akivs.kasirscanner.utils.database.DaoHandler;
import club.akivs.kasirscanner.utils.database.DaoSession;
import club.akivs.kasirscanner.utils.database.TblBarcode;

public class BarcodeAddActivity extends AppCompatActivity {

    private EditText etstokkode,etstoknama,etstokjum;
    private Button btnsave;
    private DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_add);
        //FunctionHelper.benner(this);
        //header();
        FunctionHelper.header(this,getString(R.string.mnaddbarcode),getString(R.string.sp_profil));
        etstokkode = (EditText) findViewById(R.id.etstokkode);
        etstoknama = (EditText) findViewById(R.id.etstoknama);
        etstokjum = (EditText) findViewById(R.id.etstokket);
        btnsave = (Button) findViewById(R.id.btnsave);

        daoSession = DaoHandler.getInstance(this);
    }

    public void savebarcode(View view) {
        final String gstoknama= String.valueOf(etstoknama.getText());
        final String gstokkode= String.valueOf(etstokkode.getText());
        final String gstokjum= String.valueOf(etstokjum.getText());


        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_barcode))
                .setMessage(getString(R.string.dialog_ask))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        if(gstoknama.equals("") || gstokkode.equals("") || gstokjum.equals("") ){
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

                        }else{
                            simpanbarcode(gstoknama,gstokkode,gstokjum);
                        }

                    }


                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void simpanbarcode(String gstoknama, String gstokkode, String gstokjum) {

        TblBarcode tblbarcode= new TblBarcode();
        tblbarcode.setKode(gstokkode);
        tblbarcode.setNama(gstoknama);
        tblbarcode.setKet(gstokjum);
        tblbarcode.setTanggal(FunctionHelper.tanggalnow());
        daoSession.getTblBarcodeDao().insert(tblbarcode);

        Toast.makeText(BarcodeAddActivity.this, getString(R.string.dialog_berhasil),
                Toast.LENGTH_SHORT).show();
        startActivity(new Intent(BarcodeAddActivity.this, BarcodeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();

    }
}
