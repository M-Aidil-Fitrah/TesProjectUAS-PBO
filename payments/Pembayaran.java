package payments;

import java.util.Scanner;

public abstract class Pembayaran {
    protected int total; 

    public void setTotal(int total) {
        this.total = total;
    }

    public abstract String prosesPembayaran(Scanner scanner);

    public boolean verifikasiPembayaran() {
        return total > 0; 
    }
}
