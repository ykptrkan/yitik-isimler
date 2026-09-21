package org.example;

import org.example.ana.OyunMotoru;
import org.example.durum.AnaMenuDurumu;

public class App {
    public static void main(String[] args) {
        OyunMotoru motor = new OyunMotoru(new AnaMenuDurumu());
        motor.calistir();
    }
}