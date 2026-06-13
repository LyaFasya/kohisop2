import java.util.ArrayList;
import java.util.List;

public class KohiSop {
    private static final List<Menu> DAFTAR_MENU = new ArrayList<>();
    static {
        // minuman
        DAFTAR_MENU.add(new Minuman("A1", "Caffe Latte", 46000));
        DAFTAR_MENU.add(new Minuman("A2", "Cappuccino", 46000));
        DAFTAR_MENU.add(new Minuman("E1", "Caffe Americano", 37000));
        DAFTAR_MENU.add(new Minuman("E2", "Caffe Mocha", 55000));
        DAFTAR_MENU.add(new Minuman("E3", "Caramel Macchiato", 59000));
        DAFTAR_MENU.add(new Minuman("E4", "Asian Dolce Latte", 55000));
        DAFTAR_MENU.add(new Minuman("E5", "Double Shots Iced Shaken Espresso", 50000));
        DAFTAR_MENU.add(new Minuman("B1", "Freshly Brewed Coffee", 23000));
        DAFTAR_MENU.add(new Minuman("B2", "Vanilla Sweet Cream Cold Brew", 50000));
        DAFTAR_MENU.add(new Minuman("B3", "Cold Brew", 44000));
        // makanan
        DAFTAR_MENU.add(new Makanan("M1", "Petemania Pizza", 112000));
        DAFTAR_MENU.add(new Makanan("M2", "Mie Rebus Super Mario", 35000));
        DAFTAR_MENU.add(new Makanan("M3", "Ayam Bakar Goreng Rebus Spesial", 72000));
        DAFTAR_MENU.add(new Makanan("M4", "Soto Kambing Iga Guling", 124000));
        DAFTAR_MENU.add(new Makanan("S1", "Singkong Bakar A La Carte", 37000));
        DAFTAR_MENU.add(new Makanan("S2", "Ubi Cilembu Bakar Arang", 58000));
        DAFTAR_MENU.add(new Makanan("S3", "Tempe Mendoan", 18000));
        DAFTAR_MENU.add(new Makanan("S4", "Tahu Bakso Extra Telur", 28000));

        // sort menu makanan dan minuman
        DAFTAR_MENU.sort((m1, m2) -> {
            int catCompare = m1.getKategori().compareTo(m2.getKategori());
            if (catCompare != 0) {
                return catCompare;
            }
            return m1.getKode().compareTo(m2.getKode());
        });
    }

    // database member
    private static final List<Member> DATABASE_MEMBER = new ArrayList<>();
    static {
        DATABASE_MEMBER.add(new Member("A23FB9", "Budi", 150));
        DATABASE_MEMBER.add(new Member("C78DE1", "Andi", 50));
    }

    public static final int MAX_JENIS_PER_KATEGORI = 5;
    private final List<ItemPesanan> pesanan = new ArrayList<>();

    public static List<Member> getDatabaseMember() {
        return DATABASE_MEMBER;
    }

    public static Member cariMember(String nama) {
        for (Member m : DATABASE_MEMBER) {
            if (m.getNama().equalsIgnoreCase(nama)) {
                return m;
            }
        }
        return null;
    }

    public static void daftarMemberBaru(Member m) {
        DATABASE_MEMBER.add(m);
    }

    public static boolean isKodeValid(String kode) {
        for (Menu m : DAFTAR_MENU) {
            if (m.getKode().equalsIgnoreCase(kode)) {
                return true;
            }
        }
        return false;
    }

    public static Menu cariMenu(String kode) {
        for (Menu m : DAFTAR_MENU) {
            if (m.getKode().equalsIgnoreCase(kode)) {
                return m;
            }
        }
        return null;
    }

    public boolean sudahDipesan(String kode) {
        for (ItemPesanan ip : pesanan) {
            if (ip.getMenu().getKode().equalsIgnoreCase(kode)) {
                return true;
            }
        }
        return false;
    }

