public class USD implements MataUang {
    @Override
    public String getKode() {
        return "USD";
    }

    @Override
    public double konversiDariIDR(double nominalIDR) {
        return nominalIDR / 15.0; // 1 USD = 15 IDR
    }
}
