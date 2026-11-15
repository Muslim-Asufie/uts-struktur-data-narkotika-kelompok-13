# APLIKASI PENCARIAN DATA PUTUSAN NARKOTIKA PENGADILAN NEGERI SIDOARJO 

Aplikasi berbasis Command Line Interface (CLI) yang dibangun menggunakan bahasa Java untuk mengelola, menelusuri, memfilter, dan mengaudit 50 dataset putusan perkara tindak pidana narkotika dari **Pengadilan Negeri Sidoarjo**. Proyek ini mengimplementasikan struktur data secara mandiri (*from scratch*) tanpa menggunakan koleksi bawaan dari `java.util`.

## 1. Identitas Kelompok

* **Mata Kuliah:** Struktur Data (UTS)
* **Program Studi:** Informatika — Fakultas Teknik, Universitas Muhammadiyah Malang
* **Sumber Dataset:** Direktori Putusan Mahkamah Agung RI (Pengadilan Negeri Sidoarjo)

| No | Nama Lengkap | NIM | Kelas |
| :--- | :--- | :--- | :--- |
| 1 | **Muhammad Muslim Noor Asufie** | *202510370110124* | Kelas D | 
| 2 | **Aditya yusuf bakhtiar** | *202510370110120* | Kelas D |

## 2. Arsitektur & Struktur Data Pilihan

### A. Struktur Data Utama: `DynamicArray<T>`
Digunakan sebagai wadah penyimpanan inti untuk seluruh objek `Putusan` (50 record).
* **Alasan Pemilihan:** Mendukung akses acak berdasarkan indeks dalam waktu konstan $O(1)$, efisien untuk penelusuran sekuensial dengan pencacah langkah (*step counter*), serta memiliki mekanisme ekspansi kapasitas otomatis ($2\times$) dan penyusutan (*shrinking* saat penggunaan $\le 25\%$) untuk efisiensi memori.
* **Operasi Inti:** `add`, `get`, `set`, `remove`, `search` (dengan pencatat `lastSearchSteps`), `resize`, dan `validateInvariant`.

### B. Struktur Data Sekunder: `ArrayStack<T>`
Digunakan sebagai struktur pencatat riwayat aktivitas pengguna (*Audit Log*).
* **Alasan Pemilihan:** Prinsip **LIFO** (*Last-In, First-Out*) sangat sesuai untuk menampilkan log aktivitas terbaru di posisi paling atas. Dilengkapi method `searchPreserve` dan `restore` menggunakan stack penyangga sementara sehingga pencarian riwayat bersifat non-destruktif terhadap urutan akhir.
* **Operasi Inti:** `push`, `pop`, `peek`, `searchPreserve`, `restore`, dan `validateInvariant`.

---

## 3. Cara Menjalankan, Test, dan Contoh Perintah

### A. Cara Menjalankan Aplikasi
1. Buka proyek di **VS Code** dan pastikan ekstensi *Extension Pack for Java* sudah aktif atau IDE Java lainnya.
2. Buka file **`src/main/java/pn/sidoarjo/Main.java`**.
3. Klik tombol **Run Java** (ikon ▶ di pojok kanan atas editor).
4. Menu CLI akan langsung muncul di panel Terminal.

### B. Cara Menjalankan Pengujian (Unit Test)
Pengujian otomatis menggunakan JUnit 5 mencakup 7 skenario wajib yang tersimpan pada **`src/test/java/pn/sidoarjo/StrukturDataTest.java`**.
1. Buka Terminal di VS Code atau IDE (`Ctrl + \``) .
2. Jalankan perintah berikut:

       mvn test

3. Pastikan seluruh skenario lulus dengan indikator **`BUILD SUCCESS`** (`Tests run: 7, Failures: 0, Errors: 0`).

### C. Contoh Perintah & Alur Penggunaan CLI

