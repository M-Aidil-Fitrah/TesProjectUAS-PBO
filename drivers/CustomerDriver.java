package drivers;

import models.*;
import java.util.Scanner;

public class CustomerDriver extends Driver {
    private Customer customer;
    private ListBarang listBarang;

    public CustomerDriver(Customer customer, ListBarang listBarang) {
        this.customer = customer;
        this.listBarang = listBarang;
    }

    @Override
    public void menu() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n=== Menu Customer ===");
            System.out.println("1. Lihat Barang");
            System.out.println("2. Tambah ke Keranjang");
            System.out.println("3. Checkout");
            System.out.println("4. Lihat Riwayat Belanja");
            System.out.println("5. Lihat Status Transaksi");
            System.out.println("6. Keluar");
            System.out.print("Pilih opsi: ");
            int opsi = Integer.parseInt(scanner.nextLine());

            switch (opsi) {
                case 1:
                    listBarang.lihatBarang();
                    break;
                case 2:
                    tambahKeKeranjang(scanner);
                    break;
                case 3:
                    customer.checkout(listBarang, scanner);
                    break;
                case 4:
                    customer.lihatRiwayat();
                    break;
                case 5:
                    customer.lihatStatusTransaksi(); // New functionality
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    } 

    private void tambahKeKeranjang(Scanner scanner) {
        listBarang.lihatBarang();
    
        System.out.println("\nTambahkan barang ke keranjang");
        System.out.print("Masukkan ID barang: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Masukkan jumlah: ");
        int jumlah = Integer.parseInt(scanner.nextLine());
    
        Barang barang = listBarang.getBarangById(id);
        if (barang != null) {
            if (jumlah > barang.getStok()) {
                System.out.println("Jumlah yang ingin ditambahkan melebihi stok yang tersedia. Stok yang tersedia: " + barang.getStok());
            } else {
                customer.tambahKeKeranjang(barang, jumlah);
            }
        } else {
            System.out.println("Barang dengan ID tersebut tidak ditemukan.");
        }
    }    
}
