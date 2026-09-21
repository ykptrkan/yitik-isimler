package org.example.durum;

import org.example.ana.OyunMotoru;
import org.example.model.Bolge;
import org.example.model.Karsilasma;
import org.example.model.Oyuncu;
import java.util.List;
import java.util.Optional;

public class KesifDurumu implements OyunDurumu {
    private final OyunMotoru motor;
    private final Oyuncu oyuncu;

    public KesifDurumu(OyunMotoru motor, Oyuncu oyuncu) {
        this.motor = motor;
        this.oyuncu = oyuncu;
    }

    @Override
    public void baslat() {
        Bolge mevcutBolge = motor.getVeriYoneticisi().bolgeGetir(oyuncu.getMevcutKonum()).orElse(null);
        if (mevcutBolge != null) {
            System.out.println("\n--------------------------------------------------");
            System.out.println("  " + mevcutBolge.isim() + " bölgesine adım attın...");
            System.out.println("--------------------------------------------------\n");
        }
    }

    @Override
    public void ciz() {
        Bolge mevcutBolge = motor.getVeriYoneticisi().bolgeGetir(oyuncu.getMevcutKonum()).orElse(null);
        if (mevcutBolge == null) return;

        System.out.println("\nBulunduğun Konum: " + mevcutBolge.isim().toUpperCase());
        System.out.println("Mevcut Durumun: [İrade: " + oyuncu.getIrade() + "/20 | Yankı: " + oyuncu.getYanki() + "]");
        System.out.println("\nNe yapmak istersin?");

        String karsilasmaId = mevcutBolgeKarsilasmaIdGetir(oyuncu.getMevcutKonum());

        System.out.println("1 - Etrafı Araştır (İpuçlarını ve Heceleri Topla)");

        if (oyuncu.getTamamlananOlaylar().contains(karsilasmaId)) {
            System.out.println("2 - İlerle (Bölge Arındırıldı, tehlike yok)");
        } else {
            System.out.println("2 - İlerle (Bölgenin Hakimi ile Yüzleş)");
        }

        System.out.println("3 - Seyahat Et (Bağlantılı Yollara Git)");
        System.out.println("4 - Envanterine ve Zihnine Bak");
        System.out.println("5 - Takımada Haritasını İncele");
        System.out.println("6 - Kamp Kur (5 Yankı Karşılığında Dinlen)");
        System.out.println("9 - Oyunu Kaydet");
        System.out.println("0 - Ana Menüye Dön");
    }

    @Override
    public void guncelle(OyunMotoru motor) {
        int secim = motor.getGirdiYoneticisi().sayiOku("Eylemin");
        String karsilasmaId = mevcutBolgeKarsilasmaIdGetir(oyuncu.getMevcutKonum());

        switch (secim) {
            case 1 -> etrafiArastir(karsilasmaId);
            case 2 -> {
                if (oyuncu.getTamamlananOlaylar().contains(karsilasmaId)) {
                    System.out.println("\n> Burada yapacak bir şey kalmadı. Rüzgar seni başka yönlere çağırıyor.");
                } else {
                    System.out.println("\n> Karanlığın kalbine doğru ilerliyorsun...");
                    motor.durumDegistir(new RituelDurumu(oyuncu, karsilasmaId));
                }
            }
            case 3 -> seyahatEt(karsilasmaId);
            case 4 -> zihneBak();
            case 5 -> haritayiCiz();
            case 6 -> kampKur();
            case 9 -> {
                motor.getKayitYoneticisi().kaydet(oyuncu);
                System.out.println("\n=> Anıların deftere işlendi. Güvendesin.");
            }
            case 0 -> {
                System.out.println("\n> Bilincin yavaşça kapanıyor...");
                motor.durumDegistir(new AnaMenuDurumu());
            }
            default -> System.out.println("\n> Bu yöne gidemezsin.");
        }
    }

