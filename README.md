# WanderList

Android aplikacija za organizaciju putnih želja, izrađena kao projekt za kolegij **Razvoj mobilnih aplikacija** (FERIT), 2026.

## O aplikaciji

WanderList rješava problem organizacije putnih želja — korisnik dodaje destinacije koje želi posjetiti, prati trenutnu vremensku prognozu za svaku od njih, i nakon posjeta ih premješta u popis posjećenih destinacija uz ocjenu i recenziju.

## Funkcionalnosti

- **Wishlist** — dodavanje/uređivanje/brisanje destinacija, automatsko geokodiranje (ime grada → koordinate), prikaz trenutne vremenske prognoze po destinaciji
- **Posjećeno** — ocjena (1-5 zvjezdica) i tekstualna recenzija, mogućnost vraćanja na wishlistu
- **Karta** — sve wishlist destinacije prikazane kao markeri na interaktivnoj OpenStreetMap karti
- **Iznenadi me** — nasumičan odabir destinacije, klikom na gumb ili tresenjem uređaja (akcelerometar)
- **Tjedna notifikacija** — pozadinska analiza petodnevne vremenske prognoze svih wishlist destinacija, prijedlog destinacije s najugodnijim vremenom
- **Prijava/registracija** — Firebase Authentication (email + lozinka), podaci vezani isključivo po korisniku (Firestore Security Rules)

## Tehnologije

Kotlin · Jetpack Compose · MVVM arhitektura · Firebase Firestore · Firebase Authentication · Retrofit · OpenWeather API (trenutna prognoza + 5-dnevna prognoza + geokodiranje) · OSMDroid · WorkManager · Compose Navigation · Kotlin Coroutines

## Pokretanje projekta

1. Klonirati repozitorij
2. Otvoriti u Android Studiju (min. SDK 26, target SDK 36)
3. Sync Gradle
4. Pokrenuti na emulatoru ili fizičkom uređaju

Napomena: `local.properties` (OpenWeather API ključ) i `google-services.json` (Firebase config) su uključeni u repozitorij radi jednostavnijeg pokretanja.

## Autor

Lucija Perić
