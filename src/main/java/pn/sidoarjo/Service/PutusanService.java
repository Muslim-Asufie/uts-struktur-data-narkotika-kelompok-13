package pn.sidoarjo.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import pn.sidoarjo.CSV.CsvPutusanLoader;
import pn.sidoarjo.CSV.CsvPutusanLoader.LoadResult;
import pn.sidoarjo.Model.Putusan;
import pn.sidoarjo.StrukturData.ArrayStack;
import pn.sidoarjo.StrukturData.DynamicArray;

public class PutusanService {
    private final DynamicArray<Putusan> dataset;
    private final ArrayStack<String> auditLog;
    private final CsvPutusanLoader loader;
    private DynamicArray<Putusan> lastQueryResult;

    public PutusanService() {
        this.dataset = new DynamicArray<>(50);
        this.auditLog = new ArrayStack<>(32);
        this.loader = new CsvPutusanLoader();
        this.lastQueryResult = new DynamicArray<>();
    }

    public static class SearchResult {
        private final Putusan putusan;
        private final int index;
        private final int steps;

        public SearchResult(Putusan putusan, int index, int steps) {
            this.putusan = putusan;
            this.index = index;
            this.steps = steps;
        }

        public Putusan getPutusan() { return putusan; }
        public int getIndex() { return index; }
        public int getSteps() { return steps; }
    }

    public static class AggregationSummary {
        private final int totalCases;
        private final double averageVonisMonths;
        private final long totalFineRupiah;
        private final DynamicArray<String> narcoticDistribution;

        public AggregationSummary(int totalCases, double averageVonisMonths, long totalFineRupiah, DynamicArray<String> narcoticDistribution) {
            this.totalCases = totalCases;
            this.averageVonisMonths = averageVonisMonths;
            this.totalFineRupiah = totalFineRupiah;
            this.narcoticDistribution = narcoticDistribution;
        }

        public int getTotalCases() { 
            return totalCases; 
        }

        public double getAverageVonisMonths() { 
            return averageVonisMonths; 
        }

        public long getTotalFineRupiah() { 
            return totalFineRupiah; 
        }

        public DynamicArray<String> getNarcoticDistribution() {
            return narcoticDistribution; 
        }
    }

    public LoadResult importCsv(String filePath, int targetLimit) {
        dataset.clear();
        LoadResult result = loader.loadFromFile(filePath, dataset, targetLimit);
        copyDatasetToLastResult();
        recordAudit("IMPOR CSV: memuat " + result.getLoadedCount() + " record valid, menolak " + result.getRejectedCount() + " baris");
        return result;
    }

    public DynamicArray<Putusan> getAll() {
        recordAudit("TAMPIL: melihat " + dataset.size() + " putusan");
        return dataset;
    }

    public void addPutusan(Putusan putusan, Integer position) {
        if (putusan == null) {
            throw new IllegalArgumentException("Objek Putusan tidak boleh null");
        }
        if (findByCaseId(putusan.getCaseId()).getPutusan() != null) {
            throw new IllegalArgumentException("Case ID sudah terdaftar: " + putusan.getCaseId());
        }

        if (position == null) {
            dataset.add(putusan);
            recordAudit("TAMBAH (AKHIR): " + putusan.getCaseId());
        } else {
            dataset.insert(position, putusan);
            recordAudit("TAMBAH (POSISI " + position + "): " + putusan.getCaseId());
        }
        dataset.validateInvariant();
    }

    public Putusan removeAt(int position) {
        Putusan removed = dataset.remove(position);
        dataset.validateInvariant();
        recordAudit("HAPUS (POSISI " + position + "): " + removed.getCaseId());
        return removed;
    }

