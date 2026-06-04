public interface ChannelPembayaran {
    String getNama();
    double hitungDiskon(double totalTagihan);
    double getBiayaAdmin();
    boolean butuhCekSaldo();
}
