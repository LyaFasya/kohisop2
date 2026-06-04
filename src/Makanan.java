public class Makanan extends Menu {
    private static final int MAX_KUANTITAS = 2;

    public Makanan(String kode, String namaMenu, int harga) {
        super(kode, namaMenu, harga);
    }

    @Override
    public int getMaxKuantitas() {
        return MAX_KUANTITAS;
    }

    @Override
    public String getKategori() {
        return "Makanan";
    }
}
