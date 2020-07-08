package club.akivs.kasirscanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.akivs.kasirscanner.home.HomeActivity;
import club.akivs.kasirscanner.utils.FunctionHelper;
import club.akivs.kasirscanner.utils.database.DaoHandler;
import club.akivs.kasirscanner.utils.database.DaoSession;
import club.akivs.kasirscanner.utils.database.TblKasir;
import club.akivs.kasirscanner.utils.database.TblKasirDao;
import club.akivs.kasirscanner.utils.database.TblStok;
import club.akivs.kasirscanner.utils.database.TblStokDao;
import eu.dkaratzas.android.inapp.update.Constants;
import eu.dkaratzas.android.inapp.update.InAppUpdateManager;
import eu.dkaratzas.android.inapp.update.InAppUpdateStatus;

import static android.os.Environment.getExternalStorageDirectory;

public class MenuActivity extends AppCompatActivity  {

    private static final int MY_REQUEST_CODE = 0;
    private String snama,salamat,stelepon,slogin,skasir;
    private DaoSession daoSession;
    private RelativeLayout rlmanual;
    private AppUpdateManager appUpdateManager;
    private int HIGH_PRIORITY_UPDATE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        String folder_main = "KasirBarcode";
        File f = new File(getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }
        //cekupdate();
        rlmanual=(RelativeLayout)findViewById(R.id.rlmanual);
        FunctionHelper.checkPermissions(this,this);
        header(); deklarasi(); FunctionHelper.benner(this);
        hapustemp();



    }

    private void hapustemp() {
        daoSession = DaoHandler.getInstance(this);
        //===update stok
        TblKasirDao kasirDao = daoSession.getTblKasirDao();
        List<TblKasir> tblKasirList = kasirDao.queryBuilder().where(TblKasirDao.Properties.Status.eq(0)).list();

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
        //=====hapus kasir
        daoSession.getTblKasirDao().queryBuilder()
                .where(TblKasirDao.Properties.Status.eq(0))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        daoSession.clear();
    }

    private void deklarasi() {
        TextView tvtoko = (TextView) findViewById(R.id.tvtoko);
        TextView tvuser = (TextView) findViewById(R.id.tvuser);
        if(slogin.equals("1")){
            tvtoko.setText(snama);
            tvuser.setText(skasir);
        }else{
            rlmanual.setVisibility(View.VISIBLE);
        }
        //==========
        SharedPreferences pref = getSharedPreferences(getString(R.string.sp_kasir), MODE_PRIVATE);
        SharedPreferences.Editor editorunik = pref.edit();
        editorunik.putString("kodeunik","");
        editorunik.putString("tunai","");
        editorunik.putString("cek","0");
        editorunik.apply();//commit();
    }

    private void header() {

        SharedPreferences prefx = this.getSharedPreferences(getString(R.string.sp_profil), Context.MODE_PRIVATE);
        snama = prefx.getString("nama", "");
        salamat = prefx.getString("alamat", "");
        stelepon = prefx.getString("tlp", "");
        skasir = prefx.getString("kasir", "");
        slogin = prefx.getString("login", "");

        //TextView headmenu = (TextView) findViewById(R.id.headmenu);
       // TextView headtoko = (TextView) findViewById(R.id.headtoko);
       //headmenu.setText(getString(R.string.mnprofil));
        //headtoko.setText(snama);

    }



    public void gostok(View view) {
        startActivity(new Intent(getApplicationContext(), StokActivity.class));
        finish();
    }

    public void gokasir(View view) {
        startActivity(new Intent(getApplicationContext(), KasirActivity.class));
        finish();
    }

    public void gotransaksi(View view) {
        startActivity(new Intent(getApplicationContext(), TransaksiActivity.class));
        finish();
    }

    public void goprofil(View view) {
        startActivity(new Intent(getApplicationContext(), ProfilActivity.class));
        finish();
    }

    public void gobarcode(View view) {
        startActivity(new Intent(getApplicationContext(), BarcodeActivity.class));
        finish();
    }

    public void gochart(View view) {
        Toast.makeText(getApplicationContext(), getString(R.string.notic) , Toast.LENGTH_SHORT).show();
    }
    public void closeprofil(View view) {
        rlmanual.setVisibility(View.GONE);
    }

    private void cekupdate() {

        // Creates instance of the manager.
        final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks whether the platform allows the specified type of update,
// and checks the update priority.
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.updatePriority() >= HIGH_PRIORITY_UPDATE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Request an immediate update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.IMMEDIATE,
                                // The current activity making the update request.
                                MenuActivity.this,
                                // Include a request code to later monitor this update request.
                                MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.d("xxx","Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }



}