    private void kampKur() {
        if (oyuncu.getYanki() < 5) {
            System.out.println("\n[!] Yeterli Yankı yok. Ateşi harlayacak enerjin kalmamış.");
            return;
        }

        System.out.println("\n> Kamp ateşini yaktın. Gölgeler geri çekildi. ( -5 Yankı )");
        oyuncu.yankiDegistir(-5);

        int eksikIrade = 20 - oyuncu.getIrade();
        if (eksikIrade > 0) {
            oyuncu.iradeDegistir(eksikIrade);
            System.out.println("> Zihnin dinlendi. İraden tamamen yenilendi. [İrade: 20]");
        }

        System.out.println("\nAteşin başında otururken Hatıralar Kuyusu'na dalmak ister misin?");
        System.out.println("1 - Evet, kayıp bir parçamı geri al (Maliyet: 15 Yankı)");
        System.out.println("0 - Hayır, dinlenmeye devam et");

        int secim = motor.getGirdiYoneticisi().sayiOku("Seçiminiz");
        if (secim == 1) {
            if (oyuncu.getYanki() >= 15) {
                if (!oyuncu.getKayipIsimParcalari().isEmpty()) {
                    oyuncu.yankiDegistir(-15);
                    String geriAlinanHece = oyuncu.getKayipIsimParcalari().remove(0);
                    oyuncu.getBilinenKelimeler().add(geriAlinanHece);

                    String anlam = motor.getSozlukYoneticisi().heceAnlami(geriAlinanHece);
                    System.out.println("\n[+] KUYUDAN BİR YANKI ÇIKTI: '" + geriAlinanHece + "' (" + anlam + ") hecesini tekrar hatırladın!");
                } else {
                    System.out.println("\n> Zihninde kayıp bir parça yok. Kuyu tamamen durgun.");
                }
            } else {
                System.out.println("\n[!] Kuyunun derinliklerine ulaşmak için yeterli Yankı'ya (15) sahip değilsin.");
            }
        }
    }

    private void etrafiArastir(String karsilasmaId) {
        if (oyuncu.getTamamlananOlaylar().contains(karsilasmaId + "_arastirma")) {
            System.out.println("\n> Buradaki tüm sırları zaten açığa çıkardın. Kadim kelimeler zihnine çoktan kazındı.");
            return;
        }

        Optional<Karsilasma> karsilasmaOpt = motor.getVeriYoneticisi().karsilasmaGetir(karsilasmaId);
        if (karsilasmaOpt.isEmpty()) {
            System.out.println("\n> Burada dikkatini çeken hiçbir şey yok.");
            return;
        }

        Karsilasma.Arastirma arastirma = karsilasmaOpt.get().arastirma();
        System.out.println("\n[ ETRAFI ARAŞTIRIYORSUN ]");

        List<String> ogrenilecekler = arastirma.ogrenilecekHeceler();

        for (Karsilasma.Ipucu ipucu : arastirma.ipuclari()) {
            System.out.println("\n> Bulunan Eşya: " + ipucu.isim());
            String islenmisMetin = ipucu.metin();

            if (ogrenilecekler.size() >= 3) {
                islenmisMetin = islenmisMetin.replace("{0}", motor.getSozlukYoneticisi().heceAnlami(ogrenilecekler.get(0)).toUpperCase());
                islenmisMetin = islenmisMetin.replace("{1}", motor.getSozlukYoneticisi().heceAnlami(ogrenilecekler.get(1)).toUpperCase());
                islenmisMetin = islenmisMetin.replace("{2}", motor.getSozlukYoneticisi().heceAnlami(ogrenilecekler.get(2)).toUpperCase());
            }
            System.out.println("\"" + islenmisMetin + "\"");
        }

        System.out.println("\n> Okudukların zihninde yankılanıyor... Eski dildeki karşılıklarını hatırlamaya başlıyorsun.");

        for (String hece : ogrenilecekler) {
            if (!oyuncu.getBilinenKelimeler().contains(hece)) {
                oyuncu.getBilinenKelimeler().add(hece);
                String anlam = motor.getSozlukYoneticisi().heceAnlami(hece);
                System.out.println("! Yeni bir Kadim Hece zihnine kazındı: " + hece + " (" + anlam + ")");
            }
        }

        oyuncu.olayTamamla(karsilasmaId + "_arastirma");
    }

