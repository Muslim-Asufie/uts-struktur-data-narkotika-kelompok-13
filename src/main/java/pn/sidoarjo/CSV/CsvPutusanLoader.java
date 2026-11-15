package pn.sidoarjo.CSV;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import pn.sidoarjo.Model.Putusan;
import pn.sidoarjo.StrukturData.DynamicArray;

public class CsvPutusanLoader {

    public static class LoadResult {
        private final int loadedCount;
        private final int rejectedCount;
        private final DynamicArray<String> errorLogs;

        public LoadResult(int loadedCount, int rejectedCount, DynamicArray<String> errorLogs) {
            this.loadedCount = loadedCount;
            this.rejectedCount = rejectedCount;
            this.errorLogs = errorLogs;
        }

        public int getLoadedCount() { return loadedCount; }
        public int getRejectedCount() { return rejectedCount; }
        public DynamicArray<String> getErrorLogs() { return errorLogs; }
    }

    public LoadResult loadFromFile(String filePath, DynamicArray<Putusan> targetArray, int maxRecords) {
        int loaded = 0;
        int rejected = 0;
        DynamicArray<String> errors = new DynamicArray<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); 
            if (line == null) {
                errors.add("File CSV kosong.");
                return new LoadResult(0, 0, errors);
            }

            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                if (maxRecords > 0 && loaded >= maxRecords) {
                    break;
                }

                try {
                    Putusan putusan = parseLine(line, lineNumber);
                    targetArray.add(putusan);
                    loaded++;
                } catch (IllegalArgumentException e) {
                    rejected++;
                    errors.add("Baris " + lineNumber + " ditolak: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            errors.add("Gagal membaca file '" + filePath + "': " + e.getMessage());
        }

        targetArray.validateInvariant();
        return new LoadResult(loaded, rejected, errors);
    }

    public Putusan parseLine(String line, int lineNumber) {
        String[] cols = splitCsvLine(line);
        if (cols.length != 16) {
            throw new IllegalArgumentException("Jumlah kolom harus 16, ditemukan " + cols.length);
        }

        String caseId = cols[0].trim();
        String nomorPerkara = cols[1].trim();
        String pengadilan = cols[2].trim();
        String tanggalStr = cols[3].trim();
        String terdakwaKode = cols[4].trim();
        String jenisNarkotika = cols[5].trim();
        String beratStr = cols[6].trim();
        String pasalDakwaan = cols[7].trim();
        String pasalTerbukti = cols[8].trim();
        String tuntutanStr = cols[9].trim();
        String vonisStr = cols[10].trim();
        String dendaStr = cols[11].trim();
        String barangBukti = cols[12].trim();
        String keadaanMemberatkan = cols[13].trim();
        String keadaanMeringankan = cols[14].trim();
        String sourcePages = cols[15].trim();

        if (caseId.isEmpty() || nomorPerkara.isEmpty() || jenisNarkotika.isEmpty()) {
            throw new IllegalArgumentException("Atribut identitas utama (caseId/nomorPerkara/jenisNarkotika) kosong");
        }

        LocalDate tanggalPutusan;
        try {
            tanggalPutusan = LocalDate.parse(tanggalStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format tanggal tidak valid (harus YYYY-MM-DD): " + tanggalStr);
        }

        double beratGram;
        int tuntutanBulan;
        int vonisBulan;
        long dendaRupiah;

        try {
            beratGram = Double.parseDouble(beratStr);
            tuntutanBulan = Integer.parseInt(tuntutanStr);
            vonisBulan = Integer.parseInt(vonisStr);
            dendaRupiah = Long.parseLong(dendaStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Nilai numerik (berat/tuntutan/vonis/denda) tidak valid");
        }

        if (beratGram < 0 || tuntutanBulan < 0 || vonisBulan < 0 || dendaRupiah < 0) {
            throw new IllegalArgumentException("Nilai numerik tidak boleh negatif");
        }

        return new Putusan(
                caseId, nomorPerkara, pengadilan, tanggalPutusan, terdakwaKode,
                jenisNarkotika, beratGram, pasalDakwaan, pasalTerbukti,
                tuntutanBulan, vonisBulan, dendaRupiah, barangBukti,
                keadaanMemberatkan, keadaanMeringankan, sourcePages
        );
    }

    private String[] splitCsvLine(String line) {
        DynamicArray<String> tokens = new DynamicArray<>(16);
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        tokens.add(current.toString());

        String[] result = new String[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            result[i] = tokens.get(i);
        }
        return result;
    }
}

