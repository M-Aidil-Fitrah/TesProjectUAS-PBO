package models;

public class Barang {
    private int id;
    private String nama;
    private int harga;
    private int stok;
    private int jumlahCheckout; // Tambahkan atribut ini

    public Barang(int id, String nama, int harga, int stok) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
    }

    public Barang(String nama, int jumlahCheckout) {
        this.nama = nama;
        this.jumlahCheckout = jumlahCheckout;
    }

    // Tambahkan getter dan setter untuk jumlahCheckout
    public int getJumlahCheckout() {
        return jumlahCheckout;
    }

    public void setJumlahCheckout(int jumlahCheckout) {
        this.jumlahCheckout = jumlahCheckout;
    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public String toStringFile() {
        return id + "," + nama + "," + harga + "," + stok;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Nama: " + nama + ", Harga: " + harga + ", Stok: " + stok;
    }
}
