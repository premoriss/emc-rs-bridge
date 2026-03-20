# EMC RS Bridge (Minecraft 1.12.2 Forge)

Bu proje, **ProjectE** ve **Refined Storage** arasında köprü olan örnek bir addon iskeletidir.

## Yaptığı şey
- `EMC Interface` adlı bir blok ekler.
- Blok bir **owner** (sahip oyuncu) kaydeder.
- Oyuncu, elindeki eşyalarla bloğa sağ tıklayarak **template / ghost item** listesi oluşturur.
- Blok `IItemHandler` capability sunar; bu sayede **Refined Storage External Storage** ile ağa bağlanabilir.
- RS ağı bu bloktan bir item çektiğinde, blok owner oyuncunun ProjectE EMC havuzunu kontrol eder.
- Yeterli EMC varsa ProjectE item EMC değerini kullanarak eşyayı üretir ve EMC düşer.

## Kullanım
1. Modu workspace'e ekleyin.
2. Oyunda `EMC Interface` bloğunu yerleştirin.
3. İlk sağ tıklayan oyuncu owner olur.
4. Elinizde bir item varken **shift + sağ tık** yaparak o itemi template listesine ekleyin.
5. Bloğun yanına **Refined Storage External Storage** bağlayın.
6. RS ağı template itemleri stokta varmış gibi görecek.
7. Ağ item çekince addon EMC harcayarak itemi verecek.

## Notlar
- Bu sürüm örnek / geliştirilebilir iskelettir.
- ProjectE API çağrıları **reflection** ile yapıldı; 1.12.2 API farklarında ufak isim değişiklikleri için `ProjectEReflection.java` içini düzeltmeniz gerekebilir.
- GUI eklenmedi; template ekleme çıkarma sağ tık mantığıyla yapılıyor.
- RS tarafında en kararlı entegrasyon, bu bloğu `External Storage` ile bağlamaktır.

## Derleme
```bash
gradlew setupDecompWorkspace
gradlew build
```

Derlenmiş jar dosyası:
`build/libs/EMCRSBridge-1.0.0.jar`