    public int jumlahJenisDipesan(String kategori) {
        int count = 0;
        for (ItemPesanan ip : pesanan) {
            if (ip.getMenu().getKategori().equalsIgnoreCase(kategori)) {
                count++;
            }
        }
        return count;
    }

    public void tambahPesanan(Menu menu, int kuantitas) {
        pesanan.add(new ItemPesanan(menu, kuantitas));
    }

    public ItemPesanan getPesanan(int indeks) {
        return pesanan.get(indeks);
    }

    public int getJumlahPesanan() {
        return pesanan.size();
    }

    public boolean pesananKosong() {
        return pesanan.isEmpty();
    }

    public void hapusPesananKosong() {
        pesanan.removeIf(ip -> ip.getKuantitas() <= 0);
    }

    // urutkan pesanan dari yg terkecil
    public void urutkanPesanan() {
        pesanan.sort((ip1, ip2) -> {
            int catCompare = ip1.getMenu().getKategori().compareTo(ip2.getMenu().getKategori());
            if (catCompare != 0) {
                return catCompare;
            }
            return Integer.compare(ip1.getMenu().getHarga(), ip2.getMenu().getHarga());
        });
    }

    // tampilan menu
    public static void tampilkanDaftarMenu() {
        String garis = "=".repeat(58);
        System.out.println(garis);
        System.out.println("                       MENU KOHISOP");
        System.out.println(garis);

        // makanan
        System.out.printf("%-4s | %-36s | %s%n", "Kode", "Nama Menu Makanan", "Harga (Rp)");
        System.out.println("-".repeat(58));
        for (Menu m : DAFTAR_MENU) {
            if (m instanceof Makanan) {
                System.out.printf("%-4s | %-36s | Rp %,d%n", m.getKode(), m.getNamaMenu(), m.getHarga());
            }
        }

        System.out.println();

        // minuman
        System.out.printf("%-4s | %-36s | %s%n", "Kode", "Nama Menu Minuman", "Harga (Rp)");
        System.out.println("-".repeat(58));
        for (Menu m : DAFTAR_MENU) {
            if (m instanceof Minuman) {
                System.out.printf("%-4s | %-36s | Rp %,d%n", m.getKode(), m.getNamaMenu(), m.getHarga());
            }
        }
        System.out.println(garis);
    }

    // tabel pesanan untuk isi kuantitas
    public void tampilkanTabelPesanan() {
        if (pesanan.isEmpty()) return;
        String garis = "-".repeat(65);

        // makanan
        boolean adaMakanan = false;
        for (ItemPesanan ip : pesanan) {
            if (ip.getMenu() instanceof Makanan) {
                adaMakanan = true;
                break;
            }
        }
        if (adaMakanan) {
            System.out.println(garis);
            System.out.printf("%-4s | %-33s | %-10s | %s%n", "Kode", "Makanan", "Harga", "Kuantitas");
            System.out.println(garis);
            for (ItemPesanan ip : pesanan) {
                if (ip.getMenu() instanceof Makanan) {
                    System.out.printf("%-4s | %-33s | Rp %,8d | %d%n",
                        ip.getMenu().getKode(),
                        ip.getMenu().getNamaMenu(),
                        ip.getMenu().getHarga(),
                        ip.getKuantitas());
                }
            }
        }

        // minuman
        boolean adaMinuman = false;
        for (ItemPesanan ip : pesanan) {
            if (ip.getMenu() instanceof Minuman) {
                adaMinuman = true;
                break;
            }
        }
        if (adaMinuman) {
            System.out.println(garis);
            System.out.printf("%-4s | %-33s | %-10s | %s%n", "Kode", "Minuman", "Harga", "Kuantitas");
            System.out.println(garis);
            for (ItemPesanan ip : pesanan) {
                if (ip.getMenu() instanceof Minuman) {
                    System.out.printf("%-4s | %-33s | Rp %,8d | %d%n",
                        ip.getMenu().getKode(),
                        ip.getMenu().getNamaMenu(),
                        ip.getMenu().getHarga(),
                        ip.getKuantitas());
                }
            }
        }
        System.out.println(garis);
    }
}
