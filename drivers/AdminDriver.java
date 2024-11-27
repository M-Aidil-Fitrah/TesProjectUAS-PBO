package drivers;

import models.*;
import payments.Bank;
import payments.COD;
import payments.Pembayaran;
import payments.QRIS;

import java.io.*;
import java.util.*;

public class AdminDriver extends Driver {
    private Admin admin;
    private ListBarang listBarang;
    private ArrayList<Transaksi> listTransaksi;
    private List<Invoice> invoiceSelesai = new ArrayList<>();

    public AdminDriver(Admin admin, ListBarang listBarang) {
        this.admin = admin;
        this.listBarang = listBarang;
        this.listTransaksi = new ArrayList<>();
    }

    @Override
    public void menu() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n=== Menu Admin ===");
            System.out.println("Admin: " + admin.getUsername());
            System.out.println("1. Tambah Barang");
            System.out.println("2. Hapus Barang");
            System.out.println("3. Edit Barang");
            System.out.println("4. Lihat Barang");
            System.out.println("5. Lihat Transaksi");
            System.out.println("6. Terima Transaksi"); // New option
            System.out.println("7. Keluar");
            System.out.print("Pilih opsi: ");
            int opsi = Integer.parseInt(scanner.nextLine());

            switch (opsi) {
                case 1:
                    listBarang.tambahBarang(scanner);
                    break;
                case 2:
                    listBarang.hapusBarang(scanner);
                    break;
                case 3:
                    listBarang.editBarang(scanner);
                    break;
                case 4:
                    listBarang.lihatBarang();
                    break;
                case 5:
                    lihatTransaksi();
                    break;
                case 6:
                    terimaTransaksi(scanner); // New functionality
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    public void tambahTransaksi(Transaksi transaksi) {
        listTransaksi.add(transaksi);
        transaksi.simpanTransaksiKeFile();
    }
    
    private void muatTransaksiDariFile() {
    File file = new File("data/transactions.txt");
    listTransaksi.clear(); // Bersihkan list sebelum memuat ulang data

    if (!file.exists()) {
        return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                String idTransaksi = parts[0].split("=")[1].trim();
                String username = parts[1].split("=")[1].trim();
                int total = Integer.parseInt(parts[2].split("=")[1].trim());
                String status = parts[3].split("=")[1].trim();
                String pembayaranStr = parts[4].split("=")[1].trim();

                // Konversi String pembayaran menjadi objek Pembayaran
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

                // Membaca daftar barang dari string yang ada di file
                List<Barang> barangList = new ArrayList<>();
                for (int i = 5; i < parts.length; i++) {
                    String barangStr = parts[i].trim();
                    // Misalnya format barang = "Pensil (x0)", kita ekstrak nama dan jumlah
                    String namaBarang = barangStr.split(" \\(x")[0];
                    int jumlahBarang = Integer.parseInt(barangStr.split(" \\(x")[1].replace(")", ""));
                    Barang barang = new Barang(namaBarang, jumlahBarang); // Asumsi kelas Barang memiliki konstruktor ini
                    barangList.add(barang);
                }
                // Buat objek Transaksi dan tambahkan ke listTransaksi
                Transaksi transaksi = new Transaksi(idTransaksi, username, total, status, pembayaran, barangList);
                listTransaksi.add(transaksi);
            }
        }
    } catch (IOException e) {
        System.out.println("Error saat membaca file transaksi: " + e.getMessage());
    }
}

    public void lihatTransaksi() {
        muatTransaksiDariFile();
        if (listTransaksi.isEmpty()) {
            System.out.println("Belum ada transaksi.");
        } else {
            System.out.println("=== Daftar Transaksi ===");
            for (Transaksi transaksi : listTransaksi) {
                System.out.println("ID Transaksi: " + transaksi.getIdTransaksi() +
                                   ", Customer: " + transaksi.getUsername() +
                                   ", Total: Rp" + transaksi.getTotal() +
                                   ", Status: " + transaksi.getStatus());
            }
        }
    }

    public void terimaTransaksi(Scanner scanner) {
        System.out.print("Masukkan ID Transaksi yang ingin diterima: ");
        String idTransaksi = scanner.nextLine();

        for (Transaksi transaksi : listTransaksi) {
            if (transaksi.getIdTransaksi().equals(idTransaksi)) {
                if (transaksi.getStatus().equals("PENDING")) {
                    transaksi.setStatus("SELESAI"); // Mengubah status menjadi SELESAI setelah diterima oleh admin
                    transaksi.simpanTransaksiKeFile();  // Simpan perubahan status ke file

                    Invoice invoice = new Invoice(transaksi.getIdTransaksi(), transaksi.getTotal(), "SELESAI", transaksi.getPembayaran());
                    invoiceSelesai.add(invoice);  // Menambahkan invoice selesai ke dalam invoiceSelesai

                    // // Update status invoice di dalam invoiceSelesai
                    // for (Invoice invoice : invoiceSelesai) {
                    //     if (invoice.getIdTransaksi().equals(idTransaksi)) {
                    //         invoice.setStatus("SELESAI");  // Ubah status invoice
                    //     }
                    // }

                    System.out.println("Transaksi ID " + idTransaksi + " telah diterima dan status diperbarui menjadi SELESAI.");
                } else {
                    System.out.println("Transaksi ID " + idTransaksi + " sudah dalam status " + transaksi.getStatus() + ".");
                }
                return;
            }
        }
        System.out.println("Transaksi dengan ID " + idTransaksi + " tidak ditemukan.");
    }
}
