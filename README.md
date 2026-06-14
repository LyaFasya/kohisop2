# KohiSop

Aplikasi kasir sederhana berbasis CLI untuk kedai kopi dan makanan, dibuat menggunakan Java.

## Fitur

- Pemesanan minuman dan makanan (maks 5 jenis per kategori)
- Perhitungan pajak otomatis per item
- Sistem member dengan kode unik dan poin reward
- Pembayaran dengan 3 channel: Tunai, QRIS, eMoney
- Dukungan multi mata uang: IDR, USD, JPY, MYR, EUR
- Pemotongan poin hanya berlaku untuk pembayaran IDR
- Cetak kuitansi lengkap
- Sistem antrian dapur per batch 3 pelanggan

## Struktur Folder

- `src/` — source code Java
- `bin/` — hasil kompilasi
- `member.txt` — database member

## Cara Menjalankan

```bash
# Kompilasi
javac -d bin src/*.java

# Jalankan
java -cp bin App
```

## Anggota Kelompok

- Rizkya Dwi Aulya Fasya - 255150401111025
- Muhammad Naufal Alfarrel - 255150400111035
- Ciello Anryfa Ramadhan Siregar - 255150407111011
- M. Furqanurrahman - 255150401111029
