package club.akivs.kasirscanner;

import android.app.Activity;
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
import club.akivs.kasirscanner.utils.FunctionHelper;
import club.akivs.kasirscanner.utils.database.TblKasir;


public class KasirEndAdapter extends RecyclerView.Adapter<KasirEndAdapter.ViewHolder> {

    private static final String TAG = KasirEndAdapter.class.getSimpleName();

    private List<TblKasir> slist;
    private KasirEndAdapterCallback mAdapterCallback;

    public KasirEndAdapter(List<TblKasir> list, KasirEndAdapterCallback adapterCallback) {
        this.slist = list;
        this.mAdapterCallback = adapterCallback;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kasir_end,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TblKasir item = slist.get(position);

        String nama_stok = item.getNama_stok();
        String jum_stok = item.getJum_stok();
        Double harga = Double.valueOf(item.getHarga());
        Double j = Double.valueOf(jum_stok);

        holder.tvnamastok.setText(nama_stok);
        holder.tvjumstok.setText("x"+jum_stok+" =");

        holder.tvhargastok.setText(FunctionHelper.convertRupiah(harga, (Activity) mAdapterCallback,"data_profil"));
        holder.tvakhir.setText(FunctionHelper.convertRupiah(harga*j, (Activity) mAdapterCallback,"data_profil"));
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


        @BindView(R.id.tvnamastok)
        TextView tvnamastok;
        @BindView(R.id.tvjumstok)
        TextView tvjumstok;
        @BindView(R.id.tvhargastok)
        TextView tvhargastok;
        @BindView(R.id.tvakhir)
        TextView tvakhir;
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

    public interface KasirEndAdapterCallback {
        void onLongClick(int position);
        void onDelete(int position);
    }

}

