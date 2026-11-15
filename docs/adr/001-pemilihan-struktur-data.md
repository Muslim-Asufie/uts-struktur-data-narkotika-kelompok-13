## 1. Konteks & Permasalahan
Aplikasi Manajemen Data Putusan Narkotika PN Sidoarjo membutuhkan penyimpanan di memori untuk mengelola 50 record data utama (masing-masing terdiri dari 16 atribut) serta fitur pencatatan riwayat aktivitas pengguna (*audit log*). 

Sesuai aturan tugas UTS Struktur Data, seluruh struktur penyimpanan tidak diperbolehkan menggunakan koleksi bawaan Java (`ArrayList`, `LinkedList`, `Stack`, `Queue`,), melainkan harus dibangun sendiri di atas array mentah (`Object[]`).

## 2. Keputusan
Kami memutuskan menggunakan kombinasi dua struktur data berikut:

1. **Struktur Data Utama — `DynamicArray<T>`:** Digunakan untuk menyimpan seluruh record objek `Putusan` dan menampung hasil pemrosesan (*filter* maupun *sorting*).
2. **Struktur Data Sekunder — `ArrayStack<T>`:** Digunakan untuk menyimpan riwayat operasional pengguna (*audit log*).

## 3. Alasan & Pertimbangan Teknis
### Mengapa `DynamicArray<T>` (dibandingkan *Linked List*)?
* **Akses Indeks Instan $O(1)$:** Operasi penampilan data berdasarkan posisi (`get(index)`) dan pembaruan elemen (`set(index, value)`) langsung menunjuk ke slot memori tanpa perlu menelusuri pointer node dari awal seperti pada *Linked List*.
* **Kapasitas Fleksibel:** Berbeda dengan array statis biasa, `DynamicArray` kami lengkapi mekanisme ekspansi otomatis ($2\times$ saat penuh) dan penyusutan (*shrinking* menjadi setengah kapasitas saat pemakaian $\le 25\%$) sehingga tidak memboroskan memori saat banyak data dihapus.
* **Mendukung Operasi Sisip & Hapus di Segala Posisi:** Mampu menyisipkan (`insert`) dan menghapus (`remove`) record di posisi awal, tengah, maupun akhir dengan pergeseran elemen yang terukur dan tervalidasi melalui `validateInvariant()`.

### Mengapa `ArrayStack<T>` (dibandingkan *Queue*)?
* **Sesuai Kebutuhan Audit Terbaru (LIFO):** Dalam fitur riwayat aktivitas (*audit log*), pengguna paling membutuhkan informasi aksi terakhir yang baru saja dilakukan. Prinsip **LIFO** (*Last-In, First-Out*) menempatkan log terbaru langsung di puncak tumpukan (*top*).
* **Pencarian Riwayat Non-Destruktif:** Kami menambahkan method `searchPreserve()` dan `restore()` menggunakan stack penyangga sementara agar pencarian catatan audit tertentu tidak merusak urutan riwayat aslinya.

## 4. Konsekuensi & Trade-off
* **Kelebihan:** Hemat memori per elemen (tidak menyimpan referensi `next`/`prev` tambahan seperti *Doubly Linked List*), mudah dilacak pergeseran indeksnya secara manual (*manual trace*), dan sangat cepat saat iterasi pembacaan seluruh dataset.
* **Kekurangan & Mitigasi:** Penyisipan atau penghapusan di indeks awal/tengah membutuhkan pergeseran elemen sebesar $O(n)$. Untuk skala 50–500 record putusan pengadilan, biaya pergeseran di memori ini sangat kecil (di bawah 1 milidetik) sehingga tetap optimal.