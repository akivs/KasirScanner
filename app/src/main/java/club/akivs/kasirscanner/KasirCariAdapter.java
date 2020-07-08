package club.akivs.kasirscanner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.akivs.kasirscanner.utils.FunctionHelper;
import club.akivs.kasirscanner.utils.database.TblStok;

import static android.os.Environment.getExternalStorageDirectory;
import static java.io.File.separator;


public class KasirCariAdapter extends RecyclerView.Adapter<KasirCariAdapter.ViewHolder> {

    private static final String TAG = KasirCariAdapter.class.getSimpleName();


    private List<TblStok> slist;
    private KasirCariAdapterCallback mAdapterCallback;
    private View view;

    public KasirCariAdapter(List<TblStok> list, KasirCariAdapterCallback adapterCallback) {
        this.slist = list;
        this.mAdapterCallback = adapterCallback;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kasir_cari,
                parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TblStok item = slist.get(position);

        String kode = item.getKode_stok();
        String nama_stok = item.getNama_stok();
        String jum_stok = item.getJum_stok();
        Double harga = Double.valueOf(item.getHarga());
        String folder_main = "KasirBarcode";
        String mainpath = getExternalStorageDirectory() + separator + folder_main + separator ;
        String path = mainpath + "_stok" + kode + ".png";
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        holder.tvkode.setText(kode);
        holder.tvnamastok.setText(nama_stok);
        holder.tvjumstok.setText("("+jum_stok+")");
        holder.imageno.setImageBitmap(b);
        holder.tvhargastok.setText(FunctionHelper.convertRupiah(harga, (Activity) mAdapterCallback,"data_profil"));
    }


    @Override
    public int getItemCount() {
        return slist.size();
    }

    public void clear() {
        int size = this.slist.size();
        this.slist.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvkode)
        TextView tvkode;
        @BindView(R.id.tvnamastok)
        TextView tvnamastok;
        @BindView(R.id.tvjumstok)
        TextView tvjumstok;
        @BindView(R.id.tvhargastok)
        TextView tvhargastok;
        @BindView(R.id.ivDelete)
        ImageView ivDelete;
        @BindView(R.id.imageno)
        ImageView imageno;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapterCallback.onView(getAdapterPosition());
                }
            });


        }
    }

    public void filterList(ArrayList<TblStok> filteredList) {
        slist = filteredList;
        notifyDataSetChanged();
    }

    public interface KasirCariAdapterCallback extends List<TblStok> {
        void onView(int position);
    }
}

