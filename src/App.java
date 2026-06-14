import java.util.Scanner;

public class App {
    private static final Scanner sc = new Scanner(System.in);
    private static Dapur dapur = new Dapur();
    private static int jumlahPelanggan = 0;
    private static int nomorBatch = 1;
    private static int idPesanan = 1;


    public static void main(String[] args) {
        while (true) {
        System.out.println("==========================================================");
        System.out.println("                Selamat Datang di KohiSop!                ");
        System.out.println("==========================================================");


        System.out.print("Masukkan nama Anda: ");
        String namaPelanggan = sc.nextLine().trim();
        while (namaPelanggan.isEmpty()) {
            System.out.print("Nama tidak boleh kosong. Masukkan nama Anda: ");
            namaPelanggan = sc.nextLine().trim();
        }

        Member member = KohiSop.cariMember(namaPelanggan);
        boolean memberBaru = false;
        if (member != null) {
            System.out.printf("Member Ditemukan! Kode: %s, Poin Saat Ini: %d%n%n", member.getKode(), member.getPoin());
        } else {
            System.out.println("Anda belum terdaftar sebagai member.");
            System.out.println("Anda akan otomatis terdaftar sebagai member baru setelah transaksi selesai!\n");
            memberBaru = true;
            // generate random kode
            String kodeBaru;
            while (true) {
                kodeBaru = Member.generateRandomCode();
                boolean exists = false;
                for (Member m : KohiSop.getDatabaseMember()) {
                    if (m.getKode().equalsIgnoreCase(kodeBaru)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists)
                    break;
            }
            member = new Member(kodeBaru, namaPelanggan, 0);
        }

        KohiSop kohiSop = new KohiSop();
        KohiSop.tampilkanDaftarMenu();
        System.out.println("\nPilih Minuman");
        System.out.println("Maks " + KohiSop.MAX_JENIS_PER_KATEGORI + " jenis minuman.");
        System.out.println("Ketik 'DONE' jika sudah selesai, 'CC' untuk membatalkan pesanan.");

        while (kohiSop.jumlahJenisDipesan("Minuman") < KohiSop.MAX_JENIS_PER_KATEGORI) {
            System.out.print("Kode Minuman: ");
            String input = sc.nextLine().trim().toUpperCase();
            if (input.equals("CC")) {
                System.out.println("Pesanan dibatalkan.");
                return;
            }
            if (input.equals("DONE"))
                break;
            if (!KohiSop.isKodeValid(input)) {
                System.out.println("Kode tidak valid, coba lagi ya.");
                continue;
            }

            Menu menu = KohiSop.cariMenu(input);
            if (!(menu instanceof Minuman)) {
                System.out.println("Tolong masukkan kode untuk minuman.");
                continue;
            }
            if (kohiSop.sudahDipesan(input)) {
                System.out.println("Minuman ini sudah kamu pilih.");
                continue;
            }
            kohiSop.tambahPesanan(menu, 1);
            System.out.println("=> " + menu.getNamaMenu() + " ditambahkan!");
        }

        if (kohiSop.jumlahJenisDipesan("Minuman") >= KohiSop.MAX_JENIS_PER_KATEGORI) {
            System.out.println("Maksimal 5 jenis minuman ya.");
        }
        System.out.println("\nPilih Makanan");
        System.out.println("Maks " + KohiSop.MAX_JENIS_PER_KATEGORI + " jenis makanan.");
        System.out.println("Ketik 'DONE' jika sudah selesai, 'CC' untuk membatalkan pesanan.");
        while (kohiSop.jumlahJenisDipesan("Makanan") < KohiSop.MAX_JENIS_PER_KATEGORI) {
            System.out.print("Kode Makanan: ");
            String input = sc.nextLine().trim().toUpperCase();
            if (input.equals("CC")) {
                System.out.println("Pesanan dibatalkan.");
                return;
            }
            if (input.equals("DONE"))
                break;
            if (!KohiSop.isKodeValid(input)) {
                System.out.println("Kode tidak valid, coba lagi ya.");
                continue;
            }

            Menu menu = KohiSop.cariMenu(input);
            if (!(menu instanceof Makanan)) {
                System.out.println("Tolong masukkan kode untuk makanan.");
                continue;
            }
            if (kohiSop.sudahDipesan(input)) {
                System.out.println("Makanan ini sudah kamu pilih.");
                continue;
            }
            kohiSop.tambahPesanan(menu, 1);
            System.out.println("=> " + menu.getNamaMenu() + " ditambahkan!");
        }

        if (kohiSop.jumlahJenisDipesan("Makanan") >= KohiSop.MAX_JENIS_PER_KATEGORI) {
            System.out.println("Maksimal 5 jenis makanan ya.");
        }
        if (kohiSop.pesananKosong()) {
            System.out.println("Kamu belum pesan apa-apa. Keluar program...");
            return;
        }
        kohiSop.urutkanPesanan();
        for (int i = 0; i < kohiSop.getJumlahPesanan(); i++) {
            kohiSop.getPesanan(i).setKuantitas(0);
        }

        System.out.println("\n[Input Kuantitas]");
        System.out.println("Enter = 1 porsi (default) | 0 atau S = skip | CC = batalkan");
        for (int i = 0; i < kohiSop.getJumlahPesanan(); i++) {
            ItemPesanan ip = kohiSop.getPesanan(i);
            Menu menu = ip.getMenu();
            int maxQty = menu.getMaxKuantitas();
            System.out.println();
            kohiSop.tampilkanTabelPesanan();

            System.out.printf("Kuantitas [%s] %s (maks %d, default 1): ",
                    menu.getKode(), menu.getNamaMenu(), maxQty);
            String inputQty = sc.nextLine().trim();

            // batal
            if (inputQty.equalsIgnoreCase("CC")) {
                System.out.println("Pesanan dibatalkan.");
                return;
            }

            // skip
            if (inputQty.equalsIgnoreCase("S") || inputQty.equals("0")) {
                ip.setKuantitas(0);
                System.out.println("=> " + menu.getNamaMenu() + " diskip.");
                continue;
            }

            // enter aja langsung default 1
            if (inputQty.isEmpty()) {
                ip.setKuantitas(1);
                System.out.println("=> Kuantitas set 1");
                continue;
            }

            // ambil input angkanya
            boolean valid = false;
            while (!valid) {
                try {
                    int qty = Integer.parseInt(inputQty);

                    if (qty < 0) {
                        System.out.print("Jangan masukkan angka negatif ya, masukkan lagi: ");
                    } else if (qty == 0) {
                        ip.setKuantitas(0);
                        System.out.println("=> " + menu.getNamaMenu() + " diskip.");
                        valid = true;
                    } else if (qty > maxQty) {
                        System.out.printf("Maksimal hanya %d porsi, masukkan lagi: ", maxQty);
                    } else {
                        ip.setKuantitas(qty);
                        System.out.printf("=> %s x%d%n", menu.getNamaMenu(), qty);
                        valid = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.print("Harus masukkan angka ya! Masukkan lagi: ");
                }

                if (!valid) {
                    inputQty = sc.nextLine().trim();
                    if (inputQty.equalsIgnoreCase("CC")) {
                        System.out.println("Pesanan dibatalkan.");
                        return;
                    }
                    if (inputQty.equalsIgnoreCase("S") || inputQty.equals("0")) {
                        ip.setKuantitas(0);
                        System.out.println("=> " + menu.getNamaMenu() + " diskip.");
                        valid = true;
                    }
                    if (inputQty.isEmpty()) {
                        ip.setKuantitas(1);
                        System.out.println("=> Kuantitas set 1");
                        valid = true;
                    }
                }
            }
        }

        // buang item yang tidak jadi dipesan
        kohiSop.hapusPesananKosong();

        if (kohiSop.pesananKosong()) {
            System.out.println("Semua item di skip. Keluar program...");
            return;
        }

        // bebas pajak jika sudah member dan ada kode A
        boolean bebasPajak = (member != null && !memberBaru && member.getKode().toUpperCase().contains("A"));
        for (int i = 0; i < kohiSop.getJumlahPesanan(); i++) {
            kohiSop.getPesanan(i).setBebasPajak(bebasPajak);
        }

        // detail pesanan dan hitung total sementara
        System.out.println("\n[Detail Pesanan]");
        double totalSementara = 0;
        double totalPajak = 0;
        for (int i = 0; i < kohiSop.getJumlahPesanan(); i++) {
            ItemPesanan ip = kohiSop.getPesanan(i);
            Menu m = ip.getMenu();
            int qty = ip.getKuantitas();
            int totalHargaItem = ip.getTotalHarga();
            double pajak = ip.getPajak();
            double subtotal = ip.getTotalDenganPajak();

            int persen = (totalHargaItem > 0) ? (int) Math.round(Math.abs(pajak) / totalHargaItem * 100) : 0;

            if (pajak < 0) {
                System.out.printf("- %s (x%d) : Rp %,d - Diskon %d%% Rp %,.0f = Rp %,.0f%n",
                        m.getNamaMenu(), qty, totalHargaItem, persen, -pajak, subtotal);
            } else if (pajak > 0) {
                System.out.printf("- %s (x%d) : Rp %,d + Pajak %d%% Rp %,.0f = Rp %,.0f%n",
                        m.getNamaMenu(), qty, totalHargaItem, persen, pajak, subtotal);
            } else {
                System.out.printf("- %s (x%d) : Rp %,d (Bebas Pajak)%n",
                        m.getNamaMenu(), qty, totalHargaItem);
            }

            totalPajak += pajak;
            totalSementara += subtotal;
        }
        System.out.println("--------------------------------------------------");
        System.out.printf("Total Pesanan : Rp %,.0f%n", totalSementara - totalPajak);
        System.out.printf("Total Pajak   : Rp %,.0f%n", totalPajak);
        System.out.printf("Total Tagihan : Rp %,.0f%n", totalSementara);

        // pilih channel pembayaran
        System.out.println("\n[Pilih Channel Pembayaran]");
        System.out.println("1. Tunai (Tanpa diskon)");
        System.out.println("2. QRIS (Diskon 5%)");
        System.out.println("3. eMoney (Diskon 7%, Biaya Admin 20 IDR)");

        ChannelPembayaran channel = null;
        while (channel == null) {
            System.out.print("Pilihan (1/2/3), Enter untuk Tunai (default), atau 'CC' untuk batal: ");
            String inputChannel = sc.nextLine().trim().toUpperCase();

            if (inputChannel.equals("CC")) {
                System.out.println("Pesanan dibatalkan.");
                return;
            }

            if (inputChannel.isEmpty()) {
                channel = new Tunai();
                System.out.println("=> Channel Pembayaran set Tunai");
                break;
            }

            switch (inputChannel) {
                case "1":
                    channel = new Tunai();
                    System.out.println("=> Channel Pembayaran set Tunai");
                    break;
                case "2":
                    channel = new QRIS();
                    System.out.println("=> Channel Pembayaran set QRIS");
                    break;
                case "3":
                    channel = new Emoney();
                    System.out.println("=> Channel Pembayaran set eMoney");
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }

        // cek saldo jika perlu
        if (channel.butuhCekSaldo()) {
            double diskon = channel.hitungDiskon(totalSementara);
            double admin = channel.getBiayaAdmin();
            double totalSebelumPoin = totalSementara - diskon + admin;
            double potonganPoin = 0;
            if (!memberBaru) {
                int poinSebelum = member.getPoin();
                double nilaiPoin = poinSebelum * 2.0;
                if (nilaiPoin >= totalSebelumPoin) {
                    potonganPoin = totalSebelumPoin;
                } else {
                    potonganPoin = nilaiPoin;
                }
            }
            double totalAkhir = totalSebelumPoin - potonganPoin;

            boolean saldoCukup = false;
            while (!saldoCukup) {
                System.out.printf("Total tagihan akhir Anda: Rp %,.0f.%n", totalAkhir);
                System.out.print("Masukkan jumlah saldo Anda (atau 'CC' untuk batal): ");
                String inputSaldo = sc.nextLine().trim().toUpperCase();

                if (inputSaldo.equals("CC")) {
                    System.out.println("Pesanan dibatalkan.");
                    return;
                }

                try {
                    double saldo = Double.parseDouble(inputSaldo);
                    if (saldo < totalAkhir) {
                        System.out.println("Saldo tidak mencukupi!");
                    } else {
                        System.out.println("=> Saldo mencukupi. Memproses pembayaran...");
                        saldoCukup = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Harap masukkan angka yang valid.");
                }
            }
        }

        // pilih mata uang
        System.out.println("\nPilih Mata Uang Pembayaran");
        System.out.println("1. IDR (Default)");
        System.out.println("2. USD (1 USD = 15 IDR)");
        System.out.println("3. JPY (10 JPY = 1 IDR)");
        System.out.println("4. MYR (1 MYR = 4 IDR)");
        System.out.println("5. EUR (1 EUR = 14 IDR)");

        MataUang mataUang = null;
        while (mataUang == null) {
            System.out.print("Pilihan (1-5), Enter untuk IDR (default), atau 'CC' untuk batal: ");
            String inputUang = sc.nextLine().trim().toUpperCase();

            if (inputUang.equals("CC")) {
                System.out.println("Pesanan dibatalkan.");
                return;
            }

            if (inputUang.isEmpty()) {
                mataUang = new IDR();
                System.out.println("=> Mata Uang set IDR");
                break;
            }

            switch (inputUang) {
                case "1":
                    mataUang = new IDR();
                    System.out.println("=> Mata Uang set IDR");
                    break;
                case "2":
                    mataUang = new USD();
                    System.out.println("=> Mata Uang set USD");
                    break;
                case "3":
                    mataUang = new JPY();
                    System.out.println("=> Mata Uang set JPY");
                    break;
                case "4":
                    mataUang = new MYR();
                    System.out.println("=> Mata Uang set MYR");
                    break;
                case "5":
                    mataUang = new EUR();
                    System.out.println("=> Mata Uang set EUR");
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }

        // cetak kuitansi
        Kuitansi.cetak(kohiSop, channel, mataUang, member);

        // dapur
        jumlahPelanggan++;
        dapur.tambahPesanan(kohiSop.getDaftarPesanan());

        if (jumlahPelanggan % 3 != 0) {

        System.out.printf(
            "\nTim dapur menunggu pesanan lainnya... (%d/3 pesanan)\n",
            jumlahPelanggan % 3
        );

        } else {

        System.out.println(
            "\n==================================================");
        System.out.println(
            "Pesanan 3 pelanggan sudah terkumpul!");
        System.out.println(
            "==================================================");

        System.out.print(
            "Lihat proses tim dapur? (Y/N): ");

        String lihat =
                sc.nextLine().trim().toUpperCase();

        if (lihat.equals("Y")) {

            System.out.println(
                "\nMemuat proses pesanan...\n");

            dapur.prosesPesanan();
        }

        dapur.kosongkanAntrian();

        nomorBatch++;
    }

        if (memberBaru) {
        KohiSop.daftarMemberBaru(member);
        } else {
        KohiSop.saveDatabaseMember();

        }
        System.out.println();
        System.out.println("Tekan ENTER untuk memulai pesanan baru");
        System.out.println("atau ketik 'X' untuk keluar.");

            String pilihan =
            sc.nextLine().trim().toUpperCase();

        if (pilihan.equals("X")) {

        System.out.println(
            "\nTerima kasih telah menggunakan KohiSop!");

        break; 
      }
    }
  }
}