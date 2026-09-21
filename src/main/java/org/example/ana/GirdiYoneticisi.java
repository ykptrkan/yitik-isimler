package org.example.ana;

import java.util.Scanner;

public class GirdiYoneticisi {
    private final Scanner tarayici = new Scanner(System.in);

    public String metinOku(String mesaj) {
        System.out.print(mesaj + " > ");
        return tarayici.nextLine().trim();
    }

    public int sayiOku(String mesaj) {
        while (true) {
            try {
                System.out.print(mesaj + " > ");
                String girdi = tarayici.nextLine().trim();

                if (girdi.isEmpty()) {
                    continue;
                }

                return Integer.parseInt(girdi);
            } catch (NumberFormatException e) {
                System.out.println("[!] Hatalı fısıltı. Sadece rakam girmelisin.");
            }
        }
    }

    public int sinirliSayiOku(String mesaj, int min, int max) {
        while (true) {
            int secim = sayiOku(mesaj);
            if (secim >= min && secim <= max) {
                return secim;
            } else {
                System.out.println("[!] Sınırların dışına çıktın. Lütfen " + min + " ile " + max + " arasında bir seçenek gir.");
            }
        }
    }
}