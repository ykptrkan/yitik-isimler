package org.example.durum;

import org.example.ana.OyunMotoru;
import org.example.model.Oyuncu;
import java.io.IOException;

public class GirisHikayesiDurumu implements OyunDurumu {
    private final Oyuncu oyuncu;

    public GirisHikayesiDurumu(Oyuncu oyuncu) {
        this.oyuncu = oyuncu;
    }

    @Override
    public void baslat() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    @Override
    public void ciz() {
        System.out.println("\n[Hikayeyi atlamak için Enter'a basabilirsiniz]\n");

        String hikaye = """
            Her varlığın evrende bir 'Gerçek Adı' vardır.
            Rüzgarın, denizin, ateşin ve gölgelerin...
            Gerçek adını bilen, varoluşa hükmeder.
            
            Sen, sıradan isimlerin ötesinde, 
            ailesi tarafından 5 heceli devasa bir isimle mühürlenmiş biriydin.
            Ancak bu yük senin hafızanı ve varlığını parçaladı.
            
            Şimdi, Yitik İsimler Takımadası'nın soğuk taşlarında uyandın.
            Ruhunun 3 parçasını biliyorsun, 2'si ise sonsuza dek kayıp...
            
            Karanlığa adım at. İpuçlarını topla. 
            Eski kralların, boğulmuş kaptanların ve kör yankıların gerçek adlarını çalarak kendi parçalarını geri al.
            Aksi takdirde, bu hiçlik seni yutacak.
            """;

        daktiloGibiYazdir(hikaye, 30);
    }

    @Override
    public void guncelle(OyunMotoru motor) {
        System.out.println("\n[Uyanmak için Enter'a bas]");
        motor.getGirdiYoneticisi().metinOku(""); // Boş input bekler
        motor.durumDegistir(new KesifDurumu(motor, oyuncu));
    }

    private void daktiloGibiYazdir(String metin, int gecikme) {
        try {
            for (char harf : metin.toCharArray()) {
                if (System.in.available() > 0) {
                    System.in.read(new byte[System.in.available()]);
                    System.out.print(metin.substring(metin.indexOf(harf)));
                    break;
                }
                System.out.print(harf);
                Thread.sleep(gecikme);
            }
        } catch (InterruptedException | IOException e) {
            System.out.println(metin);
            Thread.currentThread().interrupt();
        }
    }
}