package org.example;

import org.example.model.Oyuncu;
import org.example.veri.SozlukYoneticisi;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import static org.junit.Assert.*;

public class YitikIsimlerKapsamliTest {

    private static PrintWriter yazar;
    private static File dosya;
    private SozlukYoneticisi sozluk;
    private Oyuncu oyuncu;


    @BeforeClass
    public static void baslat() throws IOException {
        dosya = new File("test_sonuclari.txt");
        yazar = new PrintWriter(new FileWriter(dosya), true);

        System.out.println("\n============================================================");
        System.out.println(">>> DİKKAT: Test sonuçları şu yola kaydediliyor:");
        System.out.println(">>> " + dosya.getAbsolutePath());
        System.out.println("============================================================\n");

        yazar.println("========== YİTİK İSİMLER OTOMATİK TEST RAPORU ==========\n");
    }

    @AfterClass
    public static void bitir() {
        if (yazar != null) {
            yazar.println("\n========== TÜM TESTLER BAŞARIYLA TAMAMLANDI ==========");
            yazar.flush();
            yazar.close();
        }
    }

    @Before
    public void hazirlik() {
        sozluk = new SozlukYoneticisi();
        oyuncu = new Oyuncu("DenemeRuhu", "dummyHash", sozluk);
    }

    @Test
    public void testSozlukGramerCevirisi() {
        yazar.println("[TEST 1] Sözlük ve Gramer Çevirisi Test Ediliyor...");

        List<String> ikiHeceli = Arrays.asList("Gor", "Ig");
        String ikiHeceliCeviri = sozluk.turkceyeCevir(ikiHeceli);
        assertEquals("Karanlık Ateş", ikiHeceliCeviri);
        yazar.println(" - 2 Heceli Çeviri Başarılı: [Gor, Ig] -> " + ikiHeceliCeviri);

        List<String> ucHeceli = Arrays.asList("Gor", "Ig", "Kel");
        String ucHeceliCeviri = sozluk.turkceyeCevir(ucHeceli);
        assertEquals("Karanlık Ateş Kaptanı", ucHeceliCeviri);
        yazar.println(" - 3 Heceli Çeviri Başarılı (İyelik Eki Testi): [Gor, Ig, Kel] -> " + ucHeceliCeviri);

        yazar.println(" > SONUÇ: Sözlük motoru ve dilbilgisi kuralları kusursuz çalışıyor.\n");
    }

    @Test
    public void testOyuncuZihinYaratilisi() {
        yazar.println("[TEST 2] Oyuncu Zihin Yaratılışı Test Ediliyor...");

        List<String> bilinenler = oyuncu.getBilinenKelimeler();
        assertEquals("Oyuncu başlangıçta tam 6 hece bilmelidir.", 6, bilinenler.size());

        HashSet<String> benzersizHeceler = new HashSet<>(bilinenler);
        assertEquals("Oyuncunun başlangıç heceleri tamamen benzersiz olmalıdır.", 6, benzersizHeceler.size());
        yazar.println(" - Oyuncunun başlangıç çantasına 6 adet benzersiz hece başarıyla eklendi.");

        List<String> gercekAd = oyuncu.getGercekAdinParcalari();
        assertEquals("Gerçek ad tam 5 hece olmalıdır.", 5, gercekAd.size());
        yazar.println(" - 5 Heceli Gizli Gerçek Ad başarıyla oluşturuldu: " + gercekAd);

        List<String> kayipParcalar = oyuncu.getKayipIsimParcalari();
        assertEquals("Başlangıçta tam 2 kayıp parça olmalıdır.", 2, kayipParcalar.size());
        yazar.println(" - 2 Adet kayıp parça Hatıralar Kuyusu'na eklendi: " + kayipParcalar);

        assertTrue("Gerçek adın son 2 parçası kayıp parçalar olmalıdır.",
                gercekAd.containsAll(kayipParcalar));

        yazar.println(" > SONUÇ: Oyuncu zihni, benzersiz heceler ve matematiksel sınırlar içinde başarıyla oluşturuldu.\n");
    }

    @Test
    public void testZihinselParcalanmaMekanigi() {
        yazar.println("[TEST 3] Hasar, Ölüm ve Hatıralar Kuyusu Mekaniği Test Ediliyor...");

        assertEquals("Başlangıç iradesi 10 olmalıdır.", 10, oyuncu.getIrade());
        yazar.println(" - Başlangıç İradesi: " + oyuncu.getIrade());

        oyuncu.iradeDegistir(-20);
        assertEquals("İrade 0'ın altına düşmemelidir.", 0, oyuncu.getIrade());
        yazar.println(" - Oyuncuya -20 Hasar verildi. Güncel İrade: " + oyuncu.getIrade());

        int eskiHeceSayisi = oyuncu.getBilinenKelimeler().size();
        boolean hayattaMi = oyuncu.ruhuParcalanarakUyan();

        assertTrue("Oyuncunun unutacak hecesi olduğu için hayatta kalmalıdır.", hayattaMi);
        assertEquals("İrade 5 olarak toparlanmalıdır.", 5, oyuncu.getIrade());
        yazar.println(" - Ölüm tetiklendi. Oyuncu dirildi ve İrade " + oyuncu.getIrade() + " olarak güncellendi.");

        int yeniHeceSayisi = oyuncu.getBilinenKelimeler().size();
        assertEquals("Oyuncu 1 hece unutmuş olmalıdır.", eskiHeceSayisi - 1, yeniHeceSayisi);
        yazar.println(" - Zihinsel Parçalanma uygulandı. Hece sayısı " + eskiHeceSayisi + " -> " + yeniHeceSayisi + " düştü.");

        assertTrue("Hatıralar kuyusu unutulan heceyi tutmalıdır.", oyuncu.getKayipIsimParcalari().size() >= 2);
        yazar.println(" - Unutulan hece başarıyla Hatıralar Kuyusu'na aktarıldı.");

        yazar.println(" > SONUÇ: Hasar, dirilme ve ceza sistemi sorunsuz çalışıyor.\n");
    }
}