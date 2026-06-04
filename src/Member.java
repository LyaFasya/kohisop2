import java.util.Random;

public class Member {
    private String kode;
    private String nama;
    private int poin;

    public Member(String kode, String nama, int poin) {
        this.kode = kode;
        this.nama = nama;
        this.poin = poin;
    }

    public String getKode() {
        return kode;
    }

    public String getNama() {
        return nama;
    }

    public int getPoin() {
        return poin;
    }

    public void setPoin(int poin) {
        this.poin = poin;
    }

    public void tambahPoin(int poinBaru) {
        this.poin += poinBaru;
    }

    // Static helper to generate a 6-character alphanumeric code consisting of A-F and 0-9
    public static String generateRandomCode() {
        String chars = "ABCDEF0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
