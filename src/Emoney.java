public class Emoney implements ChannelPembayaran{
    @Override
    public String getNama() {
        return "eMoney";
    }

    @Override
    public double hitungDiskon(double totalTagihan) {
        return totalTagihan * 0.07; // diskon 7%
    }

    @Override
    public double getBiayaAdmin() {
        return 20.0; // biaya administrasi tetap 20 IDR
    }

    @Override
    public boolean butuhCekSaldo() {
        return true; // perlu verifikasi saldo
    }
}
