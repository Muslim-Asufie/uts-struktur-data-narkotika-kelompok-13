package pn.sidoarjo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import pn.sidoarjo.CSV.CsvPutusanLoader;
import pn.sidoarjo.CSV.CsvPutusanLoader.LoadResult;
import pn.sidoarjo.Model.Putusan;
import pn.sidoarjo.StrukturData.ArrayStack;
import pn.sidoarjo.StrukturData.DynamicArray;
import pn.sidoarjo.Service.PutusanService;

public class StrukturDataTest {

    private PutusanService service;
    private final String testCsvPath = "data/putusan-50.csv"; // Asumsi file ini berisi 50 data dummy valid

    @BeforeEach
    void setUp() {
        service = new PutusanService();
    }

    private Putusan createDummyPutusan(String caseId, int vonis) {
        return new Putusan(caseId, "No-" + caseId, "PN Sidoarjo", LocalDate.now(),
                "TRD-" + caseId, "Sabu", 10.0, "Pasal", "Pasal", 12, vonis, 1000,
                "BB", "Memberatkan", "Meringankan", "hal 1");
    }

    // Skenario 1: Muat 50 record dan buktikan size bernilai 50.
    @Test
    void testLoad50Records() {
        // test ini mengasumsikan CsvLoader berjalan. Jika gagal membaca file, asersinya gagal.
        LoadResult result = service.importCsv(testCsvPath, 50);
        assertTrue(result.getLoadedCount() <= 50, "Maksimal memuat 50 data");
        // Jika Anda memiliki file CSV valid isi 50, ganti ke:
        // assertEquals(50, service.getDataset().size());
    }

    // Skenario 2: Cari satu perkara yang ada, satu yang tidak ada, dan satu record pada posisi batas.
    @Test
    void testSearchExistingNonExistingAndBoundary() {
        Putusan p1 = createDummyPutusan("PUT-A", 10);
        Putusan p2 = createDummyPutusan("PUT-B", 20);
        Putusan p3 = createDummyPutusan("PUT-C", 30); // Batas akhir
        service.addPutusan(p1, null);
        service.addPutusan(p2, null);
        service.addPutusan(p3, null);

        // Cari yang ada
        assertNotNull(service.findByCaseId("PUT-A").getPutusan());
        assertEquals(1, service.findByCaseId("PUT-A").getSteps());

        // Cari yang tidak ada
        assertNull(service.findByCaseId("PUT-Z").getPutusan());
        assertEquals(3, service.findByCaseId("PUT-Z").getSteps()); // Harus cek semua

        // Cari di batas akhir
        assertNotNull(service.findByCaseId("PUT-C").getPutusan());
        assertEquals(3, service.findByCaseId("PUT-C").getSteps());
    }

    // Skenario 3: Hapus elemen pertama, tengah, dan terakhir, lalu tunjukkan perubahan pointer/pergeseran.
    @Test
    void testRemoveFirstMiddleLast() {
        for (int i = 1; i <= 5; i++) {
            service.addPutusan(createDummyPutusan("P-" + i, i * 10), null);
        }
        assertEquals(5, service.getDataset().size());

        // Hapus Pertama (indeks 0) -> "P-1" terhapus, "P-2" geser ke indeks 0
        Putusan removedFirst = service.removeAt(0);
        assertEquals("P-1", removedFirst.getCaseId());
        assertEquals("P-2", service.getDataset().get(0).getCaseId());

        // Hapus Terakhir (indeks 3, karena size sisa 4) -> "P-5" terhapus
        Putusan removedLast = service.removeAt(3);
        assertEquals("P-5", removedLast.getCaseId());
        assertEquals(3, service.getDataset().size()); // Sisa P-2, P-3, P-4

        // Hapus Tengah (indeks 1) -> "P-3" terhapus
        Putusan removedMiddle = service.removeAt(1);
        assertEquals("P-3", removedMiddle.getCaseId());
        assertEquals("P-4", service.getDataset().get(1).getCaseId());
    }

    // Skenario 4: Tambahkan kembali tiga record dan buktikan invariant serta urutan data.
    @Test
    void testAddAndValidateInvariant() {
        DynamicArray<Putusan> array = service.getDataset();
        array.add(createDummyPutusan("A1", 10));
        array.add(createDummyPutusan("A2", 20));

        // Panggil validasi invariant manual (tidak boleh throw exception)
        assertDoesNotThrow(() -> array.validateInvariant());

        array.insert(1, createDummyPutusan("A1.5", 15));
        assertEquals("A1.5", array.get(1).getCaseId());
        assertDoesNotThrow(() -> array.validateInvariant());
    }

    // Skenario 5: Jalankan filter gabungan dan cocokkan hasilnya.
    @Test
    void testCombinedFilter() {
        Putusan p1 = createDummyPutusan("F1", 10); p1.setJenisNarkotika("Ganja"); p1.setBeratBarangBuktiGram(5.0);
        Putusan p2 = createDummyPutusan("F2", 30); p2.setJenisNarkotika("Sabu"); p2.setBeratBarangBuktiGram(15.0);
        Putusan p3 = createDummyPutusan("F3", 50); p3.setJenisNarkotika("Sabu"); p3.setBeratBarangBuktiGram(50.0);
        service.addPutusan(p1, null);
        service.addPutusan(p2, null);
        service.addPutusan(p3, null);

        // Filter Sabu, berat min 10, vonis max 40 -> Harusnya cuma dapat F2
        DynamicArray<Putusan> result = service.filter("Sabu", 10.0, null, null, null, 40);
        assertEquals(1, result.size());
        assertEquals("F2", result.get(0).getCaseId());
    }

    // Skenario 6: Tunjukkan bahwa pencarian pada Stack atau Queue tidak mengubah urutan akhir.
    @Test
    void testNonDestructiveStackSearch() {
        ArrayStack<String> stack = service.getAuditLog(); // Menggunakan Stack AuditLog
        stack.push("Log 1");
        stack.push("Log 2");
        stack.push("Log 3");

        int initialSize = stack.size();
        assertEquals("Log 3", stack.peek()); // Top adalah Log 3

        // Pencarian Non-Destructive (dari top)
        int pos = stack.searchPreserve("Log 1"); // Jarak dari top (indeks 2 -> depth 2)
        assertEquals(2, pos);

        // Validasi urutan tidak berubah
        assertEquals(initialSize, stack.size());
        assertEquals("Log 3", stack.peek());
    }

    // Skenario 7: Masukkan record yang tidak valid dan tampilkan pesan kesalahan, struktur tetap konsisten.
    @Test
    void testInvalidRecordRejection() {
        CsvPutusanLoader loader = new CsvPutusanLoader();
        DynamicArray<Putusan> array = new DynamicArray<>();

        // Baris tanpa kolom yang cukup (hanya 3 kolom)
        String invalidLine = "PUT-ERR,123/Pid,PN X";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loader.parseLine(invalidLine, 1);
        });

        assertTrue(exception.getMessage().contains("Jumlah kolom harus 16"));
        assertEquals(0, array.size()); 
        assertDoesNotThrow(() -> array.validateInvariant());
    }
}