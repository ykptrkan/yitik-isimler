package org.example.veri;

import com.google.gson.Gson;
import org.example.model.Bolge;
import org.example.model.BolgeIcerigi;
import org.example.model.HaritaVerisi;
import org.example.model.Karsilasma;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VeriYoneticisi {
    private final Map<String, Bolge> bolgeSozlugu = new HashMap<>();
    private final Map<String, Karsilasma> karsilasmaSozlugu = new HashMap<>();
    private final Gson gson = new Gson();

    public VeriYoneticisi() {
        haritayiVeBolgeleriYukle();
    }

    private void haritayiVeBolgeleriYukle() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("harita.json")) {
            if (is == null) {
                System.err.println("Kritik Hata: harita.json dosyası bulunamadı!");
                return;
            }

            Reader okuyucu = new InputStreamReader(is, StandardCharsets.UTF_8);
            HaritaVerisi haritaVerisi = gson.fromJson(okuyucu, HaritaVerisi.class);

            if (haritaVerisi != null && haritaVerisi.bolgeler() != null) {
                for (Bolge b : haritaVerisi.bolgeler()) {
                    bolgeSozlugu.put(b.id(), b);
                    bolgeIceriginiYukle(b.dosyaYolu());
                }
                System.out.println("[+] Dünya haritası belleğe yüklendi. (Bölge: " + bolgeSozlugu.size() + ", Karşılaşma: " + karsilasmaSozlugu.size() + ")");
            }
        } catch (Exception e) {
            System.err.println("Harita verileri okunurken hata oluştu: " + e.getMessage());
        }
    }

    private void bolgeIceriginiYukle(String dosyaYolu) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(dosyaYolu)) {
            if (is == null) {
                System.err.println("Uyarı: Bölge dosyası bulunamadı -> " + dosyaYolu);
                return;
            }

            Reader okuyucu = new InputStreamReader(is, StandardCharsets.UTF_8);
            BolgeIcerigi icerik = gson.fromJson(okuyucu, BolgeIcerigi.class);

            if (icerik != null && icerik.karsilasmalar() != null) {
                for (Karsilasma k : icerik.karsilasmalar()) {
                    karsilasmaSozlugu.put(k.id(), k);
                }
            }
        } catch (Exception e) {
            System.err.println("Bölge içeriği okunurken hata (" + dosyaYolu + "): " + e.getMessage());
        }
    }

    public Optional<Bolge> bolgeGetir(String id) {
        return Optional.ofNullable(bolgeSozlugu.get(id));
    }

    public Optional<Karsilasma> karsilasmaGetir(String id) {
        return Optional.ofNullable(karsilasmaSozlugu.get(id));
    }

    public Map<String, Bolge> getTumBolgeler() {
        return bolgeSozlugu;
    }
}