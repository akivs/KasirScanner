package club.akivs.kasirscanner.utils.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;


@Entity(indexes = {
        @Index(value = "kode, tanggal DESC", unique = true)
    })
public class TblBarcode {
    @Id(autoincrement = true)
    private Long idTblBarcode;

    private String kode;
    private String nama;
    private String ket;
    private String tanggal;
    private String value1;
    private String value2;
    private String value3;
    private String value4;
    private String value5;
@Generated(hash = 684352402)
public TblBarcode(Long idTblBarcode, String kode, String nama, String ket,
        String tanggal, String value1, String value2, String value3,
        String value4, String value5) {
    this.idTblBarcode = idTblBarcode;
    this.kode = kode;
    this.nama = nama;
    this.ket = ket;
    this.tanggal = tanggal;
    this.value1 = value1;
    this.value2 = value2;
    this.value3 = value3;
    this.value4 = value4;
    this.value5 = value5;
}
@Generated(hash = 306095506)
public TblBarcode() {
}
public Long getIdTblBarcode() {
    return this.idTblBarcode;
}
public void setIdTblBarcode(Long idTblBarcode) {
    this.idTblBarcode = idTblBarcode;
}
public String getKode() {
    return this.kode;
}
public void setKode(String kode) {
    this.kode = kode;
}
public String getNama() {
    return this.nama;
}
public void setNama(String nama) {
    this.nama = nama;
}
public String getKet() {
    return this.ket;
}
public void setKet(String ket) {
    this.ket = ket;
}
public String getTanggal() {
    return this.tanggal;
}
public void setTanggal(String tanggal) {
    this.tanggal = tanggal;
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