package models;

import java.util.Scanner;

import payments.Pembayaran;

public class Invoice {
    private String idTransaksi;
    private String username;
    private int total;
    private String status;
    private Pembayaran pembayaran;  

    public Invoice(String idTransaksi, String username, int total, String status, Pembayaran pembayaran) {
        this.idTransaksi = idTransaksi;
        this.username = username;
        this.total = total;
        this.status = status;
        this.pembayaran = pembayaran;  
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

    public void setPembayaran(Pembayaran pembayaran) {
        this.pembayaran = pembayaran;
    }

    public String prosesPembayaran(Scanner scanner) {
        return pembayaran.prosesPembayaran(scanner); 
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "idTransaksi='" + idTransaksi + '\'' +
                ", username='" + username + '\'' +
                ", total=" + total +
                ", status='" + status + '\'' +
                ", pembayaran=" + pembayaran.getClass().getSimpleName() +  
                '}';
    }
}
