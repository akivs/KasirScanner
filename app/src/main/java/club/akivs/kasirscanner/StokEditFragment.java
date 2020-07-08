package club.akivs.kasirscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import club.akivs.kasirscanner.utils.FunctionHelper;

import static android.os.Environment.getExternalStorageDirectory;
import static java.io.File.separator;


public class StokEditFragment extends DialogFragment {

    @BindView(R.id.etstokkode)
    EditText etstokkode;
    @BindView(R.id.etstoknama)
    EditText etstoknama;
    @BindView(R.id.etstokjum)
    EditText etstokjum;
    @BindView(R.id.etstokharga)
    EditText etstokharga;
    @BindView(R.id.etKet)
    EditText etKet;
    @BindView(R.id.etstokhargabeli)
    EditText etstokhargabeli;
    @BindView(R.id.ivkamera)
    ImageView ivkamera;
    @BindView(R.id.btnsave)
    Button btnsave;
    @BindView(R.id.btkamera)
    Button btkamera;


    private Unbinder unbinder;
    private EditDialogListener editDialogListener;

    private static final String ARGS_ID = "args_id";
    private static final String ARGS_KODE = "args_kode";
    private static final String ARGS_NAMA = "args_nama";
    private static final String ARGS_JUM = "args_jum";
    private static final String ARGS_HARGA = "args_haraga";
    private static final String ARGS_KET = "args_ket";
    private static final String ARGS_HARGA_BELI = "args_harga_beli";
    private static final String ARGS_GAMBAR = "args_gambar";

    private long mId;
    private String mKode;
    private String mNama;
    private String mJum;
    private String mKet;
    private String mHarga;
    private String mHargabeli;
    private String mGambar;
    private Bitmap b;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    public StokEditFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        editDialogListener = (EditDialogListener) context;
    }

    public static StokEditFragment newInstance(long id, String kode, String nama, String jum, String harga, String ket, String harga_beli) {
        StokEditFragment stokEditFragment = new StokEditFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_ID, id);
        args.putString(ARGS_KODE, kode);
        args.putString(ARGS_NAMA, nama);
        args.putString(ARGS_JUM, jum);
        args.putString(ARGS_HARGA, harga);
        args.putString(ARGS_KET, ket);
        args.putString(ARGS_HARGA_BELI, harga_beli);
        //args.putByteArray(ARGS_GAMBAR, image);
        stokEditFragment.setArguments(args);
        return stokEditFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);

        if (getArguments() != null){
            mId = getArguments().getLong(ARGS_ID);
            mKode = getArguments().getString(ARGS_KODE);
            mNama = getArguments().getString(ARGS_NAMA);
            mJum = getArguments().getString(ARGS_JUM);
            mHarga = getArguments().getString(ARGS_HARGA);
            mKet = getArguments().getString(ARGS_KET);
            mHargabeli = getArguments().getString(ARGS_HARGA_BELI);
            //mGambar = getArguments().getString(ARGS_GAMBAR);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stok_edit, container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etstokkode.setText(mKode);
        etstoknama.setText(mNama);
        etstokjum.setText(mJum);
        etstokharga.setText(String.valueOf(mHarga));
        etKet.setText(mKet);
        etstokhargabeli.setText(mHargabeli);
        //======
        String folder_main = "KasirBarcode";
        String mainpath = getExternalStorageDirectory() + separator + folder_main + separator ;
        String path = mainpath + "_stok" + mKode + ".png";
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ivkamera.setImageBitmap(b);

        btkamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionHelper.checkPermissions(getActivity(),getActivity());
                if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }});

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kode = etstokkode.getText().toString();
                String nama = etstoknama.getText().toString();
                String jum = etstokjum.getText().toString();
                String harga = etstokharga.getText().toString();
                String ket = etKet.getText().toString();
                String hargabeli = etstokhargabeli.getText().toString();
                BitmapDrawable drawable = (BitmapDrawable) ivkamera.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                /*
                Fungsi ini iuntuk mengirim data berupa id, pembelian, dan nominal ke
                activity/fragment yang di implementasinya.
                 */
                editDialogListener.requestUpdate(mId, kode,nama,jum, harga,ket,hargabeli,bitmap);
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /*
    Interface EditDialogListener ini untuk digunakan dalam class yang kita implement nantinya.
    EditDialogListener ini berisi fungsi requestUpdate dengan parameter id, pembelian, nominal.
    Nah nantinya data yang ada di EditDialogFragment ini kita akan parsing ke activity/frgament yang
    diimplementnya.

    Sebagai contoh : Dari edit dialog ini user meng-inputkan pembelian "Baju Supreme" dan nominalnya
    "5000", maka si activity/fragment implementnya akan menerima data tersebut. Data tersebut nanti
    kita akan olah sesuai dengan kebtuuhan.
     */
    public interface EditDialogListener {
        void requestUpdate(long mId, String kode, String nama, String jum, String parseInt, String ket, String hargabeli, Bitmap gambar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivkamera.setImageBitmap(photo);
        }
    }
}
