public class IDR implements MataUang {
    @Override
    public String getKode() {
        return "IDR";
    }

    @Override
    public double konversiDariIDR(double nominalIDR) {
        return nominalIDR; // 1 IDR = 1 IDR
    }
}
