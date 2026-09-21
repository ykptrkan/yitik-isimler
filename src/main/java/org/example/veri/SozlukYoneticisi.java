package org.example.veri;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.*;

public class SozlukYoneticisi {
    private final Map<String, HeceBilgisi> sozluk = new HashMap<>();
    private final List<String> durumHeceleri = new ArrayList<>();
    private final List<String> elementHeceleri = new ArrayList<>();
    private final List<String> unvanHeceleri = new ArrayList<>();
    private final Random rastgele = new Random();

    public SozlukYoneticisi() {
        yukle();
    }

    private void yukle() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("sozluk.json")) {
            if (is != null) {
                JsonNode root = mapper.readTree(is);
                JsonNode hecelerNode = root.get("heceler");

                Iterator<Map.Entry<String, JsonNode>> fields = hecelerNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    String heceAd = field.getKey();
                    String anlam = field.getValue().get("anlam").asText();
                    String tur = field.getValue().get("tur").asText();

                    sozluk.put(heceAd, new HeceBilgisi(anlam, tur));

                    switch (tur) {
                        case "Durum" -> durumHeceleri.add(heceAd);
                        case "Element" -> elementHeceleri.add(heceAd);
                        case "Unvan" -> unvanHeceleri.add(heceAd);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Kadim Sözlük yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    public List<String> rastgeleIkiHeceliUret() {
        String durum = durumHeceleri.get(rastgele.nextInt(durumHeceleri.size()));
        String element = elementHeceleri.get(rastgele.nextInt(elementHeceleri.size()));
        return Arrays.asList(durum, element);
    }

    public String rastgeleUnvanSec() {
        return unvanHeceleri.get(rastgele.nextInt(unvanHeceleri.size()));
    }

    public String heceAnlami(String hece) {
        return sozluk.containsKey(hece) ? sozluk.get(hece).anlam() : "[Bilinmeyen]";
    }

    public String heceTuru(String hece) {
        return sozluk.containsKey(hece) ? sozluk.get(hece).tur() : "Bilinmeyen";
    }

    public String turkceyeCevir(List<String> heceler) {
        if (heceler == null || heceler.isEmpty()) return "";

        if (heceler.size() == 2) {
            return heceAnlami(heceler.get(0)) + " " + heceAnlami(heceler.get(1));

        } else if (heceler.size() == 3) {
            String durum = heceAnlami(heceler.get(0));
            String element = heceAnlami(heceler.get(1));
            String unvan = heceAnlami(heceler.get(2));
            return durum + " " + element + " " + iyelikEkiEkle(unvan);

        } else if (heceler.size() == 5) {
            String durum1 = heceAnlami(heceler.get(0));
            String element1 = heceAnlami(heceler.get(1));
            String durum2 = heceAnlami(heceler.get(2));
            String element2 = heceAnlami(heceler.get(3));
            String unvan = heceAnlami(heceler.get(4));

            return durum1 + " " + element1 + " ve " + durum2 + " " + element2 + " " + iyelikEkiEkle(unvan);
        }
        return String.join(" ", heceler);
    }

    private String iyelikEkiEkle(String kelime) {
        kelime = kelime.trim();
        char sonHarf = kelime.toLowerCase().charAt(kelime.length() - 1);
        boolean unluyleBitiyor = "aeıioöuü".indexOf(sonHarf) != -1;
        char sonSesli = 'e';
        for (int i = kelime.length() - 1; i >= 0; i--) {
            char c = kelime.toLowerCase().charAt(i);
            if ("aeıioöuü".indexOf(c) != -1) {
                sonSesli = c;
                break;
            }
        }

        String ek = "";
        if (unluyleBitiyor) {
            if (sonSesli == 'a' || sonSesli == 'ı') ek = "sı";
            else if (sonSesli == 'e' || sonSesli == 'i') ek = "si";
            else if (sonSesli == 'o' || sonSesli == 'u') ek = "su";
            else if (sonSesli == 'ö' || sonSesli == 'ü') ek = "sü";
        } else {
            if (sonSesli == 'a' || sonSesli == 'ı') ek = "ı";
            else if (sonSesli == 'e' || sonSesli == 'i') ek = "i";
            else if (sonSesli == 'o' || sonSesli == 'u') ek = "u";
            else if (sonSesli == 'ö' || sonSesli == 'ü') ek = "ü";
        }
        return kelime + ek;
    }
    public record HeceBilgisi(String anlam, String tur) {}
}