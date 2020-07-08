package club.akivs.kasirscanner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.akivs.kasirscanner.utils.FunctionHelper;
import club.akivs.kasirscanner.utils.database.TblStok;

import static android.os.Environment.getExternalStorageDirectory;
import static java.io.File.separator;


public class StokAdapter extends RecyclerView.Adapter<StokAdapter.ViewHolder> {

    private static final String TAG = StokAdapter.class.getSimpleName();
    private final Context context;
    private List<TblStok> slist;
    private StokAdapterCallback mAdapterCallback;

    public StokAdapter(List<TblStok> list, StokAdapterCallback adapterCallback, Context context) {
        this.slist = list;
        this.mAdapterCallback = adapterCallback;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stok,
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

        Bitmap bitn = FunctionHelper.ambilgambar(kode);

        holder.tvkode.setText(kode);
        holder.tvnamastok.setText(nama_stok);
        holder.tvjumstok.setText("("+jum_stok+")");
        holder.imageno.setImageBitmap(bitn);

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

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapterCallback.onDelete(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mAdapterCallback.onLongClick(getAdapterPosition());
                    return true;
                }
            });
        }
    }

    public interface StokAdapterCallback {
        void onLongClick(int position);
        void onDelete(int position);
    }
}

