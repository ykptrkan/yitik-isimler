package org.example.durum;

import org.example.ana.OyunMotoru;
import org.example.model.Oyuncu;
import java.util.Optional;

public class AnaMenuDurumu implements OyunDurumu {

    @Override
    public void baslat() {
        System.out.println("\n==========================================");
        System.out.println("           YİTİK İSİMLER TAKIMADASI       ");
        System.out.println("==========================================");
    }

    @Override
    public void ciz() {
        System.out.println("\nKaranlığa adım atmadan önce ruhunu adlandır:");
        System.out.println("1 - Yeni Kayıt (Yeni Ruh)");
        System.out.println("2 - Giriş Yap (Hatırlanış)");
        System.out.println("3 - Kayıt Sil (Unutuluş)");
        System.out.println("0 - Çıkış (Hiçliğe Karış)");
    }

    @Override
    public void guncelle(OyunMotoru motor) {
        int secim = motor.getGirdiYoneticisi().sayiOku("Seçiminiz");

        switch (secim) {
            case 1 -> yeniKayitOlustur(motor);
            case 2 -> hesabaGirisYap(motor);
            case 3 -> kayitSil(motor);
            case 0 -> motor.durdur();
            default -> System.out.println("Bilinmeyen bir seçim yaptın. Tekrar dene.");
        }
    }

    private void yeniKayitOlustur(OyunMotoru motor) {
        String isim = motor.getGirdiYoneticisi().metinOku("Karakterinizin Adı");

        if (motor.getKayitYoneticisi().yukle(isim).isPresent()) {
            System.out.println("\n=> Bu isim zaten evrende yankılanıyor. Başka bir ad seç.");
            return;
        }

        String sifre = motor.getGirdiYoneticisi().metinOku("Şifreniz");
        String sifreHash = motor.getKayitYoneticisi().sifrele(sifre);

        Oyuncu yeniOyuncu = new Oyuncu(isim, sifreHash, motor.getSozlukYoneticisi());

        motor.getKayitYoneticisi().kaydet(yeniOyuncu);
        System.out.println("\n=> Ruhun deftere yazıldı. Evrene hoş geldin, " + isim + ".");

        motor.durumDegistir(new GirisHikayesiDurumu(yeniOyuncu));
    }

    private void hesabaGirisYap(OyunMotoru motor) {
        String isim = motor.getGirdiYoneticisi().metinOku("Karakterinizin Adı");
        String sifre = motor.getGirdiYoneticisi().metinOku("Şifreniz");

        Optional<Oyuncu> oyuncuOpt = motor.getKayitYoneticisi().yukle(isim);

        if (oyuncuOpt.isPresent()) {
            Oyuncu oyuncu = oyuncuOpt.get();
            String girilenHash = motor.getKayitYoneticisi().sifrele(sifre);

            if (oyuncu.sifreDogrula(girilenHash)) {
                System.out.println("\n=> Hatırlanış başarılı. Tekrar hoş geldin, " + isim + ".");
                motor.durumDegistir(new KesifDurumu(motor, oyuncu));
            } else {
                System.out.println("\n=> Yanlış şifre. Söylediğin isim yankı bulmadı.");
            }
        } else {
            System.out.println("\n=> Böyle bir ruh bulunamadı. Boşluğa seslendin.");
        }
    }

    private void kayitSil(OyunMotoru motor) {
        String isim = motor.getGirdiYoneticisi().metinOku("Silinecek Karakterin Adı");
        String sifre = motor.getGirdiYoneticisi().metinOku("Şifreniz");

        Optional<Oyuncu> oyuncuOpt = motor.getKayitYoneticisi().yukle(isim);

        if (oyuncuOpt.isPresent()) {
            Oyuncu oyuncu = oyuncuOpt.get();
            String girilenHash = motor.getKayitYoneticisi().sifrele(sifre);

            if (oyuncu.sifreDogrula(girilenHash)) {
                String onay = motor.getGirdiYoneticisi().metinOku("Bu ruhu tamamen silmek istediğine emin misin? (E/H)");
                if (onay.equalsIgnoreCase("E")) {
                    if (motor.getKayitYoneticisi().sil(isim)) {
                        System.out.println("\n=> " + isim + " adlı ruh tamamen silindi. Evren onu unuttu.");
                    } else {
                        System.out.println("\n=> Silme başarısız oldu.");
                    }
                } else {
                    System.out.println("\n=> Silme işlemi iptal edildi.");
                }
            } else {
                System.out.println("\n=> Yanlış şifre. Sadece gerçek sahibi bir ruhu silebilir.");
            }
        } else {
            System.out.println("\n=> Böyle bir ruh bulunamadı. Olmayan bir şeyi silemezsin.");
        }
    }
}