    private void seyahatEt(String karsilasmaId) {
        Bolge mevcut = motor.getVeriYoneticisi().bolgeGetir(oyuncu.getMevcutKonum()).orElse(null);
        if (mevcut == null || mevcut.baglantilar().isEmpty()) {
            System.out.println("\n> Buradan gidebileceğin hiçbir yol yok.");
            return;
        }

        boolean bossYenildi = oyuncu.getTamamlananOlaylar().contains(karsilasmaId);

        System.out.println("\n[ BAĞLANTILI YOLLAR ]");
        List<String> yollar = mevcut.baglantilar();
        for (int i = 0; i < yollar.size(); i++) {
            String hedefId = yollar.get(i);
            Bolge hedef = motor.getVeriYoneticisi().bolgeGetir(hedefId).orElse(null);
            if (hedef != null) {
                String hedefKarsilasmaId = mevcutBolgeKarsilasmaIdGetir(hedefId);
                boolean hedefTemiz = oyuncu.getTamamlananOlaylar().contains(hedefKarsilasmaId);

                if (hedefId.equals("unutulmus_aile_evi")) {
                    System.out.println((i + 1) + " - " + hedef.isim() + " (Final)");
                } else if (hedefTemiz) {
                    System.out.println((i + 1) + " - " + hedef.isim() + " (Arındırıldı)");
                } else if (oyuncu.getKesfedilenBolgeler().contains(hedefId)) {
                    System.out.println((i + 1) + " - " + hedef.isim() + " (Keşfedildi)");
                } else {
                    System.out.println((i + 1) + " - Bilinmeyen Bölge (Sisli)");
                }
            }
        }
        System.out.println("0 - İptal");

        int secim = motor.getGirdiYoneticisi().sayiOku("Nereye gitmek istersin?");
        if (secim > 0 && secim <= yollar.size()) {
            String yeniKonumId = yollar.get(secim - 1);
            boolean kesfedildiMi = oyuncu.getKesfedilenBolgeler().contains(yeniKonumId);

            if (!kesfedildiMi && !bossYenildi) {
                String bossAdi = motor.getVeriYoneticisi().karsilasmaGetir(karsilasmaId).get().takmaAd();
                System.out.println("\n[!] SİS GEÇİT VERMİYOR: Buradaki varlığın (" + bossAdi + ") karanlık aurası ileri gitmeni engelliyor.");
                System.out.println("> (Önce bu bölgeyi arındırmalı veya keşfedilmiş eski bir bölgeye geri çekilmelisin.)");
                return;
            }

            if (yeniKonumId.equals("unutulmus_aile_evi")) {
                if (oyuncu.getKayipIsimParcalari().isEmpty() && oyuncu.getBilinenKelimeler().containsAll(oyuncu.getGercekAdinParcalari())) {
                    oyunuZaferleBitir();
                    return;
                } else {
                    System.out.println("\n[!] KAPI KİLİTLİ: Eski ahşap kapının üzerinde 5 adet boşluk var...");
                    System.out.println("> Zihnindeki heceleri yokluyorsun ama ruhunun tüm parçalarını henüz bulamadın.");
                    System.out.println("> (Gerçek Adını oluşturan tüm heceleri lügatinde toplayana kadar buraya giremezsin.)");
                    return;
                }
            }

            oyuncu.setMevcutKonum(yeniKonumId);
            oyuncu.kesifEkle(yeniKonumId);
            System.out.println("\n> Yola çıkıyorsun...");
            motor.durumDegistir(new KesifDurumu(motor, oyuncu));
        }
    }