    public SearchResult findByCaseId(String caseId) {
        int steps = 0;
        if (caseId == null || caseId.trim().isEmpty()) {
            return new SearchResult(null, -1, 0);
        }
        String target = caseId.trim();
        for (int i = 0; i < dataset.size(); i++) {
            steps++;
            Putusan current = dataset.get(i);
            if (current.getCaseId().equalsIgnoreCase(target)) {
                lastQueryResult = new DynamicArray<>(1);
                lastQueryResult.add(current);
                recordAudit("CARI caseId='" + target + "' -> DITEMUKAN (" + steps + " langkah)");
                return new SearchResult(current, i, steps);
            }
        }
        recordAudit("CARI caseId='" + target + "' -> TIDAK DITEMUKAN (" + steps + " langkah)");
        return new SearchResult(null, -1, steps);
    }

    public SearchResult findByNomorPerkara(String nomorPerkara) {
        int steps = 0;
        if (nomorPerkara == null || nomorPerkara.trim().isEmpty()) {
            return new SearchResult(null, -1, 0);
        }
        String target = nomorPerkara.trim();
        for (int i = 0; i < dataset.size(); i++) {
            steps++;
            Putusan current = dataset.get(i);
            if (current.getNomorPerkara().equalsIgnoreCase(target)) {
                lastQueryResult = new DynamicArray<>(1);
                lastQueryResult.add(current);
                recordAudit("CARI nomorPerkara='" + target + "' -> DITEMUKAN (" + steps + " langkah)");
                return new SearchResult(current, i, steps);
            }
        }
        recordAudit("CARI nomorPerkara='" + target + "' -> TIDAK DITEMUKAN (" + steps + " langkah)");
        return new SearchResult(null, -1, steps);
    }

    public DynamicArray<Putusan> filter(String jenisNarkotika, Double minBerat, Double maxBerat,
                                        String pasalKeyword, Integer minVonis, Integer maxVonis) {
        DynamicArray<Putusan> filtered = new DynamicArray<>();

        for (int i = 0; i < dataset.size(); i++) {
            Putusan p = dataset.get(i);

            if (jenisNarkotika != null && !jenisNarkotika.trim().isEmpty()) {
                if (!p.getJenisNarkotika().toLowerCase().contains(jenisNarkotika.trim().toLowerCase())) {
                    continue;
                }
            }
            if (minBerat != null && p.getBeratBarangBuktiGram() < minBerat) {
                continue;
            }
            if (maxBerat != null && p.getBeratBarangBuktiGram() > maxBerat) {
                continue;
            }
            if (pasalKeyword != null && !pasalKeyword.trim().isEmpty()) {
                if (!p.getPasalTerbukti().toLowerCase().contains(pasalKeyword.trim().toLowerCase())) {
                    continue;
                }
            }
            if (minVonis != null && p.getVonisBulan() < minVonis) {
                continue;
            }
            if (maxVonis != null && p.getVonisBulan() > maxVonis) {
                continue;
            }

            filtered.add(p);
        }

        lastQueryResult = filtered;
        recordAudit("FILTER: menghasilkan " + filtered.size() + " putusan");
        return filtered;
    }

    public AggregationSummary aggregate() {
        int total = dataset.size();
        if (total == 0) {
            recordAudit("AGREGASI: dataset kosong");
            return new AggregationSummary(0, 0.0, 0L, new DynamicArray<>());
        }

        long sumVonis = 0;
        long sumDenda = 0;
        DynamicArray<String> jenisList = new DynamicArray<>();
        DynamicArray<Integer> countList = new DynamicArray<>();

        for (int i = 0; i < total; i++) {
            Putusan p = dataset.get(i);
            sumVonis += p.getVonisBulan();
            sumDenda += p.getDendaRupiah();

            String jenis = p.getJenisNarkotika();
            int idx = findStringIndex(jenisList, jenis);
            if (idx == -1) {
                jenisList.add(jenis);
                countList.add(1);
            } else {
                countList.set(idx, countList.get(idx) + 1);
            }
        }

        DynamicArray<String> distribution = new DynamicArray<>(jenisList.size());
        for (int i = 0; i < jenisList.size(); i++) {
            distribution.add(jenisList.get(i) + ": " + countList.get(i) + " perkara");
        }

        double avgVonis = (double) sumVonis / total;
        recordAudit("AGREGASI: dihitung pada " + total + " perkara");
        return new AggregationSummary(total, avgVonis, sumDenda, distribution);
    }

