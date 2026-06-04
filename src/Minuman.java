public class Minuman extends Menu {
    private static final int MAX_KUANTITAS = 3;

    public Minuman(String kode, String namaMenu, int harga) {
        super(kode, namaMenu, harga);
    }

    @Override
    public int getMaxKuantitas() {
        return MAX_KUANTITAS;
    }

    @Override
    public String getKategori() {
        return "Minuman";
    }
}
