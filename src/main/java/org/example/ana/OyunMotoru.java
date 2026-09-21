package org.example.ana;

import org.example.durum.OyunDurumu;
import org.example.veri.KayitYoneticisi;
import org.example.veri.SozlukYoneticisi;
import org.example.veri.VeriYoneticisi;

public class OyunMotoru {

    private OyunDurumu mevcutDurum;
    private boolean calisiyor = true;
    private final GirdiYoneticisi girdiYoneticisi = new GirdiYoneticisi();
    private final KayitYoneticisi kayitYoneticisi = new KayitYoneticisi();
    private final VeriYoneticisi veriYoneticisi = new VeriYoneticisi();
    private final SozlukYoneticisi sozlukYoneticisi = new SozlukYoneticisi();

    public OyunMotoru(OyunDurumu baslangicDurumu) {
        this.mevcutDurum = baslangicDurumu;
    }

    public void durumDegistir(OyunDurumu yeniDurum) {
        this.mevcutDurum = yeniDurum;
        this.mevcutDurum.baslat();
    }

    public void durdur() {
        this.calisiyor = false;
    }

    public void calistir() {
        mevcutDurum.baslat();
        while (calisiyor) {
            mevcutDurum.ciz();
            mevcutDurum.guncelle(this);
        }
        System.out.println("\nSessizlik evrene tamamen hakim oldu. Oyun kapandı.");
    }

    public GirdiYoneticisi getGirdiYoneticisi() { return girdiYoneticisi; }
    public KayitYoneticisi getKayitYoneticisi() { return kayitYoneticisi; }
    public VeriYoneticisi getVeriYoneticisi() { return veriYoneticisi; }
    public SozlukYoneticisi getSozlukYoneticisi() { return sozlukYoneticisi; }
}