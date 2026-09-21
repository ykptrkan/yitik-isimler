package org.example.model;

import java.util.List;

public record BolgeIcerigi(
        String bolgeId,
        List<Karsilasma> karsilasmalar
) {}