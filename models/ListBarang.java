package models;

import java.io.*;
import java.util.*;

public class ListBarang {
    private List<Barang> barangList = new ArrayList<>();

    public ListBarang() {
        barangList = new ArrayList<>();
        loadProducts(); 
    }

    public void loadProducts() {
        barangList.clear(); 
        try (BufferedReader br = new BufferedReader(new FileReader("data/products.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String nama = parts[1];
                int harga = Integer.parseInt(parts[2]);
                int stok = Integer.parseInt(parts[3]);
                barangList.add(new Barang(id, nama, harga, stok));
            }
        } catch (IOException e) {
            System.out.println("Error membaca file produk.");
        }
    }

    public Barang getBarangById(int id) {
        for (Barang barang : barangList) {
            if (barang.getId() == id) {
                return barang;  
            }
        }
        return null;  
    }

    public List<Barang> getBarangList() {
        return barangList;  
    }

    public void tambahBarang(Scanner scanner) {
        System.out.print("Masukkan ID barang: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Masukkan nama barang: ");
        String nama = scanner.nextLine();
        System.out.print("Masukkan harga barang: ");
        int harga = Integer.parseInt(scanner.nextLine());
        System.out.print("Masukkan stok barang: ");
        int stok = Integer.parseInt(scanner.nextLine());
    
        barangList.add(new Barang(id, nama, harga, stok));
        simpanKeFile();
        System.out.println("Barang berhasil ditambahkan.");
    }

    public void hapusBarang(Scanner scanner) {
        System.out.print("Masukkan ID barang yang ingin dihapus: ");
        int id = Integer.parseInt(scanner.nextLine());
    
        boolean removed = barangList.removeIf(barang -> barang.getId() == id);
        if (removed) {
            simpanKeFile();
            System.out.println("Barang berhasil dihapus.");
        } else {
            System.out.println("Barang dengan ID tersebut tidak ditemukan.");
        }
    }    

    public void editBarang(Scanner scanner) {
        System.out.print("Masukkan ID barang yang ingin diubah: ");
        int id = Integer.parseInt(scanner.nextLine());
    
        for (Barang barang : barangList) {
            if (barang.getId() == id) {
                System.out.print("Masukkan nama baru: ");
                barang.setNama(scanner.nextLine());
                System.out.print("Masukkan harga baru: ");
                barang.setHarga(Integer.parseInt(scanner.nextLine()));
                System.out.print("Masukkan stok baru: ");
                barang.setStok(Integer.parseInt(scanner.nextLine()));
    
                simpanKeFile();  
                System.out.println("Barang berhasil diubah.");
                return;
            }
        }
        System.out.println("Barang dengan ID tersebut tidak ditemukan.");
    }    

    public void lihatBarang() {
        System.out.println("\n=== Daftar Barang ===");
        for (Barang barang : barangList) {
            System.out.println(barang);
        }
    }

    private void simpanKeFile() {
        File file = new File("data/products.txt");
    
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();  
                file.createNewFile();  
            }
    
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (Barang barang : barangList) {
                    bw.write(barang.toStringFile());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error menyimpan data barang.");
        }
    }
    
}
