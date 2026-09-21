package org.example.model;

import org.example.veri.SozlukYoneticisi;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Oyuncu {
    private String isim;
    private String sifreHash;
    private int irade = 10;
    private int yanki = 10;
    private String mevcutKonum = "kul_limani";
    private List<String> tamamlananOlaylar = new ArrayList<>();
    private List<String> kesfedilenBolgeler = new ArrayList<>();
    private List<String> gercekAdinParcalari = new ArrayList<>();
    private List<String> kayipIsimParcalari = new ArrayList<>();
    private List<String> bilinenKelimeler = new ArrayList<>();

    public Oyuncu() {}

    public Oyuncu(String isim, String sifreHash, SozlukYoneticisi sozlukYoneticisi) {
        this.isim = isim;
        this.sifreHash = sifreHash;
        zihinYarat(sozlukYoneticisi);
        kesifEkle("kul_limani");
    }

    private void zihinYarat(SozlukYoneticisi sozluk) {
        List<String> oyuncuHeceleri = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            String durum;
            do {
                durum = sozluk.rastgeleIkiHeceliUret().get(0);
            } while (oyuncuHeceleri.contains(durum));
            oyuncuHeceleri.add(durum);
            String element;
            do {
                element = sozluk.rastgeleIkiHeceliUret().get(1);
            } while (oyuncuHeceleri.contains(element));
            oyuncuHeceleri.add(element);
        }
        this.bilinenKelimeler.addAll(oyuncuHeceleri);
        String bilinenDurum1 = oyuncuHeceleri.get(0);
        String bilinenElement1 = oyuncuHeceleri.get(1);
        String bilinenDurum2 = oyuncuHeceleri.get(2);

        String kayipElement;
        do {
            kayipElement = sozluk.rastgeleIkiHeceliUret().get(1);
        } while (this.bilinenKelimeler.contains(kayipElement));

        String kayipUnvan = sozluk.rastgeleUnvanSec();

        gercekAdinParcalari.add(bilinenDurum1);
        gercekAdinParcalari.add(bilinenElement1);
        gercekAdinParcalari.add(bilinenDurum2);
        gercekAdinParcalari.add(kayipElement);
        gercekAdinParcalari.add(kayipUnvan);

        kayipIsimParcalari.add(kayipElement);
        kayipIsimParcalari.add(kayipUnvan);
    }

    public String getIsim() { return isim; }
    public String getMevcutKonum() { return mevcutKonum; }
    public void setMevcutKonum(String konum) { this.mevcutKonum = konum; }
    public int getIrade() { return irade; }
    public int getYanki() { return yanki; }
    public List<String> getBilinenKelimeler() { return bilinenKelimeler; }
    public List<String> getKayipIsimParcalari() { return kayipIsimParcalari; }
    public List<String> getGercekAdinParcalari() { return gercekAdinParcalari; }
    public List<String> getTamamlananOlaylar() { return tamamlananOlaylar; }
    public List<String> getKesfedilenBolgeler() { return kesfedilenBolgeler; }

    public boolean sifreDogrula(String girilenSifreHash) {
        return this.sifreHash != null && this.sifreHash.equals(girilenSifreHash);
    }

    public void iradeDegistir(int miktar) {
        this.irade += miktar;
        if (this.irade < 0) this.irade = 0;
        if (this.irade > 20) this.irade = 20;
    }

    public void yankiDegistir(int miktar) {
        this.yanki += miktar;
        if (this.yanki < 0) this.yanki = 0;
    }

    public void olayTamamla(String olayId) {
        if (!tamamlananOlaylar.contains(olayId)) {
            tamamlananOlaylar.add(olayId);
        }
    }

    public void kesifEkle(String bolgeId) {
        if (!kesfedilenBolgeler.contains(bolgeId)) {
            kesfedilenBolgeler.add(bolgeId);
        }
    }

    public boolean ruhuParcalanarakUyan() {
        if (!this.bilinenKelimeler.isEmpty()) {
            this.irade = 5;
            int rastgeleIndex = new Random().nextInt(this.bilinenKelimeler.size());
            String unutulanKelime = this.bilinenKelimeler.remove(rastgeleIndex);

            if (!kayipIsimParcalari.contains(unutulanKelime)) {
                kayipIsimParcalari.add(unutulanKelime);
            }

            System.out.println("\n[!] ZİHNİN PARÇALANDI...");
            System.out.println("[!] Hiçliğin kıyısından döndün ama ağır bir bedel ödedin.");
            System.out.println("[!] '" + unutulanKelime + "' hecesi zihninden silinip Hatıralar Kuyusu'na düştü.");
            System.out.println("[!] Gözlerini tekrar açtığında iraden zar zor toparlanmıştı (İrade: 5).");
            return true;
        }
        return false;
    }
}