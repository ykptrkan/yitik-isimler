package org.example.model;

import java.util.List;

public record Bolge(
        String id,
        String isim,
        int x,
        int y,
        String dosyaYolu,
        List<String> baglantilar
) {}