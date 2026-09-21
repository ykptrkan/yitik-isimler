package org.example.durum;

import org.example.ana.OyunMotoru;
import org.example.model.Karsilasma;
import org.example.model.Oyuncu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RituelDurumu implements OyunDurumu {
    private final Oyuncu oyuncu;
    private final String karsilasmaId;
    private Karsilasma dusmanData;
    private String mevcutAsama = "arastirma";
    private final List<String> toplananIpuclari = new ArrayList<>();
    private List<String> bossGercekAdiHeceleri;
    private String bossGercekAdiMetni;
    private String kazanilacakHece;

    public RituelDurumu(Oyuncu oyuncu, String karsilasmaId) {
        this.oyuncu = oyuncu;
        this.karsilasmaId = karsilasmaId;
    }

    @Override
    public void baslat() {
        System.out.println("\n==================================================");
        System.out.println("            RİTÜEL BAŞLIYOR...                    ");
        System.out.println("==================================================");
    }

    @Override
    public void ciz() {}

    @Override
    public void guncelle(OyunMotoru motor) {
        if (dusmanData == null) {
            Optional<Karsilasma> veriOpt = motor.getVeriYoneticisi().karsilasmaGetir(karsilasmaId);
            if (veriOpt.isEmpty()) {
                motor.durumDegistir(new KesifDurumu(motor, oyuncu));
                return;
            }
            dusmanData = veriOpt.get();
            bossIsminiOlustur(motor);
        }

        switch (mevcutAsama) {
            case "arastirma" -> arastirmaAsamasi(motor);
            case "diyalog" -> diyalogAsamasi(motor);
            case "heceleme" -> hecelemeAsamasi(motor);
            case "fedakarlik" -> fedakarlikAsamasi(motor);
            case "bitti" -> motor.durumDegistir(new KesifDurumu(motor, oyuncu));
        }
    }

    private void bossIsminiOlustur(OyunMotoru motor) {
        List<String> bolgeHeceleri = dusmanData.arastirma().ogrenilecekHeceler();
        bossGercekAdiHeceleri = new ArrayList<>();

        String bolgeDurumu = bolgeHeceleri.get(0);
        String bolgeElementi = bolgeHeceleri.get(1);
        String bolgeUnvani = bolgeHeceleri.get(2);

        List<String> oyuncuHeceleri = new ArrayList<>(oyuncu.getBilinenKelimeler());
        if (oyuncuHeceleri.isEmpty()) {
            oyuncuHeceleri.add(bolgeDurumu);
        }
        Collections.shuffle(oyuncuHeceleri);
        String oyuncudanGelen = oyuncuHeceleri.get(0);
        String oyuncuHeceTuru = motor.getSozlukYoneticisi().heceTuru(oyuncudanGelen);

        if (oyuncuHeceTuru.equals("Durum")) {
            bossGercekAdiHeceleri.add(oyuncudanGelen);
            bossGercekAdiHeceleri.add(bolgeElementi);
            bossGercekAdiHeceleri.add(bolgeUnvani);
        } else if (oyuncuHeceTuru.equals("Element")) {
            bossGercekAdiHeceleri.add(bolgeDurumu);
            bossGercekAdiHeceleri.add(oyuncudanGelen);
            bossGercekAdiHeceleri.add(bolgeUnvani);
        } else {
            bossGercekAdiHeceleri.add(bolgeDurumu);
            bossGercekAdiHeceleri.add(bolgeElementi);
            bossGercekAdiHeceleri.add(oyuncudanGelen);
        }

        if (!oyuncu.getKayipIsimParcalari().isEmpty()) {
            kazanilacakHece = oyuncu.getKayipIsimParcalari().get(0);
        } else {
            kazanilacakHece = motor.getSozlukYoneticisi().rastgeleUnvanSec();
        }

        bossGercekAdiMetni = motor.getSozlukYoneticisi().turkceyeCevir(bossGercekAdiHeceleri);
    }

    private void arastirmaAsamasi(OyunMotoru motor) {
        System.out.println("\n> Karşında: " + dusmanData.takmaAd());
        System.out.println("\n[ ARAŞTIRMA AŞAMASI ]");

        List<Karsilasma.Ipucu> ipuclari = dusmanData.arastirma().ipuclari();

        while(true) {
            System.out.println("\nDüşmanla yüzleşmeden önce zihnini toplamak için kısa bir vaktin var.");

            for (int i = 0; i < ipuclari.size(); i++) {
                Karsilasma.Ipucu ipucu = ipuclari.get(i);
                String durum = toplananIpuclari.contains(ipucu.id()) ? "[Okundu]" : "[Hatırla]";
                System.out.println((i + 1) + " - Düşün: " + ipucu.isim() + " " + durum);
            }
            System.out.println("0 - Yüzleşmeye Başla (Diyalog)");

            int secim = motor.getGirdiYoneticisi().sayiOku("Seçiminiz");

            if (secim == 0) {
                System.out.println("\n> Gölgelerden çıkıp " + dusmanData.takmaAd() + " ile yüzleşiyorsun...");
                mevcutAsama = "diyalog";
                break;
            } else if (secim > 0 && secim <= ipuclari.size()) {
                Karsilasma.Ipucu secilenIpucu = ipuclari.get(secim - 1);

                if (!toplananIpuclari.contains(secilenIpucu.id())) {
                    System.out.println("\n> [" + secilenIpucu.isim() + "] hatırasına yoğunlaştın.");

                    String islenmisMetin = secilenIpucu.metin();
                    List<String> ogrenilecekler = dusmanData.arastirma().ogrenilecekHeceler();
                    if (ogrenilecekler.size() >= 3) {
                        islenmisMetin = islenmisMetin.replace("{0}", motor.getSozlukYoneticisi().heceAnlami(ogrenilecekler.get(0)).toUpperCase());
                        islenmisMetin = islenmisMetin.replace("{1}", motor.getSozlukYoneticisi().heceAnlami(ogrenilecekler.get(1)).toUpperCase());
                        islenmisMetin = islenmisMetin.replace("{2}", motor.getSozlukYoneticisi().heceAnlami(ogrenilecekler.get(2)).toUpperCase());
                    }

                    System.out.println("> METİN: " + islenmisMetin);
                    toplananIpuclari.add(secilenIpucu.id());
                } else {
                    System.out.println("\n> Bu hatırayı zaten zihnine kazıdın.");
                }
            } else {
                System.out.println("\n> Yanlış bir yöne odaklandın.");
            }
        }
    }

    private void diyalogAsamasi(OyunMotoru motor) {
        Karsilasma.DiyalogAgaci agac = dusmanData.diyalogAgaci();
        String aktifDugumId = agac.baslangicDugumu();

        while (aktifDugumId != null) {
            var dugum = agac.dugumler().get(aktifDugumId);
            if (dugum == null) break;

            System.out.println("\n" + dusmanData.takmaAd() + ": \"" + dugum.npcMetni() + "\"");
            System.out.println("--------------------------------------------------");

            for (int i = 0; i < dugum.secenekler().size(); i++) {
                System.out.println((i + 1) + " - " + dugum.secenekler().get(i).metin());
            }

            int secim = motor.getGirdiYoneticisi().sayiOku("Cevabın") - 1;

            if (secim >= 0 && secim < dugum.secenekler().size()) {
                var secilen = dugum.secenekler().get(secim);

                if (secilen.yozlasmaCarpani() > 0) {
                    System.out.println("\n[!] Kibir/Öfke zihnini yaraladı. (-" + secilen.yozlasmaCarpani() + " İrade)");
                    oyuncu.iradeDegistir(-secilen.yozlasmaCarpani());
                } else if (secilen.yozlasmaCarpani() < 0) {
                    int sifa = Math.abs(secilen.yozlasmaCarpani());
                    System.out.println("\n[+] Sükunet kalkanı zayıflattı. (+" + sifa + " İrade)");
                    oyuncu.iradeDegistir(sifa);
                }

                if (oyuncu.getIrade() <= 0) {
                    System.out.println("\n> İraden tamamen tükendi. Karanlık zihnini ele geçiriyor...");
                    yenilgiKontrolu(motor);
                    return;
                }

                if (secilen.tetiklenecekAsama() != null) {
                    if (secilen.tetiklenecekAsama().equals("heceleme")) {
                        mevcutAsama = "heceleme";
                    } else if (secilen.tetiklenecekAsama().equals("savas_hasari")) {
                        System.out.println("\n> Zihnine aldığın şiddetli darbe ritüeli bozdu! Yaralı bir şekilde geri çekilmek zorunda kaldın.");
                        mevcutAsama = "bitti";
                    }
                    return;
                }
                aktifDugumId = secilen.sonrakiDugum();
            } else {
                System.out.println("Hatalı seçim.");
            }
        }
    }

    private void hecelemeAsamasi(OyunMotoru motor) {
        System.out.println("\n> Düşmanın sahte ismi parçalanıyor!");

        boolean tumIpuclariBulundu = toplananIpuclari.size() == dusmanData.arastirma().ipuclari().size();

        if (tumIpuclariBulundu) {
            System.out.println("> Metinleri dikkatle okuduğun için ipuçları zihninde birleşiyor...");
            System.out.println("> Zihninde yankılanan isim: " + bossGercekAdiMetni);
        } else {
            System.out.println("> [!] Etrafı yeterince araştırmadın! Heceler bulanık...");
        }

        System.out.println("\n============== [ ZİHNİNDEKİ HECELER ] ==============");
        for (String hece : oyuncu.getBilinenKelimeler()) {
            String anlam = motor.getSozlukYoneticisi().heceAnlami(hece);
            String tur = motor.getSozlukYoneticisi().heceTuru(hece);
            System.out.println(" * " + hece + " -> " + anlam + " [" + tur + "]");
        }
        System.out.println("====================================================");

        System.out.println("\n> Hatırladığın kadim kelimeleri aralarında boşluk bırakarak doğru sırayla gir:");
        String tahmin = motor.getGirdiYoneticisi().metinOku("Birleştir");

        boolean dogruMu = true;
        String[] tahminHeceler = tahmin.split(" ");

        if (tahminHeceler.length != bossGercekAdiHeceleri.size()) {
            dogruMu = false;
        } else {
            for (String t : tahminHeceler) {
                if (!bossGercekAdiHeceleri.contains(t)) {
                    dogruMu = false;
                }
            }
        }

        if (dogruMu) {
            System.out.println("\n> DOĞRU! Evren sarsılıyor...");
            mevcutAsama = "fedakarlik";
        } else {
            System.out.println("\n> Yanlış hece dizilimi! İsim seni reddetti. (-5 İrade)");
            oyuncu.iradeDegistir(-5);

            if (oyuncu.getIrade() <= 0) {
                System.out.println("\n> İraden tamamen tükendi. Karanlık zihnini ele geçiriyor...");
                yenilgiKontrolu(motor);
            } else {
                mevcutAsama = "bitti";
            }
        }
    }

    private void fedakarlikAsamasi(OyunMotoru motor) {
        System.out.println("\n> " + dusmanData.fedakarlik().hikayeMetni());

        if (oyuncu.getBilinenKelimeler().isEmpty()) {
            oyunuBitir(motor, "> Zihninde feda edebileceğin hiçbir kelime kalmadı... Bedel ödenemedi.");
            return;
        }

        System.out.println("\n[ Feda Edilecek Heceyi Seç ]");
        for (int i = 0; i < oyuncu.getBilinenKelimeler().size(); i++) {
            String h = oyuncu.getBilinenKelimeler().get(i);
            String anlam = motor.getSozlukYoneticisi().heceAnlami(h);
            System.out.println((i + 1) + " - " + h + " (" + anlam + ")");
        }

        int secim = motor.getGirdiYoneticisi().sayiOku("Silinecek hece") - 1;

        if (secim >= 0 && secim < oyuncu.getBilinenKelimeler().size()) {
            String silinenKelime = oyuncu.getBilinenKelimeler().remove(secim);

            if (oyuncu.getGercekAdinParcalari().contains(silinenKelime) && !oyuncu.getKayipIsimParcalari().contains(silinenKelime)) {
                oyuncu.getKayipIsimParcalari().add(silinenKelime);
            }

            System.out.println("\n> \"" + silinenKelime + "\" hecesini zihninden söküp attın.");

            oyuncu.getKayipIsimParcalari().remove(kazanilacakHece);

            if (!oyuncu.getBilinenKelimeler().contains(kazanilacakHece)) {
                oyuncu.getBilinenKelimeler().add(kazanilacakHece);
                System.out.println("> Ruhunun eksik bir parçası karanlığın içinden koptu ve sana geri döndü!");
                System.out.println("> [YENİ GÜÇ KAZANILDI]: " + kazanilacakHece + " (" + motor.getSozlukYoneticisi().heceAnlami(kazanilacakHece) + ")");
            }

            System.out.println("\n> [+15 Yankı] Düşmanın yankısı ruhuna işledi.");
            oyuncu.yankiDegistir(15);

            oyuncu.olayTamamla(karsilasmaId);

            motor.getKayitYoneticisi().kaydet(oyuncu);
            motor.getGirdiYoneticisi().metinOku("\n[Devam etmek için Enter'a bas]");
            mevcutAsama = "bitti";
        } else {
            System.out.println("Hatalı seçim.");
        }
    }

    private void oyunuBitir(OyunMotoru motor, String mesaj) {
        System.out.println(mesaj);
        System.out.println("========== OYUN BİTTİ ==========");
        motor.durdur();
    }

    private void yenilgiKontrolu(OyunMotoru motor) {
        if (oyuncu.ruhuParcalanarakUyan()) {
            motor.getKayitYoneticisi().kaydet(oyuncu);
            System.out.println("> Başlangıç noktasına savruldun. Acı içinde, soğuk taşların üzerinde gözlerini açtın.");
            oyuncu.setMevcutKonum("kul_limani");
            motor.getGirdiYoneticisi().metinOku("\n[Devam etmek için Enter'a bas]");
            motor.durumDegistir(new KesifDurumu(motor, oyuncu));
        } else {
            System.out.println("\n> Zihninde feda edebileceğin, seni koruyacak hiçbir kelime kalmadı.");
            System.out.println("> Sessizlik seni tamamen yuttu...");
            System.out.println("========== OYUN BİTTİ ==========");
            motor.durdur();
        }
    }
}