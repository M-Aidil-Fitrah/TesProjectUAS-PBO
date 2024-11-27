package models;

import java.util.*;

public class Keranjang {
    private Map<Barang, Integer> barangKeranjang;

    public Keranjang() {
        this.barangKeranjang = new HashMap<>();
    }

    public void tambahBarang(Barang barang, int jumlah) {
        if (barangKeranjang.containsKey(barang)) {
            // Jika barang sudah ada di keranjang, tambahkan jumlahnya
            barangKeranjang.put(barang, barangKeranjang.get(barang) + jumlah);
        } else {
            // Jika barang baru ditambahkan, set jumlahCheckout
            barang.setJumlahCheckout(jumlah);
            barangKeranjang.put(barang, jumlah);
        }
    }

    // public void hapusBarang(Barang barang) {
    //     barangKeranjang.remove(barang);
    // }

    // public void tampilkanKeranjang() {
    //     System.out.println("=== Barang di Keranjang ===");
    //     for (Map.Entry<Barang, Integer> entry : barangKeranjang.entrySet()) {
    //         System.out.println(entry.getKey().getNama() + " - " + entry.getValue() + " x " + entry.getKey().getHarga());
    //     }
    // }

    public int totalHarga() {
        int total = 0;
        for (Map.Entry<Barang, Integer> entry : barangKeranjang.entrySet()) {
            total += entry.getKey().getHarga() * entry.getValue();
        }
        return total;
    }

    public boolean isEmpty() {
        return barangKeranjang.isEmpty();
    }

    public Map<Barang, Integer> getBarangKeranjang() {
        return barangKeranjang;
    }

    public void kosongkan() {
        barangKeranjang.clear();
    }
}
