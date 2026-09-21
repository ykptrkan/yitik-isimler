# Yitik İsimler (True Name) — Terminal RPG

![Java](https://img.shields.io/badge/Java-25-orange?style=flat-square&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
![Gson](https://img.shields.io/badge/Serialization-Gson-4285F4?style=flat-square&logo=google&logoColor=white)
![Jackson](https://img.shields.io/badge/Data%20Binding-Jackson-2A9D8F?style=flat-square)
![JUnit](https://img.shields.io/badge/Tests-JUnit%204-25A162?style=flat-square&logo=junit5&logoColor=white)
![CLI](https://img.shields.io/badge/Interface-CLI-6E56CF?style=flat-square)

*Yitik İsimler*, Ursula K. Le Guin'in "Yerdeniz" evreninden ilham alan, her şeyin bir "Gerçek Adı" olduğu ve isimleri bilenin varoluşa hükmettiği, metin tabanlı (CLI) karanlık bir Rol Yapma Oyunudur (RPG).

Klasik "kılıç ve kalkan" mekaniklerini bir kenara bırakan bu oyun; oyuncuları **psikolojik diyaloglara, kelime bulmacalarına ve zorlu fedakârlıklara** davet eder. Kendi beş heceli gerçek adını unutan bir ruh olarak hiçlikten uyanır ve hafızanı geri kazanmak için Takımada'nın eski kralları ve karanlık varlıklarıyla yüzleşirsin.

---

## Temel Oyun Mekanikleri

- **Veri Odaklı (Data-Driven) Dünya:** Oyun içi haritalar, bölgeler, düşmanlar, diyalog ağaçları ve ipuçlarının tamamı JSON dosyaları üzerinden beslenir.
- **Dinamik Gerçek Ad Motoru:** Düşmanların ve oyuncunun adları statik değildir. Oyun, kendi içindeki sözlüğü (Kadim Dil) kullanarak `[Durum] + [Element] + [Unvan]` gramer kuralına göre benzersiz ve rastgele hecelerden oluşan mantıklı Türkçe isimler üretir (örn. *Kanlı Deniz Kaptanı*).
- **Dört Aşamalı Ritüel Savaşı:**
    1. **Araştırma:** Çevredeki ipuçlarını toplayıp düşmanın hecelerini keşfetme.
    2. **Diyalog:** Düşmanın kalkanını kırmak için psikolojik seçimler yapma (Kibir hasar verir, Sükûnet iyileştirir).
    3. **Heceleme:** Toplanan heceleri dilbilgisi kuralına göre zihinde doğru sırayla birleştirme.
    4. **Fedakârlık:** Düşmanın gücünü emmek için zihninden kendi bildiğin bir heceyi (gücü) feda edip sonsuzluğa bırakma.
- **Hatıralar Kuyusu ve Ceza Sistemi:** Karakterin İradesi (Canı) sıfırlanırsa ölmez, hafızası parçalanır. Envanterinden rastgele bir heceyi unutarak başlangıç noktasına savrulur. Unutulan bu parçalar "Hatıralar Kuyusu"nda birikir ve kamplarda Yankı (oyun içi para) harcanarak geri alınabilir.

---

## Mimari ve Tasarım Prensipleri

Bu proje, karmaşık RPG sistemlerini yönetilebilir kılmak amacıyla katı yazılım mühendisliği prensipleriyle (SOLID ve OOP) tasarlanmıştır.

- **State Pattern (Durum Tasarım Deseni):** Konsol ekranlarındaki menü geçişleri `OyunDurumu` arayüzü (interface) üzerinden yönetilir (`AnaMenuDurumu`, `KesifDurumu`, `RituelDurumu`). Spagetti if-else blokları yerine, her ekran kendi yaşam döngüsüne (lifecycle) sahiptir.
- **Single Responsibility Principle (SRP):** Sınıflar tek bir amaca hizmet eder.
    - `KayitYoneticisi`: Yalnızca oyun kayıt işlemlerinden sorumludur.
    - `GirdiYoneticisi`: Kullanıcı girişlerini valide eder ve harf girildiğinde oluşabilecek çökme (crash) hatalarını engeller.
    - `SozlukYoneticisi`: JSON veri tabanından heceleri çeker, grameri kurar ve Türkçe iyelik eki kurallarını (büyük ünlü uyumuna kadar) işler.
- **Tam Kapsülleme (Encapsulation):** Oyuncu verileri ve oyun matematiği (irade puanları, yankı ekonomisi, envanter yönetimi) tamamen private tutulmuş, limit kontrolleri setter metotlarıyla koruma altına alınmıştır.

---

## Kullanılan Teknolojiler ve Araçlar

Proje **Java 25** kullanılarak geliştirilmiştir. Dış bağımlılıkların yönetimi için **Maven** kullanılmıştır.

- **Gson (Google):** Oyuncu profilinin (`Oyuncu.java`) `kayitlar/` klasörüne JSON formatında dinamik olarak yazılıp okunması (serialization / deserialization) ve oyuncu şifrelerinin düz metin yerine **SHA-256 hash** algoritmasıyla güvenli bir şekilde saklanması için kullanılmıştır.
- **Jackson (Databind):** Katı yapıdaki sözlük ve harita JSON dosyalarının (diyalog ağaçları, bölgeler, ipuçları) dinamik node'lar hâlinde belleğe yüklenip ayrıştırılması için kullanılmıştır.
- **JUnit 4:** Çekirdek oyun döngüsünün, hasar mekaniğinin, gramer çevirilerinin ve ölüm/dirilme mantığının hatasız çalışmasını güvence altına alan otomatik test yazılımları (`YitikIsimlerKapsamliTest.java`) için kullanılmıştır.

---

## Kurulum ve Çalıştırma

1. Bağımlılıkları yükleyin ve derleyin:

   ```bash
   mvn clean install
   ```

2. Testleri çalıştırarak oyunun matematiksel motorunu doğrulayın (otomatik olarak `test_sonuclari.txt` dosyasını oluşturur):

   ```bash
   mvn clean test
   ```

3. Oyunu başlatın:

   ```bash
   mvn exec:java -Dexec.mainClass="org.example.App"
   ```

   *(Alternatif olarak IntelliJ IDEA veya Visual Studio Code üzerinden `App.java` dosyasını doğrudan çalıştırabilirsiniz.)*

---


**Geliştirici:** Yakup "ykptrkan" TÜRKAN