public abstract class Menu implements IOrderable {
    protected String kode;
    protected String namaMenu;
    protected int harga;

    public Menu(String kode, String namaMenu, int harga) {
        this.kode = kode;
        this.namaMenu = namaMenu;
        this.harga = harga;
    }

    // getter
    public String getKode() { 
        return kode; 
    }
    public String getNamaMenu() { 
        return namaMenu; 
    }
    public int getHarga() { 
        return harga; 
    }
    @Override
    public abstract int getMaxKuantitas();
    @Override
    public abstract String getKategori();

    @Override
    public String toString() {
        return String.format("%-3s | %-35s | Rp %,d", kode, namaMenu, harga);
    }
}
