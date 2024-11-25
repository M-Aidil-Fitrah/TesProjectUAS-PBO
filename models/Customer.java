package models;

import payments.*;
import java.io.*;
import java.util.*;

public class Customer extends Akun {
    private Keranjang keranjang;
    private List<Invoice> invoiceSelesai;

    public Customer(String id, String username, String password) {
        super(id, username, password);
        this.keranjang = new Keranjang();
        this.invoiceSelesai = new ArrayList<>();
    }

    @Override
    public void viewMenu() {
        System.out.println("Menu Customer");
    }

    public void tambahKeKeranjang(Barang barang, int jumlah) {
        keranjang.tambahBarang(barang, jumlah);
        System.out.println(barang.getNama() + " telah ditambahkan ke keranjang.");
    }

    public void checkout(ListBarang listBarang, Scanner scanner) {
        if (keranjang.isEmpty()) {
            System.out.println("Keranjang kosong. Tidak ada barang untuk checkout.");
            return;
        }
        
        Pembayaran pembayaran = pilihPembayaran(scanner);
        if (pembayaran == null) {
            System.out.println("Checkout dibatalkan. Metode pembayaran tidak valid.");
            return;
        }
        
        int totalHarga = keranjang.totalHarga();
        pembayaran.setTotal(totalHarga); 
        
        System.out.println("Metode pembayaran yang dipilih: " + pembayaran.getClass().getSimpleName());

        String hasilPembayaran = pembayaran.prosesPembayaran(scanner);
        System.out.println(hasilPembayaran);  

        String idTransaksi = generateRandomIdTransaksi();  
        List<Barang> barangList = new ArrayList<>(keranjang.getBarangKeranjang().keySet());
    
        Transaksi transaksi = new Transaksi(idTransaksi, getUsername(), totalHarga, "PENDING", pembayaran, barangList);
        simpanTransaksiKeFile(transaksi);
    
        Invoice invoice = new Invoice(idTransaksi, getUsername(), totalHarga, "PENDING", pembayaran);
        invoiceSelesai.add(invoice);
    
        System.out.println("Checkout berhasil. ID Transaksi: " + idTransaksi);
    
        for (Barang barang : barangList) {
            int jumlahDibeli = keranjang.getBarangKeranjang().get(barang);
            Barang stokBarang = listBarang.getBarangById(barang.getId());
            if (stokBarang != null) {
                stokBarang.setStok(stokBarang.getStok() - jumlahDibeli);
            }
        }
    
        keranjang.kosongkan();
    }
    
    private String generateRandomIdTransaksi() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder idTransaksi = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(characters.length());
            idTransaksi.append(characters.charAt(index));
        }
        return idTransaksi.toString();
    }
    
    private Pembayaran pilihPembayaran(Scanner scanner) {
        System.out.println("Pilih metode pembayaran:");
        System.out.println("1. Bank");
        System.out.println("2. QRIS");
        System.out.println("3. COD");
        System.out.print("Masukkan pilihan (1/2/3): ");
    
        String input = scanner.nextLine();
        if (!input.matches("[123]")) {
            System.out.println("Pilihan tidak valid!");
            return null;  
        }
    
        int pilihan = Integer.parseInt(input);
    
        switch (pilihan) {
            case 1:
                return new Bank();
            case 2:
                return new QRIS();
            case 3:
                return new COD();
            default:
                return null;
        }
    }

    public void lihatRiwayat() {
        System.out.println("=== Riwayat Belanja ===");
        for (Invoice invoice : invoiceSelesai) {
            System.out.println("ID Transaksi: " + invoice.getIdTransaksi() + ", Total: " + invoice.getTotal() + ", Status: " + invoice.getStatus());
        }
    }

    private void simpanTransaksiKeFile(Transaksi transaksi) {
        File file = new File("data/transactions.txt");

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(transaksi.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Gagal menyimpan transaksi ke file: " + e.getMessage());
        }
    }
}
