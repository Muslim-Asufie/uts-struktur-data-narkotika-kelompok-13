# Penelusuran Langkah Manual Operasi Struktur Data (*Manual Trace*)

Dokumen ini berisi simulasi langkah demi langkah perubahan isi memori (`Object[]`), pergeseran indeks, pencacah langkah pencarian, serta operasi stack non-destruktif pada proyek **APLIKASI PENCARIAN DATA PUTUSAN NARKOTIKA PENGADILAN NEGERI SIDOARJO**

## 1. Trace `DynamicArray<T>`: Ekspansi Kapasitas, Sisip, dan Hapus

Misalkan `DynamicArray<Putusan>` dimulai dengan kapasitas awal **`capacity = 4`** dan **`size = 0`**.

### A. Penambahan Data & Ekspansi Kapasitas ($2\times$)
| Tahap | Operasi | Kondisi `size` | Kondisi `capacity` | Isi Array `data[]` | Keterangan Mekanisme |
| :---: | :--- | :---: | :---: | :--- | :--- |
| 1 | `add(PUT-001)` | 1 | 4 | `[PUT-001, null, null, null]` | Masuk langsung ke indeks `0` ($O(1)$). |
| 2 | `add(PUT-002)` | 2 | 4 | `[PUT-001, PUT-002, null, null]` | Masuk langsung ke indeks `1` ($O(1)$). |
| 3 | `add(PUT-003)` | 3 | 4 | `[PUT-001, PUT-002, PUT-003, null]` | Masuk langsung ke indeks `2` ($O(1)$). |
| 4 | `add(PUT-004)` | 4 | 4 | `[PUT-001, PUT-002, PUT-003, PUT-004]` | Masuk ke indeks `3`, array penuh (`size == capacity`). |
| 5 | `add(PUT-005)` | **5** | **8** | `[PUT-001, PUT-002, PUT-003, PUT-004, PUT-005, null, null, null]` | Array penuh $\rightarrow$ alokasi array baru ukuran $4 \times 2 = 8$, salin 4 elemen lama, lalu taruh `PUT-005` di indeks `4`. |

### B. Penyisipan di Tengah (`insert(index = 1, PUT-099)`)
* **Kondisi Awal:** `size = 5`, `capacity = 8` $\rightarrow$ `[PUT-001, PUT-002, PUT-003, PUT-004, PUT-005, null, null, null]`
* **Proses Pergeseran ke Kanan** (dimulai dari `i = size - 1` turun ke `index`):
  1. `data[5] = data[4]` (`PUT-005` geser dari indeks `4` ke `5`)
  2. `data[4] = data[3]` (`PUT-004` geser dari indeks `3` ke `4`)
  3. `data[3] = data[2]` (`PUT-003` geser dari indeks `2` ke `3`)
  4. `data[2] = data[1]` (`PUT-002` geser dari indeks `1` ke `2`)
  5. `data[1] = PUT-099` (menempati slot indeks `1` yang sudah kosong)
  6. `size` bertambah menjadi **`6`**.
* **Kondisi Akhir:** `[PUT-001, PUT-099, PUT-002, PUT-003, PUT-004, PUT-005, null, null]` (Total 4 kali pergeseran elemen).

### C. Penghapusan di Awal (`remove(index = 0)`) & Penyusutan Kapasitas ($\le 25\%$)
* **Kondisi Awal:** `size = 6`, `capacity = 8` $\rightarrow$ `[PUT-001, PUT-099, PUT-002, PUT-003, PUT-004, PUT-005, null, null]`
* **Proses Pergeseran ke Kiri** (dimulai dari `i = index` naik ke `size - 2`):
  1. Simpan elemen target: `removed = data[0]` (`PUT-001`).
  2. `data[0] = data[1]` (`PUT-099` geser ke indeks `0`)
  3. `data[1] = data[2]` (`PUT-002` geser ke indeks `1`)
  4. `data[2] = data[3]` (`PUT-003` geser ke indeks `2`)
  5. `data[3] = data[4]` (`PUT-004` geser ke indeks `3`)
  6. `data[4] = data[5]` (`PUT-005` geser ke indeks `4`)
  7. `data[5] = null` (hapus referensi sisa agar tidak terjadi *memory leak*)
  8. `size` berkurang menjadi **`5`**.
