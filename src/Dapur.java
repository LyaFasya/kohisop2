import java.util.*;

class Dapur {

    private PriorityQueue<ItemPesanan> antrianMakanan;
    private Stack<ItemPesanan> antrianMinuman;

    public Dapur() {

        antrianMakanan = new PriorityQueue<>(
                (a, b) -> Integer.compare(
                        b.getMenu().getHarga(),
                        a.getMenu().getHarga()));

        antrianMinuman = new Stack<>();
    }

    public void tambahPesanan(ArrayList<ItemPesanan> daftarPesanan) {

        for (ItemPesanan item : daftarPesanan) {

            if (item.getMenu() instanceof Makanan) {

                antrianMakanan.offer(item);

            } else if (item.getMenu() instanceof Minuman) {

                antrianMinuman.push(item);
            }
        }
    }

    public ArrayList<ItemPesanan> getAntrianMakanan() {

        PriorityQueue<ItemPesanan> tempQueue =
                new PriorityQueue<>(antrianMakanan);

        ArrayList<ItemPesanan> daftarMakanan =
                new ArrayList<>();

        while (!tempQueue.isEmpty()) {

            daftarMakanan.add(tempQueue.poll());
        }

        return daftarMakanan;
    }

    public ArrayList<ItemPesanan> getAntrianMinuman() {

        ArrayList<ItemPesanan> daftarMinuman =
                new ArrayList<>();

        for (int i = antrianMinuman.size() - 1;
             i >= 0;
             i--) {

            daftarMinuman.add(
                    antrianMinuman.get(i));
        }

        return daftarMinuman;
    }

    public void prosesPesanan() {
        

        System.out.println(
                "\n======================== TIM DAPUR KOHISOP =======================");

        System.out.println(
                "                       URUTAN PROSES PESANAN");

        System.out.println(
                "------------------------------------------------------------------");

        System.out.println(
            
                "\nANTRIAN PROSES MAKANAN :");

        System.out.println(
                "------------------------------------------------------------------");

        System.out.printf(
                "%-5s | %-30s | %-10s | Qty%n",
                "Kode",
                "Nama",
                "Harga");

        System.out.println(
                "------------------------------------------------------------------");

        ArrayList<ItemPesanan> makananList =
                getAntrianMakanan();

        if (makananList.isEmpty()) {

            System.out.println(
                    "Tidak ada pesanan makanan.");

        } else {

            for (ItemPesanan item : makananList) {

                System.out.printf(
                        "%-5s | %-30s | Rp %-8d | %d porsi%n",

                        item.getMenu().getKode(),

                        item.getMenu().getNamaMenu(),

                        item.getMenu().getHarga(),

                        item.getKuantitas());
            }
        }

        System.out.println(
                "------------------------------------------------------------------");

        System.out.println(
                "\nANTRIAN PROSES MINUMAN :");

        System.out.println(
                "------------------------------------------------------------------");

        System.out.printf(
                "%-5s | %-35s | %-10s | Qty%n",

                "Kode",

                "Nama",

                "Harga");

        System.out.println(
                "------------------------------------------------------------------");

        ArrayList<ItemPesanan> minumanList =
                getAntrianMinuman();

        if (minumanList.isEmpty()) {

            System.out.println(
                    "Tidak ada pesanan minuman.");

        } else {

            for (ItemPesanan item : minumanList) {

                System.out.printf(
                        "%-5s | %-35s | Rp %-7d | %d porsi%n",

                        item.getMenu().getKode(),

                        item.getMenu().getNamaMenu(),

                        item.getMenu().getHarga(),

                        item.getKuantitas());
            }
        }

        System.out.println(
                "------------------------------------------------------------------");
    }

    public void kosongkanAntrian() {

        antrianMakanan.clear();

        antrianMinuman.clear();
    }
}