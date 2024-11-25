import models.*;
import drivers.*;

import java.io.*;
import java.util.*;

public class Main {
    private static List<Akun> akunList = new ArrayList<>();
    private static ListBarang listBarang = new ListBarang();

    public static void main(String[] args) {
        loadUsers();
        listBarang.loadProducts();

        Scanner scanner = new Scanner(System.in); 
        try {
            while (true) {
                System.out.println("\n=== Sistem Perbelanjaan Online ===");
                System.out.println("1. Login");
                System.out.println("2. Daftar Akun Baru");
                System.out.println("3. Keluar");
                System.out.print("Pilih opsi: ");
                int opsi = Integer.parseInt(scanner.nextLine());

                if (opsi == 1) {
                    Akun akun = login(scanner);  
                    if (akun != null) {
                        if (akun instanceof Admin) {
                            AdminDriver adminDriver = new AdminDriver((Admin) akun, listBarang);
                            adminDriver.menu();
                        } else if (akun instanceof Customer) {
                            CustomerDriver customerDriver = new CustomerDriver((Customer) akun, listBarang);
                            customerDriver.menu();
                        }
                    }
                } else if (opsi == 2) {
                    daftarAkunBaru(scanner); 
                } else if (opsi == 3) {
                    System.out.println("Terima kasih telah menggunakan sistem!");
                    break;
                } else {
                    System.out.println("Pilihan tidak valid!");
                }
            }
        } finally {
            scanner.close(); 
        }
    }

    private static void loadUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String role = parts[0];
                String id = parts[1];
                String username = parts[2];
                String password = parts[3];
    
                if (role.equalsIgnoreCase("admin")) {
                    akunList.add(new Admin(id, username, password));
                } else if (role.equalsIgnoreCase("customer")) {
                    akunList.add(new Customer(id, username, password));
                }
            }
        } catch (IOException e) {
            System.out.println("Error: Tidak dapat membaca file pengguna.");
        }
    }

    private static Akun login(Scanner scanner) {
        System.out.print("Masukkan username: ");
        String username = scanner.nextLine();
        System.out.print("Masukkan password: ");
        String password = scanner.nextLine();

        for (Akun akun : akunList) {
            if (akun.getUsername().equals(username) && akun.getPassword().equals(password)) {
                System.out.println("Login berhasil! Selamat datang, " + username);
                return akun;
            }
        }
        System.out.println("Login gagal! Username atau password salah.");
        return null;
    }

    private static void daftarAkunBaru(Scanner scanner) {
        System.out.println("\n=== Daftar Akun Baru ===");
        System.out.println("Pilih jenis akun:");
        System.out.println("1. Admin");
        System.out.println("2. Customer");
        System.out.print("Masukkan pilihan (1/2): ");
        int jenisAkun = Integer.parseInt(scanner.nextLine());

        if (jenisAkun != 1 && jenisAkun != 2) {
            System.out.println("Pilihan tidak valid! Pendaftaran dibatalkan.");
            return;
        }

        System.out.print("Masukkan username baru: ");
        String username = scanner.nextLine();
        System.out.print("Masukkan password baru: ");
        String password = scanner.nextLine();

        for (Akun akun : akunList) {
            if (akun.getUsername().equals(username)) {
                System.out.println("Pendaftaran gagal! Username sudah digunakan.");
                return;
            }
        }

        String id = generateRandomIdTransaksi(); 
    
        Akun akunBaru;
        if (jenisAkun == 1) {
            akunBaru = new Admin(id, username, password);
            System.out.println("Pendaftaran berhasil! Akun Admin telah dibuat.");
        } else {
            akunBaru = new Customer(id, username, password);
            System.out.println("Pendaftaran berhasil! Akun Customer telah dibuat.");
        }

        akunList.add(akunBaru);
        saveUserToFile(akunBaru);
    }

    private static void saveUserToFile(Akun akun) {
        File file = new File("data/users.txt");
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();  
                file.createNewFile();
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                String role = (akun instanceof Admin) ? "admin" : "customer";
                writer.write(role + "," + akun.getId() + "," + akun.getUsername() + "," + akun.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error: Tidak dapat menyimpan akun baru ke file.");
        }
    }

    private static String generateRandomIdTransaksi() {
    Random random = new Random();

    char huruf = (char) ('A' + random.nextInt(26));

    int angka = random.nextInt(1000);

    return String.format("%c%03d", huruf, angka);
}

}
