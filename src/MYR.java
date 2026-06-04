public class MYR implements MataUang {
    @Override
    public String getKode() {
        return "MYR";
    }

    @Override
    public double konversiDariIDR(double nominalIDR) {
        return nominalIDR / 4.0; // 1 MYR = 4 IDR
    }
}