    public DynamicArray<Putusan> sortByVonis(boolean ascending) {
        DynamicArray<Putusan> sorted = new DynamicArray<>(Math.max(1, dataset.size()));
        for (int i = 0; i < dataset.size(); i++) {
            sorted.add(dataset.get(i));
        }

        for (int i = 1; i < sorted.size(); i++) {
            Putusan key = sorted.get(i);
            int j = i - 1;
            while (j >= 0 && shouldSwap(sorted.get(j).getVonisBulan(), key.getVonisBulan(), ascending)) {
                sorted.set(j + 1, sorted.get(j));
                j--;
            }
            sorted.set(j + 1, key);
        }

        dataset.validateInvariant();
        sorted.validateInvariant();
        lastQueryResult = sorted;
        recordAudit("URUTKAN (vonisBulan " + (ascending ? "ASC" : "DESC") + "): " + sorted.size() + " record");
        return sorted;
    }

    public boolean exportLastResult(String outputPath) {
        DynamicArray<Putusan> source = lastQueryResult.isEmpty() ? dataset : lastQueryResult;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("case_id,nomor_perkara,pengadilan,tanggal_putusan,terdakwa_kode,jenis_narkotika,berat_barang_bukti_gram,pasal_dakwaan,pasal_terbukti,tuntutan_bulan,vonis_bulan,denda_rupiah,barang_bukti,keadaan_memberatkan,keadaan_meringankan,source_pages");
            writer.newLine();

            for (int i = 0; i < source.size(); i++) {
                Putusan p = source.get(i);
                writer.write(String.format(
                        "%s,\"%s\",\"%s\",%s,%s,\"%s\",%.3f,\"%s\",\"%s\",%d,%d,%d,\"%s\",\"%s\",\"%s\",\"%s\"",
                        p.getCaseId(),
                        p.getNomorPerkara(),
                        p.getPengadilan(),
                        p.getTanggalPutusan(),
                        p.getTerdakwaKode(),
                        p.getJenisNarkotika(),
                        p.getBeratBarangBuktiGram(),
                        p.getPasalDakwaan(),
                        p.getPasalTerbukti(),
                        p.getTuntutanBulan(),
                        p.getVonisBulan(),
                        p.getDendaRupiah(),
                        p.getBarangBukti(),
                        p.getKeadaanMemberatkan(),
                        p.getKeadaanMeringankan(),
                        p.getSourcePages()
                ));
                writer.newLine();
            }
            recordAudit("EKSPOR: " + source.size() + " record disimpan ke " + outputPath);
            return true;
        } catch (IOException e) {
            recordAudit("EKSPOR GAGAL ke " + outputPath + ": " + e.getMessage());
            return false;
        }
    }

    public ArrayStack<String> getAuditLog() {
        return auditLog;
    }

    public DynamicArray<Putusan> getDataset() {
        return dataset;
    }

    private void recordAudit(String action) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        auditLog.push("[" + timestamp + "] " + action);
        auditLog.validateInvariant();
    }

    private void copyDatasetToLastResult() {
        lastQueryResult = new DynamicArray<>(Math.max(1, dataset.size()));
        for (int i = 0; i < dataset.size(); i++) {
            lastQueryResult.add(dataset.get(i));
        }
    }

    private int findStringIndex(DynamicArray<String> list, String target) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equalsIgnoreCase(target)) {
                return i;
            }
        }
        return -1;
    }

    private boolean shouldSwap(int leftVonis, int rightVonis, boolean ascending) {
        return ascending ? leftVonis > rightVonis : leftVonis < rightVonis;
    }
}
