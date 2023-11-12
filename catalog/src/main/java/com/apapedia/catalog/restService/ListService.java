package com.apapedia.catalog.restService;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ListService {
    public List<String> getCategoryList() {
        return new ArrayList<>(List.of("Aksesoris Fashion", "Buku & Alat Tulis", "Elektronik",
                "Fashion Bayi & Anak", "Fashion Muslim", "Fotografi", "Hobi & Koleksi",
                "Jam Tangan", "Perawatan & Kecantikan", "Makanan & Minuman", "Otomotif",
                "Perlengkapan Rumah", "Souvenir & Party Supplies"));
    }
}
