package payments;

import java.util.Scanner;

public class Bank extends Pembayaran {
    private String namaBank;
    private String nomorRekening;

    @Override
    public String prosesPembayaran(Scanner scanner) {   
        if (!verifikasiPembayaran()) {
            return "Pembayaran gagal. Total pembayaran tidak valid.";
        }

        System.out.println("=== Pembayaran via Bank ===");
        System.out.print("Masukkan nama bank: ");
        namaBank = scanner.nextLine();  
        System.out.print("Masukkan nomor rekening Anda: ");
        nomorRekening = scanner.nextLine();  

        System.out.println("Mengirim permintaan ke bank...");
        try {
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            return "Terjadi kesalahan saat memproses pembayaran.";
        }

        return "Pembayaran via Bank berhasil! Nama Bank: " + namaBank + ", Rekening: " + nomorRekening;
    }

    @Override
    public String toString() {
        return "Bank";
    }
}
