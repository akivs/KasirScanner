package club.akivs.kasirscanner.utils.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;

@Entity(indexes = {
        @Index(value = "kode_stok, tanggal DESC", unique = true)
})
public class TblStok {
    @Id(autoincrement = true)
    private Long idTblStok;

    private String kode_stok;
    private String nama_stok;
    private String jenis_stok;
    private String ket_stok;
    private String jum_stok;
    private String tanggal;
    private String harga;
    private String value1; //harga beli
    private String value2;
    private String value3;
    private String value4;
    private String value5;
@Generated(hash = 2107056119)
public TblStok(Long idTblStok, String kode_stok, String nama_stok, String jenis_stok,
        String ket_stok, String jum_stok, String tanggal, String harga, String value1,
        String value2, String value3, String value4, String value5) {
    this.idTblStok = idTblStok;
    this.kode_stok = kode_stok;
    this.nama_stok = nama_stok;
    this.jenis_stok = jenis_stok;
    this.ket_stok = ket_stok;
    this.jum_stok = jum_stok;
    this.tanggal = tanggal;
    this.harga = harga;
    this.value1 = value1;
    this.value2 = value2;
    this.value3 = value3;
    this.value4 = value4;
    this.value5 = value5;
}
@Generated(hash = 1187005680)
public TblStok() {
}
public Long getidTblStok() {
    return this.idTblStok;
}
public void setidTblStok(Long idTblStok) {
    this.idTblStok = idTblStok;
}
public String getKode_stok() {
    return this.kode_stok;
}
public void setKode_stok(String kode_stok) {
    this.kode_stok = kode_stok;
}
public String getNama_stok() {
    return this.nama_stok;
}
public void setNama_stok(String nama_stok) {
    this.nama_stok = nama_stok;
}
public String getJenis_stok() {
    return this.jenis_stok;
}
public void setJenis_stok(String jenis_stok) {
    this.jenis_stok = jenis_stok;
}
public String getKet_stok() {
    return this.ket_stok;
}
public void setKet_stok(String ket_stok) {
    this.ket_stok = ket_stok;
}
public String getJum_stok() {
    return this.jum_stok;
}
public void setJum_stok(String jum_stok) {
    this.jum_stok = jum_stok;
}
public String getTanggal() {
    return this.tanggal;
}
public void setTanggal(String tanggal) {
    this.tanggal = tanggal;
}
public String getHarga() {
    return this.harga;
}
public void setHarga(String harga) {
    this.harga = harga;
}
public Long getIdTblStok() {
    return this.idTblStok;
}
public void setIdTblStok(Long idTblStok) {
    this.idTblStok = idTblStok;
}
public String getValue1() {
    return this.value1;
}
public void setValue1(String value1) {
    this.value1 = value1;
}
public String getValue2() {
    return this.value2;
}
public void setValue2(String value2) {
    this.value2 = value2;
}
public String getValue3() {
    return this.value3;
}
public void setValue3(String value3) {
    this.value3 = value3;
}
public String getValue4() {
    return this.value4;
}
public void setValue4(String value4) {
    this.value4 = value4;
}
public String getValue5() {
    return this.value5;
}
public void setValue5(String value5) {
    this.value5 = value5;
}
}
