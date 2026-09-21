package org.example.model;

import java.util.List;
import java.util.Map;

public record Karsilasma(
        String id,
        String takmaAd,
        String aciklama,
        Arastirma arastirma,
        DiyalogAgaci diyalogAgaci,
        Fedakarlik fedakarlik)
{


    public record Arastirma(
            List<String> ogrenilecekHeceler,
            List<Ipucu> ipuclari
    ) {}

    public record Ipucu(
            String id,
            String isim,
            String metin
    ) {}

    public record DiyalogAgaci(
            String baslangicDugumu,
            Map<String, Dugum> dugumler
    ) {}

    public record Dugum(
            String npcMetni,
            List<Secenek> secenekler
    ) {}

    public record Secenek(
            String metin,
            int yozlasmaCarpani,
            String sonrakiDugum,
            String tetiklenecekAsama
    ) {}

    public record Fedakarlik(
            String hikayeMetni
    ) {}
}