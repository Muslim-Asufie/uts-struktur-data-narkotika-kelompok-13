package pn.sidoarjo;

import java.time.LocalDate;
import java.util.Scanner;
import pn.sidoarjo.CSV.CsvPutusanLoader.LoadResult;
import pn.sidoarjo.Model.Putusan;
import pn.sidoarjo.Service.PutusanService;
import pn.sidoarjo.Service.PutusanService.AggregationSummary;
import pn.sidoarjo.Service.PutusanService.SearchResult;
import pn.sidoarjo.StrukturData.ArrayStack;
import pn.sidoarjo.StrukturData.DynamicArray;

public class Main {
    private static final String DEFAULT_CSV_PATH = "data/putusan-50.csv";

    public static void main(String[] args) {
        PutusanService service = new PutusanService();
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            printMenu(service.getDataset().size());
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleImport(scanner, service);
                    break;
                case "2":
                    handleDisplayAll(service);
                    break;
                case "3":
                    handleAdd(scanner, service);
                    break;
                case "4":
                    handleRemove(scanner, service);
                    break;
                case "5":
                    handleSearch(scanner, service);
                    break;
                case "6":
                    handleFilter(scanner, service);
                    break;
                case "7":
                    handleAggregate(service);
                    break;
                case "8":
                    handleSort(scanner, service);
                    break;
                case "9":
                    handleAuditLog(service);
                    break;
                case "10":
                    handleExport(scanner, service);
                    break;
                case "11":
                    running = false;
                    System.out.println("Program selesai.");
                    break;
                default:
                    System.out.println("Pilihan menu tidak dikenali.");
            }
        }
        scanner.close();
    }

    private static void printMenu(int currentSize) {
        System.out.println("\n=== APLIKASI PENCARIAN DATA PUTUSAN NARKOTIKA PENGADILAN NEGERI SIDOARJO ===");
        System.out.println("Total Record Aktif (DynamicArray): " + currentSize);
        System.out.println("1. Impor CSV");
        System.out.println("2. Tampilkan Semua Putusan");
        System.out.println("3. Tambah Putusan");
        System.out.println("4. Hapus Putusan berdasarkan Posisi");
        System.out.println("5. Cari Putusan");
        System.out.println("6. Filter Multi-Kriteria");
        System.out.println("7. Ringkasan Agregasi");
        System.out.println("8. Urutkan berdasarkan Vonis (Bulan)");
        System.out.println("9. Lihat Riwayat Operasi (Audit Stack)");
        System.out.println("10. Ekspor Hasil Terakhir ke CSV");
        System.out.println("11. Keluar");
        System.out.print("Pilih menu : ");
    }

    private static void handleImport(Scanner scanner, PutusanService service) {
        System.out.print("Path file CSV [Enter untuk " + DEFAULT_CSV_PATH + "]: ");
        String path = scanner.nextLine().trim();
        if (path.isEmpty()) {
            path = DEFAULT_CSV_PATH;
        }
        LoadResult res = service.importCsv(path, 50);
        System.out.println("Berhasil dimuat: " + res.getLoadedCount() + ", Ditolak: " + res.getRejectedCount());
        DynamicArray<String> errors = res.getErrorLogs();
        for (int i = 0; i < errors.size(); i++) {
            System.out.println("  [!] " + errors.get(i));
        }
    }

    private static void handleDisplayAll(PutusanService service) {
        DynamicArray<Putusan> list = service.getAll();
        if (list.isEmpty()) {
            System.out.println("Belum ada data putusan.");
            return;
        }
        System.out.println("\n--- DAFTAR PUTUSAN (" + list.size() + " RECORD) ---");
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("%2d. %s%n", i, list.get(i));
        }
    }

    private static void handleAdd(Scanner scanner, PutusanService service) {
        try {
            System.out.print("Case ID (Misal PUT-051)                          : ");
            String caseId = scanner.nextLine().trim();

            System.out.print("Nomor Perkara (Misal 352/Pid.Sus/2026/PN Sda)    : ");
            String nomorPerkara = scanner.nextLine().trim();

            System.out.print("Pengadilan [Pengadilan Negeri Sidoarjo]          : ");
            String pengadilan = scanner.nextLine().trim();
            if (pengadilan.isEmpty()) pengadilan = "Pengadilan Negeri Sidoarjo";

            System.out.print("Tanggal Putusan (YYYY-MM-DD)                      : ");
            LocalDate tanggal = LocalDate.parse(scanner.nextLine().trim());

            System.out.print("Kode Terdakwa (misal TRD-51)                      : ");
            String terdakwa = scanner.nextLine().trim();
            
            System.out.print("Jenis Narkotika (Sabu, Ganja, Metamfetamina, dll) : ");
            String jenis = scanner.nextLine().trim();

            System.out.print("Berat Barang Bukti (gram)                         : ");
            double berat = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Pasal Dakwaan                                     : ");
            String dakwaan = scanner.nextLine().trim();

            System.out.print("Pasal Terbukti                                    : ");
            String terbukti = scanner.nextLine().trim();

            System.out.print("Tuntutan (bulan)                                  : ");
            int tuntutan = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Vonis (bulan)                                     : ");
            int vonis = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Denda (rupiah)                                    : ");
            long denda = Long.parseLong(scanner.nextLine().trim());

            System.out.print("Ringkasan Barang Bukti                            : ");
            String bb = scanner.nextLine().trim();

            System.out.print("Keadaan Memberatkan                               : ");
            String memberatkan = scanner.nextLine().trim();

            System.out.print("Keadaan Meringankan                               : ");
            String meringankan = scanner.nextLine().trim();

            System.out.print("Halaman Sumber                                    : ");
            String pages = scanner.nextLine().trim();

            System.out.print("Posisi sisip (0 s.d. " + service.getDataset().size() + ", kosongkan untuk akhir)  : ");
            String posInput = scanner.nextLine().trim();
            Integer pos = posInput.isEmpty() ? null : Integer.parseInt(posInput);

            Putusan p = new Putusan(caseId, nomorPerkara, pengadilan, tanggal, terdakwa, jenis,
                    berat, dakwaan, terbukti, tuntutan, vonis, denda, bb, memberatkan, meringankan, pages);
            service.addPutusan(p, pos);
            System.out.println("Putusan " + caseId + " berhasil ditambahkan.");
        } catch (Exception e) {
            System.out.println("Gagal menambah data: " + e.getMessage());
        }
    }

    private static void handleRemove(Scanner scanner, PutusanService service) {
        if (service.getDataset().isEmpty()) {
            System.out.println("Dataset kosong.");
            return;
        }
        try {
            System.out.print("Masukkan indeks posisi yang akan dihapus (0 s.d. " + (service.getDataset().size() - 1) + "): ");
            int pos = Integer.parseInt(scanner.nextLine().trim());
            Putusan removed = service.removeAt(pos);
            System.out.println("Berhasil menghapus: " + removed.getCaseId() + " (" + removed.getNomorPerkara() + ")");
        } catch (Exception e) {
            System.out.println("Gagal menghapus: " + e.getMessage());
        }
    }

    private static void handleSearch(Scanner scanner, PutusanService service) {
        System.out.println("Cari berdasarkan");
        System.out.println("1. Case ID");
        System.out.println("2. Nomor Perkara");
        System.out.print("Pilih : ");
        String mode = scanner.nextLine().trim();
        System.out.print("Masukkan kata kunci pencarian: ");
        String query = scanner.nextLine().trim();

        SearchResult result = "2".equals(mode)
                ? service.findByNomorPerkara(query)
                : service.findByCaseId(query);

        if (result.getPutusan() != null) {
            System.out.println("Ditemukan pada indeks " + result.getIndex()
                    + " dalam " + result.getSteps() + " langkah:");
            printPutusanDetail(result.getPutusan());
        } else {
            System.out.println("Data tidak ditemukan setelah " + result.getSteps() + " langkah pemeriksaan.");
        }
    }

    private static void handleFilter(Scanner scanner, PutusanService service) {
        try {
            System.out.println("Kosongkan kriteria jika tidak ingin difilter:");
            System.out.print("Jenis Narkotika (misal Sabu / Ganja): ");
            String jenis = scanner.nextLine().trim();

            System.out.print("Berat Minimum (gram): ");
            String minBeratStr = scanner.nextLine().trim();
            Double minBerat = minBeratStr.isEmpty() ? null : Double.parseDouble(minBeratStr);

            System.out.print("Berat Maksimum (gram): ");
            String maxBeratStr = scanner.nextLine().trim();
            Double maxBerat = maxBeratStr.isEmpty() ? null : Double.parseDouble(maxBeratStr);

            System.out.print("Kata Kunci Pasal Terbukti (misal 114 / 609): ");
            String pasal = scanner.nextLine().trim();

            System.out.print("Vonis Minimum (bulan): ");
            String minVonisStr = scanner.nextLine().trim();
            Integer minVonis = minVonisStr.isEmpty() ? null : Integer.parseInt(minVonisStr);

            System.out.print("Vonis Maksimum (bulan): ");
            String maxVonisStr = scanner.nextLine().trim();
            Integer maxVonis = maxVonisStr.isEmpty() ? null : Integer.parseInt(maxVonisStr);

            DynamicArray<Putusan> results = service.filter(jenis, minBerat, maxBerat, pasal, minVonis, maxVonis);
            System.out.println("\n--- HASIL FILTER (" + results.size() + " RECORD) ---");
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }
        } catch (NumberFormatException e) {
            System.out.println("Input angka filter tidak valid.");
        }
    }

    private static void handleAggregate(PutusanService service) {
        AggregationSummary summary = service.aggregate();
        System.out.println("\n--- RINGKASAN AGREGASI ---");
        System.out.println("Jumlah Perkara    : " + summary.getTotalCases());
        System.out.printf("Rata-rata Vonis   : %.2f bulan%n", summary.getAverageVonisMonths());
        System.out.printf("Total Denda       : Rp %,d%n", summary.getTotalFineRupiah());
        System.out.println("Distribusi Jenis Narkotika:");
        DynamicArray<String> dist = summary.getNarcoticDistribution();
        for (int i = 0; i < dist.size(); i++) {
            System.out.println("  - " + dist.get(i));
        }
    }

    private static void handleSort(Scanner scanner, PutusanService service) {
        System.out.println("1. Terpendek ke Terlama");
        System.out.println("2. Terlama ke Terpendek");     
        System.out.print("Pilih : ");
        String mode = scanner.nextLine().trim();
        boolean asc = !"2".equals(mode);

        DynamicArray<Putusan> sorted = service.sortByVonis(asc);
        System.out.println("\n--- HASIL PENGURUTAN VONIS (" + (asc ? "ASC" : "DESC") + ") ---");
        for (int i = 0; i < sorted.size(); i++) {
            System.out.println((i + 1) + ". " + sorted.get(i));
        }
    }

    private static void handleAuditLog(PutusanService service) {
        ArrayStack<String> log = service.getAuditLog();
        System.out.println("\n--- RIWAYAT AUDIT (URUTAN LIFO / TERBARU DI ATAS) ---");
        for (int i = 0; i < log.size(); i++) {
            System.out.println((i + 1) + ". " + log.get(i));
        }
    }

    private static void handleExport(Scanner scanner, PutusanService service) {
        System.out.print("Nama file ekspor [docs/demo/hasil-ekspor.csv]: ");
        String outPath = scanner.nextLine().trim();
        if (outPath.isEmpty()) {
            outPath = "docs/demo/hasil-ekspor.csv";
        }
        boolean success = service.exportLastResult(outPath);
        if (success) {
            System.out.println("Data berhasil diekspor ke: " + outPath);
        } else {
            System.out.println("Gagal mengekspor data.");
        }
    }

    private static void printPutusanDetail(Putusan p) {
        System.out.println("  Case ID      : " + p.getCaseId());
        System.out.println("  No. Perkara  : " + p.getNomorPerkara());
        System.out.println("  Pengadilan   : " + p.getPengadilan() + " (" + p.getTanggalPutusan() + ")");
        System.out.println("  Terdakwa     : " + p.getTerdakwaKode());
        System.out.println("  Narkotika    : " + p.getJenisNarkotika() + " (" + p.getBeratBarangBuktiGram() + " gram)");
        System.out.println("  Pasal Terbukti: " + p.getPasalTerbukti());
        System.out.println("  Tuntutan/Vonis: " + p.getTuntutanBulan() + " bulan / " + p.getVonisBulan() + " bulan");
        System.out.printf("  Denda        : Rp %,d%n", p.getDendaRupiah());
        System.out.println("  Halaman      : " + p.getSourcePages());
    }
}