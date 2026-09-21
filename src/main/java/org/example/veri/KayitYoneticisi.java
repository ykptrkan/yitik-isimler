package org.example.veri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.model.Oyuncu;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class KayitYoneticisi {
    private static final String KAYIT_KLASORU = "kayitlar";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public KayitYoneticisi() {
        try {
            Files.createDirectories(Paths.get(KAYIT_KLASORU));
        } catch (IOException e) {
            System.err.println("Kayıt klasörü oluşturulamadı.");
        }
    }

    public String sifrele(String sifre) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(sifre.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Şifreleme algoritması bulunamadı", e);
        }
    }

    public void kaydet(Oyuncu oyuncu) {
        try (Writer yazar = new FileWriter(KAYIT_KLASORU + "/" + oyuncu.getIsim() + ".json")) {
            gson.toJson(oyuncu, yazar);
        } catch (IOException e) {
            System.out.println("Kayıt hatası: " + e.getMessage());
        }
    }

    public Optional<Oyuncu> yukle(String isim) {
        Path path = Paths.get(KAYIT_KLASORU + "/" + isim + ".json");

        if (!Files.exists(path)) {
            return Optional.empty();
        }

        try (Reader okuyucu = new FileReader(path.toFile())) {
            Oyuncu oyuncu = gson.fromJson(okuyucu, Oyuncu.class);
            return Optional.ofNullable(oyuncu);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public boolean sil(String isim) {
        Path path = Paths.get(KAYIT_KLASORU + "/" + isim + ".json");
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            System.out.println("Kayıt silinirken bir engel çıktı: " + e.getMessage());
            return false;
        }
    }
}