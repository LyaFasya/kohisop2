public class EUR implements MataUang{
    @Override
    public String getKode() {
        return "EUR";
    }

    @Override
    public double konversiDariIDR(double nominalIDR) {
        return nominalIDR / 14.0; // 1 EUR = 14 IDR
    }
}
