package org.example.durum;

import org.example.ana.OyunMotoru;

public interface OyunDurumu {
    void baslat();
    void ciz();
    void guncelle(OyunMotoru motor);
}