* **Mekanisme Penyusutan (*Shrink*):**
  Jika penghapusan dilanjutkan hingga `size = 2` pada `capacity = 8` (rasio pemakaian $2 / 8 = 25\%$), `DynamicArray` otomatis menyusutkan `capacity` menjadi setengahnya ($8 / 2 = 4$) untuk menghemat memori, sekaligus lulus pengecekan `validateInvariant()`.

## 2. Trace Pencarian Linear dengan Pencacah Langkah (`Step Counter`)

Simulasi pencarian berdasarkan `Case ID` pada dataset penuh berisi **`n = 50` record** (`indeks 0` s.d. `indeks 49`):

| Skenario Uji | Target `Case ID` | Posisi Indeks | Urutan Indeks yang Diperiksa | Nilai `lastSearchSteps` | Kompleksitas |
| :--- | :--- | :---: | :--- | :---: | :---: |
| **Best Case** | `PUT-001` | `0` | `0` (langsung cocok) | **1 langkah** | $\Omega(1)$ |
| **Average Case** | `PUT-025` | `24` | `0, 1, 2, ..., 24` | **25 langkah** | $\Theta(n/2)$ |
| **Worst Case (Ada)** | `PUT-050` | `49` | `0, 1, 2, ..., 49` | **50 langkah** | $O(n)$ |
| **Worst Case (Tidak Ada)** | `PUT-999` | `-1` | `0, 1, 2, ..., 49` (habis) | **50 langkah** | $O(n)$ |

## 3. Trace `ArrayStack<T>`: Pencarian Audit Log Non-Destruktif

Misalkan pengguna telah melakukan 3 aktivitas berurutan (`IMPORT` $\rightarrow$ `FILTER` $\rightarrow$ `SORT`). Karena prinsip **LIFO (*Last-In, First-Out*)**, isi `auditStack` dari bawah ke atas adalah:

* **Isi Awal `auditStack` (`top = 2`):**
  `[0] "IMPORT: 50 record"` $\rightarrow$ `[1] "FILTER: Sabu"` $\rightarrow$ `[2] "SORT: Vonis DESC"` *(Puncak / Top)*

Pengguna ingin mencari riwayat dengan kata kunci **`"IMPORT"`** tanpa merusak urutan tumpukan asli menggunakan metode `searchPreserve("IMPORT")`:

### Langkah 1: Pemindahan ke Stack Penyangga (`bufferStack`)
1. `pop()` dari `auditStack` $\rightarrow$ dapat `"SORT: Vonis DESC"` (bukan `"IMPORT"`). `push()` ke `bufferStack`.
2. `pop()` dari `auditStack` $\rightarrow$ dapat `"FILTER: Sabu"` (bukan `"IMPORT"`). `push()` ke `bufferStack`.
3. `pop()` dari `auditStack` $\rightarrow$ dapat `"IMPORT: 50 record"` (**Cocok!** Dicatat sebagai hasil temuan). `push()` ke `bufferStack`.
   * *Kondisi Sementara:* `auditStack` kosong (`size = 0`), sedangkan `bufferStack` berisi dari bawah ke atas: `["SORT: Vonis DESC", "FILTER: Sabu", "IMPORT: 50 record"]`.

### Langkah 2: Pemulihan Kembali (`restore()`)
1. `pop()` dari `bufferStack` (`"IMPORT: 50 record"`) $\rightarrow$ `push()` kembali ke `auditStack` (indeks `0`).
2. `pop()` dari `bufferStack` (`"FILTER: Sabu"`) $\rightarrow$ `push()` kembali ke `auditStack` (indeks `1`).
3. `pop()` dari `bufferStack` (`"SORT: Vonis DESC"`) $\rightarrow$ `push()` kembali ke `auditStack` (indeks `2`).

* **Kondisi Akhir `auditStack` (`top = 2`):**
  `[0] "IMPORT: 50 record"` $\rightarrow$ `[1] "FILTER: Sabu"` $\rightarrow$ `[2] "SORT: Vonis DESC"` *(Puncak / Top)*
* **Kesimpulan:** Data pencarian ditemukan dan urutan LIFO pada `auditStack` tetap utuh 100% tanpa kehilangan satu pun catatan riwayat.