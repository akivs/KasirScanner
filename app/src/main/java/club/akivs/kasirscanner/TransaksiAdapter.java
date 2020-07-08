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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.akivs.kasirscanner.utils.FunctionHelper;
import club.akivs.kasirscanner.utils.database.TblKasir;
import club.akivs.kasirscanner.utils.database.TblStok;


public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {

    private static final String TAG = TransaksiAdapter.class.getSimpleName();

    private List<TblKasir> slist;
    private TransaksiAdapterCallback mAdapterCallback;
    private View view;

    public TransaksiAdapter(List<TblKasir> list, TransaksiAdapterCallback adapterCallback) {
        this.slist = list;
        this.mAdapterCallback = adapterCallback;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trans,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TblKasir item = slist.get(position);

        String kode = item.getTanggal();
        String nama_stok = item.getNama_kasir();
        String jum_stok = item.getKode_kasir();
        Double harga = Double.valueOf(item.getTunai());

        holder.tvkode.setText(kode);
        holder.tvnamastok.setText(jum_stok);
        holder.tvjumstok.setText(nama_stok);
        holder.tvhargastok.setText(String.valueOf(position+1));
       //holder.tvhargastok.setText(FunctionHelper.convertRupiah(getTotal(), (Activity) mAdapterCallback,"data_profil"));
    }

    private Double getTotal(){
        Double total = 0.0;
        for (int i = 0; i < slist.size(); i++){
            Double nominal = Double.valueOf(slist.get(i).getHarga());
            Double juml = Double.valueOf(slist.get(i).getJum_stok());
            total = total + (nominal*juml);
        }
        return total;
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

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapterCallback.onView(getAdapterPosition());
                }
            });
        }
    }

    public interface TransaksiAdapterCallback extends List<TblKasir> {
        void onLongClick(int position);
        void onDelete(int position);
        void onView(int position);
    }


    public void filterList(ArrayList<TblKasir> filteredList) {
        slist = filteredList;
        notifyDataSetChanged();
    }

}

