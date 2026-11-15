# Laporan Kontribusi Anggota Kelompok — UTS Struktur Data

## 1. Muhammad Muslim Noor Asufie
* **NIM:** *202510370110124*
* **Fokus Tugas:** Ekstraksi data putusan 26–50, pengecekan & pembuatan *data-provenance* untuk data putusan 1–25, pembuatan struktur data (`DynamicArray` & `ArrayStack`), serta penyusunan laporan akhir.

| Jenis Aktivitas | Pekerjaan yang Dilakukan | Branch | Tautan (Issue / PR / Commit) | Status |
| :--- | :--- | :--- | :--- | :--- |
| **Issue** | Ekstraksi dan pembersihan data putusan narkotika nomor 26–50 | `putusan-26-50` | https://github.com/Muslim-Asufie/uts-struktur-data-narkotika-kelompok-13/issues/3#issue-6403004401 | Selesai |
| **Issue** | Pembuatan struktur data `DynamicArray<T>` dan `ArrayStack<T>` dari nol tanpa `java.util` | `struktur-data` | https://github.com/Muslim-Asufie/uts-struktur-data-narkotika-kelompok-13/issues/4#issue-6403113029 | Selesai |
| **Issue** | Penyusunan dokumentasi ADR, *manual trace*, `README.md`, dan laporan akhir | `final-report` | https://github.com/Muslim-Asufie/uts-struktur-data-narkotika-kelompok-13/issues/5#issue-6403122541 | Selesai |
| **Pull Request** | Menambahkan hasil ekstraksi data putusan record 26–50 | `putusan-26-50` $\rightarrow$ `main` | https://github.com/Muslim-Asufie/uts-struktur-data-narkotika-kelompok-13/pull/2#issue-6390934181 | Merged |
| **Pull Request** | Menambahkan kode `DynamicArray` dan `ArrayStack` beserta validasi konsistensi data | `struktur-data` $\rightarrow$ `main` | https://github.com/Muslim-Asufie/uts-struktur-data-narkotika-kelompok-13/pull/10#issue-6404598383 | Merged |
| **Pull Request** | Melengkapi dokumentasi ADR, *manual trace*, `README`, dan laporan akhir | `final-report` $\rightarrow$ `main` | *(Isi tautan PR)* | Merged |
| **Review & Provenance** | Mengecek hasil ekstraksi data 1–25 milik Aditya sekaligus membuatkan `data-provenance.csv`-nya | `putusan-1-25` | 78510b3b3cb10dc433e0c25b355521f2fc2e922a | Approved |
| **Commit Utama** | Menyimpan 25 record data putusan narkotika (nomor 26–50) | `putusan-26-50` | 4b8315cf490eaaf4a777a02b467becb562b206ab | Verified |
| **Commit Utama** | Menambahkan pemetaan halaman sumber (*provenance*) untuk record 1–25 | `putusan-1-25` | 78510b3b3cb10dc433e0c25b355521f2fc2e922a | Verified |
| **Commit Utama** | Menyelesaikan implementasi `DynamicArray` dan `ArrayStack` mandiri | `struktur-data` | 7a5131bf030deb386ff1fbf74ff7064104f2f263 | Verified |
| **Commit Utama** | Menyelesaikan seluruh dokumen pendukung dan laporan akhir proyek | `final-report` | d9022d5772d18a6dbfd1ecca6555b539398fb2fc | Verified |

## 2. Aditya yusuf bakhtiar
* **NIM:** *202510370110120*
* **Fokus Tugas:** Ekstraksi data putusan 1–25, pengecekan & pembuatan *data-provenance* untuk data putusan 26–50, pembuatan tampilan & fitur aplikasi CLI, serta pengujian otomatis (`StrukturDataTest`).

| Jenis Aktivitas | Pekerjaan yang Dilakukan | Branch | Tautan (Issue / PR / Commit) | Status |
| :--- | :--- | :--- | :--- | :--- |
| **Issue** | Ekstraksi dan pembersihan data putusan narkotika nomor 1–25 | `putusan-1-25` | https://github.com/Muslim-Asufie/uts-struktur-data-narkotika-kelompok-13/issues/6#issue-6404302796 | Selesai |
| **Issue** | Pembuatan tampilan menu CLI (`Main.java`) serta fitur filter, agregasi, sorting, dan audit | `tampilan-aplikasi` | https://github.com/Muslim-Asufie/uts-struktur-data-narkotika-kelompok-13/issues/7#issue-6404310153 | Selesai |
| **Issue** | Pembuatan 7 skenario pengujian otomatis menggunakan JUnit 5 | `test-aplikasi` | https://github.com/Muslim-Asufie/uts-struktur-data-narkotika-kelompok-13/issues/8#issue-6404319631 | Selesai |
| **Pull Request** | Menambahkan hasil ekstraksi data putusan record 1–25 | `putusan-1-25` $\rightarrow$ `main` | https://github.com/Muslim-Asufie/uts-struktur-data-narkotika-kelompok-13/pull/1#issue-6390776671 | Merged |
| **Pull Request** | Menambahkan antarmuka CLI dan seluruh fitur layanan di `PutusanService` | `tampilan-aplikasi` $\rightarrow$ `main` | https://github.com/Muslim-Asufie/uts-struktur-data-narkotika-kelompok-13/pull/9#issue-6404598280 | Merged |
| **Pull Request** | Menambahkan pengujian otomatis JUnit 5 untuk 7 skenario wajib | `test-aplikasi` $\rightarrow$ `main` | https://github.com/Muslim-Asufie/uts-struktur-data-narkotika-kelompok-13/pull/11#issue-6404763732 | Merged |
| **Review & Provenance** | Mengecek hasil ekstraksi data 26–50 milik Muslim sekaligus membuatkan `data-provenance.csv`-nya | `putusan-26-50` | e5bd7bd85662fdc6580a6fa54f3ecff17bd0aba7 | Approved |
| **Commit Utama** | Menyimpan 25 record data putusan narkotika (nomor 1–25) | `putusan-1-25` | b88899c598979565f76b6e7b5af38d58a0c0097f | Verified |
| **Commit Utama** | Menambahkan pemetaan halaman sumber (*provenance*) untuk record 26–50 | `putusan-26-50` | e5bd7bd85662fdc6580a6fa54f3ecff17bd0aba7 | Verified |
| **Commit Utama** | Menyelesaikan menu interaktif CLI dan fitur pemrosesan data | `tampilan-aplikasi` | 11cc1e87de3e3175d38c15454a4346d55679cca0 | Verified |
| **Commit Utama** | Menyelesaikan seluruh skenario pengujian di `StrukturDataTest.java` | `test-aplikasi` | 8fa5fe9fc9372baefdcedc8705cf18039d7b0176 | Verified |