Saat aplikasi berjalan, terminal akan menampilkan 11 pilihan menu utama:

    === APLIKASI PENCARIAN DATA PUTUSAN NARKOTIKA PENGADILAN NEGERI SIDOARJO ===
    Total Record Aktif (DynamicArray): 0
    1. Impor CSV
    2. Tampilkan Semua Putusan
    3. Tambah Putusan
    4. Hapus Putusan berdasarkan Posisi
    5. Cari Putusan
    6. Filter Multi-Kriteria
    7. Ringkasan Agregasi
    8. Urutkan berdasarkan Vonis (Bulan)
    9. Lihat Riwayat Operasi (Audit Stack)
    10. Ekspor Hasil Terakhir ke CSV
    11. Keluar
    Pilih menu :

Berikut contoh interaksi perintah untuk fitur-fitur utamanya:

1. **Mengimpor 50 Dataset CSV (`Menu 1`):**

       Pilih menu : 1
       Path file CSV [Enter untuk data/putusan-50.csv]: [Tekan Enter]
       Berhasil dimuat: 50, Ditolak: 0

2. **Menambah Putusan Baru pada Posisi Tertentu (`Menu 3`):**

       Pilih menu : 3
       Case ID (Misal PUT-051)                          : PUT-051
       Nomor Perkara (Misal 352/Pid.Sus/2026/PN Sda)    : 901/Pid.Sus/2026/PN Sda
       Pengadilan [Pengadilan Negeri Sidoarjo]          : [Tekan Enter]
       Tanggal Putusan (YYYY-MM-DD)                     : 2026-08-10
       Kode Terdakwa (misal TRD-51)                     : TRD-51
       Jenis Narkotika (Sabu, Ganja, Metamfetamina, dll): Sabu
       Berat Barang Bukti (gram)                        : 1.25
       Pasal Dakwaan                                    : Pasal 114 ayat (1) UU Narkotika
       Pasal Terbukti                                   : Pasal 112 ayat (1) UU Narkotika
       Tuntutan (bulan)                                 : 60
       Vonis (bulan)                                    : 48
       Denda (rupiah)                                   : 800000000
       Ringkasan Barang Bukti                           : 1 poket plastik sabu berat 1.25 gram
       Keadaan Memberatkan                              : Tidak mendukung program pemerintah
       Keadaan Meringankan                              : Terdakwa bersikap sopan
       Halaman Sumber                                   : hal 1-14
       Posisi sisip (0 s.d. 50, kosongkan untuk akhir)  : 0
       Putusan PUT-051 berhasil ditambahkan.

3. **Mencari Putusan & Melihat Jumlah Langkah (`Menu 5`):**

       Pilih menu : 5
       Cari berdasarkan
       1. Case ID
       2. Nomor Perkara
       Pilih : 1
       Masukkan kata kunci pencarian: PUT-025
       Ditemukan pada indeks 25 dalam 26 langkah:
         Case ID       : PUT-025
         No. Perkara   : ...
         Pengadilan    : Pengadilan Negeri Sidoarjo (...)
         Narkotika     : Sabu (... gram)
         Tuntutan/Vonis: ... bulan / ... bulan

4. **Menjalankan Filter Multi-Kriteria (`Menu 6`):**

       Pilih menu : 6
       Kosongkan kriteria jika tidak ingin difilter:
       Jenis Narkotika (misal Sabu / Ganja): Sabu
       Berat Minimum (gram): 1.0
       Berat Maksimum (gram): [Tekan Enter]
       Kata Kunci Pasal Terbukti (misal 114 / 609): 114
       Vonis Minimum (bulan): 48
       Vonis Maksimum (bulan): [Tekan Enter]

5. **Melihat Riwayat Aktivitas di Audit Stack (`Menu 9`):**

       Pilih menu : 9

       --- RIWAYAT AUDIT (URUTAN LIFO / TERBARU DI ATAS) ---
       1. [2026-11-15 15:45:12] FILTER: menghasilkan 12 putusan
       2. [2026-11-15 15:44:30] CARI caseId='PUT-025' -> DITEMUKAN (26 langkah)
       3. [2026-11-15 15:43:55] IMPOR CSV: memuat 50 record valid, menolak 0 baris

6. **Mengekspor Hasil Terakhir ke CSV (`Menu 10`):**

       Pilih menu : 10
       Nama file ekspor [docs/demo/hasil-ekspor.csv]: [Tekan Enter]
       Data berhasil diekspor ke: docs/demo/hasil-ekspor.csv