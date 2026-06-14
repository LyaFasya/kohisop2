public class Kuitansi {
    public static void cetak(KohiSop kohiSop, ChannelPembayaran channel, MataUang mataUang, Member member) {
        boolean sudahMember = false;
        if (member != null) {
            for (Member m : KohiSop.getDatabaseMember()) {
                if (m.getKode().equalsIgnoreCase(member.getKode())) {
                    sudahMember = true;
                    break;
                }
            }
        }
        boolean bebasPajak = sudahMember && member.getKode().toUpperCase().contains("A");
        for (int i = 0; i < kohiSop.getJumlahPesanan(); i++) {
            kohiSop.getPesanan(i).setBebasPajak(bebasPajak);
        }

        System.out.println("\n=======================================================");
        System.out.println("                   KUITANSI PEMBELIAN");
        System.out.println("=======================================================");

        double totalHargaLuarPajak = 0;
        double totalPajakKeseluruhan = 0;

        // cetak makanan
        System.out.println("\n[ MAKANAN ]");
        boolean hasMakanan = false;
        for (int i = 0; i < kohiSop.getJumlahPesanan(); i++) {
            ItemPesanan ip = kohiSop.getPesanan(i);
            if (ip.getMenu() instanceof Makanan) {
                hasMakanan = true;
                tampilkanItemKuitansi(ip);
                totalHargaLuarPajak += ip.getTotalHarga();
                totalPajakKeseluruhan += ip.getPajak();
            }
        }
        if (!hasMakanan)
            System.out.println("- Tidak ada makanan yang dipesan.\n");

        // cetak minuman
        System.out.println("[ MINUMAN ]");
        boolean hasMinuman = false;
        for (int i = 0; i < kohiSop.getJumlahPesanan(); i++) {
            ItemPesanan ip = kohiSop.getPesanan(i);
            if (ip.getMenu() instanceof Minuman) {
                hasMinuman = true;
                tampilkanItemKuitansi(ip);
                totalHargaLuarPajak += ip.getTotalHarga();
                totalPajakKeseluruhan += ip.getPajak();
            }
        }
        if (!hasMinuman)
            System.out.println("- Tidak ada minuman yang dipesan.\n");

        double totalHargaDenganPajak = totalHargaLuarPajak + totalPajakKeseluruhan;

        double diskon = channel.hitungDiskon(totalHargaDenganPajak);
        double admin = channel.getBiayaAdmin();
        double totalSebelumPoin = totalHargaDenganPajak - diskon + admin;

        int poinSebelum = member.getPoin();
        int poinYangDigunakan = 0;
        double potonganPoin = 0;

        if (sudahMember && mataUang.getKode().equals("IDR")) {
            double nilaiPoin = poinSebelum * 2.0;
            if (nilaiPoin >= totalSebelumPoin) {
                potonganPoin = totalSebelumPoin;
                poinYangDigunakan = (int) Math.ceil(totalSebelumPoin / 2.0);
            } else {
                potonganPoin = nilaiPoin;
                poinYangDigunakan = poinSebelum;
            }
        }

        double totalAkhirIDR = totalSebelumPoin - potonganPoin;

        double totalAkhirKonversi = mataUang.konversiDariIDR(totalAkhirIDR);
        double totalLuarPajakKonversi = mataUang.konversiDariIDR(totalHargaLuarPajak);

        // hitung poin member
        int poinDiperoleh = (int) (totalAkhirIDR / 10);
        boolean doublePoin = member.getKode().toUpperCase().contains("A");
        if (doublePoin) {
            poinDiperoleh *= 2;
        }
        member.setPoin(poinSebelum - poinYangDigunakan + poinDiperoleh);
        int poinSetelah = member.getPoin();

        System.out.println("-------------------------------------------------------");
        System.out.printf("%-36s : Rp %,12.0f%n", "Total Harga (di luar pajak)", totalHargaLuarPajak);
        System.out.printf("%-36s : Rp %,12.0f%n", "Total Pajak Keseluruhan", totalPajakKeseluruhan);
        System.out.printf("%-36s : Rp %,12.0f%n", "Total Harga (dengan pajak)", totalHargaDenganPajak);
        System.out.println("-------------------------------------------------------");
        System.out.printf("%-36s : Rp %,12.0f%n", "Diskon (" + channel.getNama() + ")", diskon);
        System.out.printf("%-36s : Rp %,12.0f%n", "Biaya Admin", admin);
        System.out.println("-------------------------------------------------------");

        // total tagihan sebelum poin
        double totalSebelumPoinKonversi = mataUang.konversiDariIDR(totalSebelumPoin);
        System.out.printf("%-36s : Rp %,12.0f%n", "Total Tagihan (IDR)", totalSebelumPoin);
        if (!mataUang.getKode().equals("IDR")) {
            System.out.printf("%-36s : %s %,12.2f%n", "Total Tagihan (" + mataUang.getKode() + ")",
                    mataUang.getKode(), totalSebelumPoinKonversi);
        }
        System.out.println("-------------------------------------------------------");

        // poin member
        System.out.printf("%-36s : %s (Kode: %s)%n", "Nama Member", member.getNama(), member.getKode());
        System.out.printf("%-36s : %d%n", "Poin Sebelum Transaksi", poinSebelum);
        if (sudahMember && mataUang.getKode().equals("IDR")) {
            if (poinSebelum > 0) {
                System.out.printf("%-36s : %s%n", "Info Poin", "Dapat digunakan.");
            } else {
                System.out.printf("%-36s : %s%n", "Info Poin", "Tidak ada poin.");
            }
        } else if (sudahMember && !mataUang.getKode().equals("IDR")) {
            System.out.printf("%-36s : %s%n", "Info Poin", "Hanya untuk IDR.");
        }
        if (potonganPoin > 0) {
            System.out.printf("%-36s : Rp %,12.0f%n", "Pemotongan Poin (" + poinYangDigunakan + " poin)", potonganPoin);
        }
        System.out.printf("%-36s : %d%n", "Poin Diperoleh", poinDiperoleh);
        if (doublePoin) {
            System.out.printf("%-36s : %s%n", "", "(Poin digandakan karena kode mengandung 'A')");
        }
        System.out.printf("%-36s : %d%n", "Poin Setelah Transaksi", poinSetelah);
        System.out.println("-------------------------------------------------------");

        // total tagihan akhir
        System.out.printf("%-36s : Rp %,12.0f%n", "Total Tagihan Akhir (IDR)", totalAkhirIDR);
        if (!mataUang.getKode().equals("IDR")) {
            System.out.printf("%-36s : %s %,12.2f%n", "Total Tagihan Akhir (" + mataUang.getKode() + ")",
                    mataUang.getKode(), totalAkhirKonversi);
        }

        System.out.println("=======================================================");
        System.out.println("       Terima kasih dan silakan datang kembali!        ");
        System.out.println("=======================================================");
    }

    private static void tampilkanItemKuitansi(ItemPesanan ip) {
        Menu m = ip.getMenu();
        System.out.printf("%-4s %s%n", m.getKode(), m.getNamaMenu());
        System.out.printf("     %d porsi @ Rp %,d%n", ip.getKuantitas(), m.getHarga());
        System.out.printf("     %-33s Rp %,12d%n", "Total Harga", ip.getTotalHarga());
        System.out.printf("     %-33s Rp %,12.0f%n", "Pajak", ip.getPajak());
        System.out.println();
    }
}
