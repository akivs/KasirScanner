package club.akivs.kasirscanner.utils.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity(indexes = {
        @Index(value = "kode_kasir, tanggal DESC", unique = true)
})
public class TblKasir {
    @Id(autoincrement = true)
    private Long idTblKasir;

    private String kode_kasir;
    private String nama_kasir;
    private String kode_stok;
    private String nama_stok;
    private String jum_stok;
    private String tanggal;
    private String tunai;
    private String value1;
    private String value2;
    private String value3;
    private String value4;
    private String value5;
    private String harga;
    private int status;
@Generated(hash = 1477066457)
public TblKasir(Long idTblKasir, String kode_kasir, String nama_kasir,
        String kode_stok, String nama_stok, String jum_stok, String tanggal,
        String tunai, String value1, String value2, String value3,
        String value4, String value5, String harga, int status) {
    this.idTblKasir = idTblKasir;
    this.kode_kasir = kode_kasir;
    this.nama_kasir = nama_kasir;
    this.kode_stok = kode_stok;
    this.nama_stok = nama_stok;
    this.jum_stok = jum_stok;
    this.tanggal = tanggal;
    this.tunai = tunai;
    this.value1 = value1;
    this.value2 = value2;
    this.value3 = value3;
    this.value4 = value4;
    this.value5 = value5;
    this.harga = harga;
    this.status = status;
}
@Generated(hash = 2021777251)
public TblKasir() {
}
public Long getIdTblKasir() {
    return this.idTblKasir;
}
public void setIdTblKasir(Long idTblKasir) {
    this.idTblKasir = idTblKasir;
}
public String getKode_kasir() {
    return this.kode_kasir;
}
public void setKode_kasir(String kode_kasir) {
    this.kode_kasir = kode_kasir;
}
public String getNama_kasir() {
    return this.nama_kasir;
}
public void setNama_kasir(String nama_kasir) {
    this.nama_kasir = nama_kasir;
}
public String getNama_stok() {
    return this.nama_stok;
}
public void setNama_stok(String nama_stok) {
    this.nama_stok = nama_stok;
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
public int getStatus() {
    return this.status;
}
public void setStatus(int status) {
    this.status = status;
}
public String getKode_stok() {
    return this.kode_stok;
}
public void setKode_stok(String kode_stok) {
    this.kode_stok = kode_stok;
}
public String getTunai() {
    return this.tunai;
}
public void setTunai(String tunai) {
    this.tunai = tunai;
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
