public class QRIS implements ChannelPembayaran{
    @Override
    public String getNama() {
        return "QRIS";
    }

    @Override
    public double hitungDiskon(double totalTagihan) {
        return totalTagihan * 0.05; // diskon 5%
    }

    @Override
    public double getBiayaAdmin() {
        return 0.0; // tidak ada biaya admin
    }

    @Override
    public boolean butuhCekSaldo() {
        return true; // perlu verifikasi saldo
    }
}
