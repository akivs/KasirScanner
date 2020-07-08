package club.akivs.kasirscanner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.dkaratzas.android.inapp.update.Constants;
import eu.dkaratzas.android.inapp.update.InAppUpdateManager;
import eu.dkaratzas.android.inapp.update.InAppUpdateStatus;

public class SplashActivity extends AppCompatActivity {

    private TextView tvSplash;
    private TextView versi;
    private String version;

    ProgressBar bar;

    private Handler progressBarHandler = new Handler();

    GradientDrawable progressGradientDrawable = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, new int[]{
            0xff1e90ff,0xff006ab6,0xff367ba8});
    ClipDrawable progressClipDrawable = new ClipDrawable(
            progressGradientDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
    Drawable[] progressDrawables = {
            new ColorDrawable(0xffffffff),
            progressClipDrawable, progressClipDrawable};
    LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvSplash = (TextView) findViewById(R.id.tvSplash);
        versi = (TextView) findViewById(R.id.versi);

        bar = (ProgressBar) findViewById(R.id.progresBar1);
        bar.setProgress(0);
        bar.setMax(100);

        progressLayerDrawable.setId(0, android.R.id.background);
        progressLayerDrawable.setId(1, android.R.id.secondaryProgress);
        progressLayerDrawable.setId(2, android.R.id.progress);

        bar.setProgressDrawable(progressLayerDrawable);
        //=======================
        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = pInfo.versionName;
        versi.setText("v."+version);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                finish();
            }
        }, 3000L); //3000 L = 3 detik



    }





    public void akivs(View view) {
        String appPackageName = "Akivs";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=" + appPackageName)));
        }
    }


}
