package club.akivs.kasirscanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.akivs.kasirscanner.utils.database.TblBarcode;
import club.akivs.kasirscanner.utils.database.TblStok;


public class BarcodeAdapter extends RecyclerView.Adapter<BarcodeAdapter.ViewHolder> {

    private static final String TAG = BarcodeAdapter.class.getSimpleName();

    private List<TblBarcode> slist;
    private BarcodeAdapterCallback mAdapterCallback;

    public BarcodeAdapter(List<TblBarcode> list, BarcodeAdapterCallback adapterCallback) {
        this.slist = list;
        this.mAdapterCallback = adapterCallback;
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
        TblBarcode item = slist.get(position);

        String kode = item.getKode();
        String nama_stok = item.getNama();
        String jum_stok = item.getKet();
        String harga = item.getTanggal();

        holder.tvkode.setText(kode);
        holder.tvnamastok.setText(nama_stok);
        holder.tvjumstok.setText("("+jum_stok+")");

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.tvhargastok.setText(harga);
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

    public interface BarcodeAdapterCallback {
        void onLongClick(int position);
        void onDelete(int position);
    }
}

