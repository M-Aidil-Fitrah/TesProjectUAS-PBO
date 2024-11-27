package payments;

import java.util.Scanner;

public class COD extends Pembayaran {
    private String alamatPengiriman;

    @Override
    public String prosesPembayaran(Scanner scanner) {
        if (!verifikasiPembayaran()) {
            return "Pembayaran gagal. Total pembayaran tidak valid.";
        }

        System.out.println("=== Pembayaran via COD ===");
        System.out.print("Masukkan alamat pengiriman: ");
        alamatPengiriman = scanner.nextLine();

        System.out.println("Pesanan Anda akan dikirim ke alamat berikut:");
        System.out.println(alamatPengiriman);
        System.out.println("Harap siapkan uang tunai sejumlah: Rp " + total);

        return "Pesanan dengan metode COD berhasil diproses!";
    }

    @Override
    public String toString() {
        return "COD";
    }
}
