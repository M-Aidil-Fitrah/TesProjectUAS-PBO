package drivers;

import models.*;
import java.io.*;
import java.util.*;

public class AdminDriver extends Driver {
    private Admin admin;
    private ListBarang listBarang;
    private ArrayList<Transaksi> listTransaksi;

    public AdminDriver(Admin admin, ListBarang listBarang) {
        this.admin = admin;
        this.listBarang = listBarang;
        this.listTransaksi = new ArrayList<>();
        muatTransaksiDariFile();
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

    public void lihatTransaksi() {
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

    public void tambahTransaksi(Transaksi transaksi) {
        listTransaksi.add(transaksi);
        simpanTransaksiKeFile(transaksi);
    }

    public void terimaTransaksi(Scanner scanner) {
        System.out.print("Masukkan ID Transaksi yang ingin diterima: ");
        String idTransaksi = scanner.nextLine();

        for (Transaksi transaksi : listTransaksi) {
            if (transaksi.getIdTransaksi().equals(idTransaksi)) {
                transaksi.setStatus("SELESAI");
                simpanTransaksiKeFile(transaksi);
                System.out.println("Transaksi ID " + idTransaksi + " telah diterima.");
                return;
            }
        }
        System.out.println("Transaksi dengan ID " + idTransaksi + " tidak ditemukan.");
    }

    private void muatTransaksiDariFile() {
        File file = new File("data/transactions.txt");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String idTransaksi = parts[0].split("=")[1].trim().replace("'", "");
                    String username = parts[1].split("=")[1].trim().replace("'", "");
                    int total = Integer.parseInt(parts[2].split("=")[1].trim());
                    String status = parts[3].split("=")[1].trim().replace("'", "");

                    Transaksi transaksi = new Transaksi(idTransaksi, username, total, status, null, null);
                    listTransaksi.add(transaksi);
                }
            }
        } catch (IOException e) {
            System.out.println("Error saat membaca file transaksi: " + e.getMessage());
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
        } catch (NullPointerException e) {
            System.out.println("Error: Transaksi memiliki nilai null yang tidak diharapkan. Pastikan semua field telah diinisialisasi.");
        }
    }
}
