public class ItemPesanan {
    private Menu menu;
    private int kuantitas;

    public ItemPesanan(Menu menu, int kuantitas) {
        this.menu = menu;
        this.kuantitas = kuantitas;
    }
    public Menu getMenu() { 
        return menu; 
    }
    public int getKuantitas() { 
        return kuantitas; 
    }
    public void setKuantitas(int kuantitas) { 
        this.kuantitas = kuantitas; 
    }
    public int getTotalHarga() {
        return menu.getHarga() * kuantitas;
    }
    public double getPajak() {
        return Pajak.hitungPajak(menu, getTotalHarga());
    }
    public double getTotalDenganPajak() {
        return getTotalHarga() + getPajak();
    }
}