    private void oyunuZaferleBitir() {
        System.out.println("\n==================================================");
        System.out.println("             UNUTULMUŞ AİLE EVİ                   ");
        System.out.println("==================================================");
        System.out.println("\n> Eski, çürümüş ahşap kapıya dokunuyorsun.");
        System.out.println("> Zihnindeki beş hece sırasıyla parlamaya başlıyor.");

        String gercekAdin = motor.getSozlukYoneticisi().turkceyeCevir(oyuncu.getGercekAdinParcalari());
        System.out.println("> \"" + String.join(" ", oyuncu.getGercekAdinParcalari()) + "\" diye fısıldıyorsun.");
        System.out.println("> " + gercekAdin + "...");

        System.out.println("\n> Kapı büyük bir gıcırtıyla açılıyor. İçerisi sıcak ve tanıdık.");
        System.out.println("> Yıllardır süren hiçlik sona eriyor. Ailenin hatıraları zihnine dolarken");
        System.out.println("> bu karanlık takımadaya veda ediyorsun.");

        System.out.println("\n==================================================");
        System.out.println("                    OYUN BİTTİ                    ");
        System.out.println("==================================================");

        motor.getKayitYoneticisi().sil(oyuncu.getIsim());

        motor.getGirdiYoneticisi().metinOku("\n[Ana Menüye dönmek için Enter'a bas]");
        motor.durumDegistir(new AnaMenuDurumu());
    }

    private void haritayiCiz() {
        System.out.println("\n=================[ TAKIMADA HARİTASI ]=================");
        System.out.println("                " + h("unutulmus_aile_evi"));
        System.out.println("                     |");
        System.out.println("  " + h("unutulmus_vadi") + "--" + h("kristal_zirve") + "--" + h("kizil_harabe"));
        System.out.println("       |             |             |");
        System.out.println(" " + h("batik_mezarlik") + "-" + h("sisli_orman") + "-" + h("yankilanan_magara"));
        System.out.println("       |             |             |");
        System.out.println("  " + h("firtina_koyu") + "--" + h("kul_limani") + "--" + h("kemik_colu"));
        System.out.println("=======================================================");
        System.out.println(" Harita Lejantı: [*] = Buradasın | [?] = Bilinmiyor");
    }

    private String h(String bolgeId) {
        if (oyuncu.getMevcutKonum().equals(bolgeId)) {
            return "[* BURADASIN *]";
        }
        if (oyuncu.getKesfedilenBolgeler().contains(bolgeId)) {
            String isim = motor.getVeriYoneticisi().bolgeGetir(bolgeId).get().isim();
            return "[" + isim + "]";
        }
        return "[? SİSLİ ?]";
    }

    private void zihneBak() {
        System.out.println("\n============== [ ZİHNİN VE ÇANTAN ] ==============");
        System.out.println("Ruhunun Gizli Parçaları (Gerçek Adın):");
        System.out.println("- Zihninin derinlerinde 5 heceli kadim bir yankı hissediyorsun...");
        System.out.println("- Ancak üzerine örtülen sis henüz kalkmış değil. Parçaları birleştirmelisin.");

        System.out.println("\nBildiğin Kadim Heceler:");
        for (String hece : oyuncu.getBilinenKelimeler()) {
            String anlam = motor.getSozlukYoneticisi().heceAnlami(hece);
            String tur = motor.getSozlukYoneticisi().heceTuru(hece);
            System.out.println(" * " + hece + " -> " + anlam + " [" + tur + "]");
        }
        System.out.println("==================================================");
    }

    private String mevcutBolgeKarsilasmaIdGetir(String bolgeId) {
        return switch (bolgeId) {
            case "kul_limani" -> "liman_tuccari";
            case "firtina_koyu" -> "bogulmus_kaptan";
            case "batik_mezarlik" -> "sessiz_bekci";
            case "kemik_colu" -> "col_hakimi";
            case "sisli_orman" -> "orman_ruhu";
            case "yankilanan_magara" -> "kor_yanki";
            case "unutulmus_vadi" -> "vadi_gozcusu";
            case "kizil_harabe" -> "harabe_bekcisi";
            case "kristal_zirve" -> "zirve_koruyucusu";
            case "unutulmus_aile_evi" -> "gecmisin_yankisi";
            default -> "";
        };
    }
}