package payments;

import java.util.Random;
import java.util.Scanner;

public class QRIS extends Pembayaran {
    private String kodeQR;

    @Override
    public String prosesPembayaran(Scanner scanner) {
        if (!verifikasiPembayaran()) {
            return "Pembayaran gagal. Total pembayaran tidak valid.";
        }

        System.out.println("=== Pembayaran via QRIS ===");
        kodeQR = generateKodeQR();
        System.out.println("Kode QR telah dibuat. Silakan pindai kode QR berikut untuk membayar:");
        System.out.println("[ Kode QR: " + kodeQR + " ]");

        System.out.println("Menunggu pembayaran...");
        try {
            Thread.sleep(3000); 
        } catch (InterruptedException e) {
            return "Terjadi kesalahan saat memproses pembayaran.";
        }

        return "Pembayaran via QRIS berhasil!";
    }

    private String generateKodeQR() {
        Random random = new Random();
        return "QR-" + random.nextInt(1000000); 
    }

    @Override
    public String toString() {
        return "QRIS";
    }
}
