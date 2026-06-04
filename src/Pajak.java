public class Pajak {
    public static double hitungPajak(Menu menu, int totalHarga) {
        if (menu instanceof Makanan) {
            // >50  dapat diskon 8% dan <=50 dapat diskon 11%.
            if (menu.getHarga() > 50000) {
                return totalHarga * 0.08;
            } else {
                return totalHarga * 0.11;
            }
        } else if (menu instanceof Minuman) {
            // <50 tidak ada pajak
            if (menu.getHarga() < 50000) {
                return 0.0;
            } 
            // 50 - 55 dapat diskon 8%
            else if (menu.getHarga() <= 55000) {
                return totalHarga * 0.08;
            } 
            // >55 dapat diskon 11%
            else {
                return totalHarga * 0.11;
            }
        }
        return 0;
    }
}
