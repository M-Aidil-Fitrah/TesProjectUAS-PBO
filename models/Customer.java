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
        //simpanTransaksiKeFile(transaksi);
        transaksi.simpanTransaksiKeFile();

        Invoice invoice = new Invoice(idTransaksi, totalHarga, "PENDING", pembayaran);
        invoiceSelesai.add(invoice);

        System.out.println("Checkout berhasil. ID Transaksi: " + idTransaksi);

        // Perbarui stok di ListBarang
        for (Barang barang : barangList) {
            int jumlahDibeli = keranjang.getBarangKeranjang().get(barang);
            Barang stokBarang = listBarang.getBarangById(barang.getId());
            if (stokBarang != null) {
                if (stokBarang.getStok() >= jumlahDibeli) {
                    stokBarang.setStok(stokBarang.getStok() - jumlahDibeli); // Kurangi stok
                } else {
                    System.out.println("Stok tidak mencukupi untuk barang: " + stokBarang.getNama());
                    return; // Batalkan jika stok tidak mencukupi
                }
            } else {
                System.out.println("Barang dengan ID " + barang.getId() + " tidak ditemukan di daftar barang.");
            }
        }

        // Simpan perubahan stok ke file products.txt
        listBarang.simpanBarangKeFile();
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
    
        List<Invoice> riwayatSelesai = new ArrayList<>();
    
        // Membaca file transaksi dan memuat transaksi yang statusnya SELESAI ke dalam riwayatSelesai
        try (BufferedReader reader = new BufferedReader(new FileReader("data/transactions.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String idTransaksi = parts[0].split("=")[1].trim();
                    String status = parts[3].split("=")[1].trim();
                    String pembayaranStr = parts[4].split("=")[1].trim();

                    // Hanya menampilkan transaksi yang statusnya SELESAI
                    if ("SELESAI".equals(status)) {
                        // Mendapatkan total transaksi dari file
                        int total = Integer.parseInt(parts[2].split("=")[1].trim());

                        // Menentukan jenis pembayaran berdasarkan string yang ada
                        Pembayaran pembayaran = null;
                        switch (pembayaranStr) {
                            case "Bank":
                                pembayaran = new Bank();
                                break;
                            case "QRIS":
                                pembayaran = new QRIS();
                                break;
                            case "COD":
                                pembayaran = new COD();
                                break;
                            default:
                                System.out.println("Pembayaran tidak dikenal: " + pembayaranStr);
                                break;
                        }

                        // Buat invoice untuk transaksi selesai dan masukkan ke riwayat
                        if (pembayaran != null) {
                            Invoice invoice = new Invoice(idTransaksi, total, "SELESAI", pembayaran);
                            riwayatSelesai.add(invoice);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error saat membaca file transaksi: " + e.getMessage());
        }   
        // Jika tidak ada transaksi selesai
        if (riwayatSelesai.isEmpty()) {
            System.out.println("Belum ada transaksi yang selesai.");
            return;
        }
    
        // Menampilkan riwayat belanja
        for (Invoice invoice : riwayatSelesai) {
            System.out.println("ID Transaksi: " + invoice.getIdTransaksi() +
                               ", Total: Rp" + invoice.getTotal() +
                               ", Status: " + invoice.getStatus() +
                               ", Pembayaran: " + invoice.getPembayaran()); // Menampilkan string pembayaran langsung
        }
    }

    public void lihatStatusTransaksi() {
        File file = new File("data/transactions.txt");
        if (!file.exists()) {
            System.out.println("Belum ada transaksi.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean found = false;
            String usernameDicari = "username=" + getUsername(); 
            
            while ((line = reader.readLine()) != null) {
                if (line.contains(usernameDicari)) {
                    found = true;
                    // Pisahkan data transaksi berdasarkan koma
                    String[] parts = line.split(",");
                    String idTransaksi = parts[0].split("=")[1].trim();
                    String total = parts[2].split("=")[1].trim();
                    String status = parts[3].split("=")[1].trim();
                    String pembayaran = parts[4].split("=")[1].trim();
                    String barang = line.substring(line.indexOf("barang=") + 7); // Ambil daftar barang
                    
                    // Format output
                    System.out.println("=================================");
                    System.out.println("ID Transaksi : " + idTransaksi);
                    System.out.println("Total        : " + total);
                    System.out.println("Status       : " + status);
                    System.out.println("Pembayaran   : " + pembayaran);
                    System.out.println("Daftar Barang: " + barang);
                    System.out.println("=================================");
                }
            }
        
            if (!found) {
                System.out.println("Tidak ada transaksi ditemukan untuk pengguna ini.");
            }
        } catch (IOException e) {
            System.out.println("Error saat membaca file transaksi: " + e.getMessage());
        }
    
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean found = false;
            String usernameDicari = "username=" + getUsername(); 
            
            while ((line = reader.readLine()) != null) {
                if (line.contains(usernameDicari)) {
                    // Pisahkan data transaksi berdasarkan koma
                    String[] parts = line.split(",");
                    String idTransaksi = parts[0].split("=")[1].trim();
                    String total = parts[2].split("=")[1].trim();
                    String status = parts[3].split("=")[1].trim();
                    String pembayaran = parts[4].split("=")[1].trim();
                    String barang = line.substring(line.indexOf("barang=") + 7); // Ambil daftar barang
                    
                    // Menampilkan hanya transaksi yang statusnya SELESAI
                    if ("SELESAI".equals(status)) {
                        found = true;
                        // Format output
                        System.out.println("=================================");
                        System.out.println("ID Transaksi : " + idTransaksi);
                        System.out.println("Total        : " + total);
                        System.out.println("Status       : " + status);
                        System.out.println("Pembayaran   : " + pembayaran);
                        System.out.println("Daftar Barang: " + barang);
                        System.out.println("=================================");
                    }
                }
            }
    
            if (!found) {
                System.out.println("Tidak ada transaksi dengan status SELESAI ditemukan untuk pengguna ini.");
            }
        } catch (IOException e) {
            System.out.println("Error saat membaca file transaksi: " + e.getMessage());
        }
    }
    

    // private void simpanTransaksiKeFile(Transaksi transaksi) {
    //     File file = new File("data/transactions.txt");

    //     try {
    //         if (!file.exists()) {
    //             file.getParentFile().mkdirs();
    //             file.createNewFile();
    //         }

    //         try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
    //             writer.write(transaksi.toString());
    //             writer.newLine();
    //         }
    //     } catch (IOException e) {
    //         System.out.println("Gagal menyimpan transaksi ke file: " + e.getMessage());
    //     }
    // }
} 