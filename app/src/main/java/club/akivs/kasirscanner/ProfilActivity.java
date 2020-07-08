package club.akivs.kasirscanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import club.akivs.kasirscanner.utils.FunctionHelper;
import club.akivs.kasirscanner.utils.LocaleHelper;

public class ProfilActivity extends AppCompatActivity {


    private View rlprofil;
    private EditText etnama,etalamat,ettelepon,etkasir;
    private SharedPreferences pref;
    private String snama,salamat,stelepon,skasir,slogin;
    private TextView tvnama,tvalamat,tvtelepon,tvkasir,tvuang,tvuang2;
    private AlertDialog.Builder builderSingle;
    private EditText etuang;
    private String snm_uang,sid_uang;
    private RelativeLayout rlmanual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        header();
        deklarasi();  FunctionHelper.benner(this);
        builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.kasirlogo);
        builderSingle.setTitle(getString(R.string.doalog_pil));
    }

    private void deklarasi() {
        rlmanual=(RelativeLayout)findViewById(R.id.rlmanual);
        rlprofil=(RelativeLayout)findViewById(R.id.rlprofil);
        etnama=(EditText)findViewById(R.id.etnama);
        etalamat=(EditText)findViewById(R.id.etalamat);
        ettelepon=(EditText)findViewById(R.id.ettelepon);
        etkasir=(EditText)findViewById(R.id.etkasir);
        tvnama=(TextView)findViewById(R.id.tvnama);
        tvalamat=(TextView)findViewById(R.id.tvalamat);
        tvtelepon=(TextView)findViewById(R.id.tvtelepon);
        tvkasir=(TextView)findViewById(R.id.tvkasir);
        etuang=(EditText)findViewById(R.id.etuang);
        tvuang=(TextView)findViewById(R.id.tvuang);
        tvuang2=(TextView)findViewById(R.id.tvuang2);


        pref = getSharedPreferences(getString(R.string.sp_profil), MODE_PRIVATE);

        etnama.setText(snama);
        etalamat.setText(salamat);
        ettelepon.setText(stelepon);
        etkasir.setText(skasir);
        etuang.setText(snm_uang);
        tvuang.setText(sid_uang);

        if(slogin.equals("1")){
            tvnama.setText(snama);
            tvalamat.setText(salamat);
            tvtelepon.setText(stelepon);
            tvkasir.setText(skasir);
            tvuang2.setText(snm_uang);
        }
    }

    private void header() {

        SharedPreferences prefx = this.getSharedPreferences(getString(R.string.sp_profil), Context.MODE_PRIVATE);
         snama = prefx.getString("nama", "");
         salamat = prefx.getString("alamat", "");
         stelepon = prefx.getString("tlp", "");
        skasir = prefx.getString("kasir", "");
        snm_uang = prefx.getString("nm_uang", "");
        sid_uang = prefx.getString("id_uang", "");
         slogin = prefx.getString("login", "");

        TextView headmenu = (TextView) findViewById(R.id.headmenu);
        TextView headtoko = (TextView) findViewById(R.id.headtoko);
        headmenu.setText(getString(R.string.mnprofil));
        headtoko.setText(snama);

    }


    public void goeditprofil(View view) {
        rlprofil.setVisibility(View.VISIBLE);
    }

    public void closeprofil(View view) {
        rlprofil.setVisibility(View.GONE);
        rlmanual.setVisibility(View.GONE);
    }

    public void saveprofil(View view) {

        final String gnama= String.valueOf(etnama.getText());
        final String galamat= String.valueOf(etalamat.getText());
        final String gtelepon= String.valueOf(ettelepon.getText());
        final String gkasir= String.valueOf(etkasir.getText());
        final String getuang= String.valueOf(etuang.getText());
        final String gtvuang= String.valueOf(tvuang.getText());

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_profil))
                .setMessage(getString(R.string.dialog_ask))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        if(gnama.equals("") || galamat.equals("") || gtelepon.equals("") || gkasir.equals("") ){
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.dialog_null), Toast.LENGTH_LONG).show();
                            if(gnama.equals("")){
                                etnama.setError(getString(R.string.er_nama));
                            }
                            if(galamat.equals("")){
                                etalamat.setError(getString(R.string.er_alamat));
                            }
                            if(gtelepon.equals("")){
                                ettelepon.setError(getString(R.string.er_telepon));
                            }
                            if(gkasir.equals("")){
                                etkasir.setError(getString(R.string.er_Kasir));
                            }
                        }else{
                            simpanprofil(gnama,galamat,gtelepon,gkasir,getuang,gtvuang);
                            rlprofil.setVisibility(View.GONE);
                        }


                    }

                    private void simpanprofil(String gnama, String galamat, String gtelepon,String gkasir ,String getuang ,String gtvuang) {

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("nama",gnama);
                        editor.putString("alamat", galamat);
                        editor.putString("tlp",gtelepon);
                        editor.putString("kasir",gkasir);
                        editor.putString("nm_uang",getuang);
                        editor.putString("id_uang",gtvuang);
                        editor.putString("login", "1");
                        editor.commit();

                        Intent intent = new Intent(ProfilActivity.this, ProfilActivity.class);
                        startActivity(intent);
                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, null).show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        finish();
    }

    public void kembali(View view) {
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        finish();
    }

    public void gouang(View view) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        final ArrayAdapter<String> arrayId = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);

        arrayAdapter.add("Indonesia");
        arrayAdapter.add("Japan");
        arrayAdapter.add("United States of America");
        arrayAdapter.add("China");
        arrayAdapter.add("European Union");
        arrayId.add("id|ID");
        arrayId.add("jp|JP");
        arrayId.add("us|US");
        arrayId.add("cn|CN");
        arrayId.add("eu|EU");


        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                String strId = arrayId.getItem(which);
                etuang.setText(strName);
                tvuang.setText(strId);

            }
        });
        builderSingle.show();
    }

    public void gorate(View view) {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void gobagi(View view) {

        Intent shareIntent = new Intent();
        shareIntent.setType("text/plain");
        //shareIntent.setType("image/jpeg");
        shareIntent.setAction(Intent.ACTION_SEND);
        //shareIntent.putExtra(Intent.EXTRA_STREAM, android.R.drawable.ic_dialog_alert);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "KasirBarcode " + "https://play.google.com/store/apps/details?id=club.akivs.kasirscanner");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));

    }

    public void gomanual(View view) {
        rlmanual.setVisibility(View.VISIBLE);
    }

    public void gokembali(View view) {
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        finish();
    }

    public void gobahasa(View view) {
        String mLanguageCode = "id";
        LocaleHelper.setLocale(ProfilActivity.this, mLanguageCode);

        //It is required to recreate the activity to reflect the change in UI.
        recreate();
    }
}
