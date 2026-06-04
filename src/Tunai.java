public class Tunai implements ChannelPembayaran {
    @Override
    public String getNama() {
        return "Tunai";
    }

    @Override
    public double hitungDiskon(double totalTagihan) {
        return 0.0; // tidak mendapat diskon
    }

    @Override
    public double getBiayaAdmin() {
        return 0.0; // tidak ada biaya admin
    }

    @Override
    public boolean butuhCekSaldo() {
        return false; // tunai tidak perlu cek saldo
    }
}

