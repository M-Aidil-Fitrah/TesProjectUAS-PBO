package models;

import payments.Pembayaran;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Transaksi {
    private String idTransaksi;
    private String username;
    private int total;
    private String status;
    private Pembayaran pembayaran;
    private List<Barang> barangList;

    public Transaksi(String idTransaksi, String username, int total, String status, Pembayaran pembayaran, List<Barang> barangList) {
        this.idTransaksi = idTransaksi;
        this.username = username;
        this.total = total;
        this.status = status;
        this.pembayaran = pembayaran;
        this.barangList = barangList;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public String getUsername() {
        return username;
    }

    public int getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public Pembayaran getPembayaran() {
        return pembayaran;
    }

    public List<Barang> getBarangList() {
        return barangList;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPembayaran(Pembayaran pembayaran) {
        this.pembayaran = pembayaran;
    }

    public String prosesPembayaran(Scanner scanner) {
        return pembayaran.prosesPembayaran(scanner);  
    }

public void simpanKeFile() {
    File file = new File("data/transactions.csv");
    try {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(idTransaksi + "," + username + "," + (int) (total * 100) + "," + status + "," + pembayaran.getClass().getSimpleName());

            for (Barang barang : barangList) {
                writer.write("," + barang.getNama() + " (x" + barang.getStok() + ")");
            }

            writer.newLine();  
        }
    } catch (IOException e) {
        System.out.println("Error: Tidak dapat menyimpan transaksi ke file.");
    }
}

    @Override
    public String toString() {
        StringBuilder barangStr = new StringBuilder();
        for (Barang barang : barangList) {
            barangStr.append(barang.getNama()).append(" (x").append(barang.getStok()).append("), ");
        }

        return "Transaksi{" +
                "idTransaksi='" + idTransaksi + '\'' +
                ", username='" + username + '\'' +
                ", total=" + total +  
                ", status='" + status + '\'' +
                ", pembayaran=" + pembayaran.getClass().getSimpleName() +
                ", barangList=" + barangStr +
                '}';
    }
}