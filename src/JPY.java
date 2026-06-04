public class JPY implements MataUang {
    @Override
    public String getKode() {
        return "JPY";
    }

    @Override
    public double konversiDariIDR(double nominalIDR) {
        return nominalIDR * 10.0; // 10 JPY = 1 IDR
    }
}
