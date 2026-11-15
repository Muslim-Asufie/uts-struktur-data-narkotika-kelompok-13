## 1. Konteks & Permasalahan
Dataset putusan pengadilan yang dimuat dari file CSV maupun ditambah secara manual oleh pengguna tidak selalu berada dalam kondisi terurut berdasarkan `Case ID` maupun lama `Vonis (bulan)`. Pengguna membutuhkan:
1. Fitur pencarian berdasarkan `Case ID` atau `Nomor Perkara` yang menampilkan informasi lokasi indeks serta efisiensi langkah pencariannya.
2. Fitur pengurutan (*sorting*) lama vonis pidana penjara (terpendek ke terlama maupun sebaliknya) tanpa merusak urutan asli penyimpanan data utama.

## 2. Keputusan
1. **Strategi Pencarian — *Sequential / Linear Search* dengan Pencacah Langkah (`Step Counter`):** Menelusuri elemen dari indeks `0` hingga `size - 1` secara berurutan dan mencatat jumlah perbandingan elemen yang dilakukan.
2. **Strategi Pengurutan — *Comparison-Based Sort* pada Salinan `DynamicArray` (*Non-Destructive*):** Menyalin isi `dataset` utama ke `DynamicArray` baru terlebih dahulu sebelum diurutkan berdasarkan atribut `vonisBulan`.

## 3. Alasan & Pertimbangan Teknis
### Mengapa *Linear Search* (dibandingkan *Binary Search*)?
* **Tidak Memerlukan Prasyarat Data Terurut:** *Binary Search* mewajibkan data selalu dalam keadaan terurut berdasarkan kunci pencarian (`Case ID` atau `Nomor Perkara`). Karena pengguna dapat menyisipkan record baru di posisi bebas (awal, tengah, atau akhir) dan mencari berdasarkan dua atribut berbeda, *Linear Search* menjamin hasil selalu akurat tanpa harus mengurutkan ulang dataset setiap kali ada perubahan.
* **Transparansi Kompleksitas untuk Evaluasi:** Pencacah langkah (`lastSearchSteps`) langsung membuktikan perilaku nyata struktur data: **1 langkah** untuk *best case* (indeks 0), **$k+1$ langkah** untuk indeks ke-$k$, dan **$n$ langkah** (50 langkah) untuk *worst case* atau data tidak ditemukan.

### Mengapa Pengurutan Dilakukan pada Salinan Array (*Non-Destructive*)?
* **Menjaga Urutan Asli Dataset (`dataset`):** Urutan awal record di memori mencerminkan urutan masuk dari file CSV dan posisi sisip manual pengguna. Jika pengurutan dilakukan langsung di tempat (*in-place* pada `dataset` utama), posisi indeks record asli akan berubah dan membingungkan operasi hapus berdasarkan indeks.
* **Integrasi dengan Fitur Ekspor CSV:** Hasil pengurutan disimpan ke variabel `lastQueryResult`, sehingga pengguna bisa langsung mengekspor daftar putusan yang sudah terurut rapi ke file CSV melalui Menu 10 tanpa mengubah isi dataset aktif.

## 4. Konsekuensi & Trade-off
* **Kelebihan:** Pencarian kebal terhadap perubahan urutan sisip data, hasil *sorting* aman diekspor tanpa merusak indeks asli, dan seluruh proses berjalan di atas struktur `DynamicArray` mandiri tanpa bantuan `Arrays.sort()` atau `Collections.sort()`.
* **Kekurangan & Mitigasi:** Operasi penyalinan array saat pengurutan membutuhkan ruang memori tambahan sebesar $O(n)$ dan waktu $O(n^2)$ / $O(n \log n)$. Untuk dataset puluhan hingga ratusan record putusan, overhead memori ini sangat ringan dan dapat diabaikan.