# Media Flash — Android TV USB Media Player

<div align="center">

**برنامه‌ی اندروید TV برای مرور و پخش فایل‌ها از فلش USB**
<br/>
**Created By Mohammad Shaerpour**

</div>

---

## معرفی

**Media Flash** یک اپلیکیشن اندروید TV (Android TV) است که با اتصال فلش USB به تلویزیون، فایل‌های ویدیو، آهنگ و عکس شما رو با یک رابط کاربری زیبا و تم دارک پخش می‌کنه. ساختار برنامه شبیه File Explorer هست که هر نوع فایل با آیکون مخصوص خودش نشون داده می‌شه و با کنترل تلویزیون (D-pad) کامل قابل ناوبری هست.

### امکانات اصلی

- **انتخاب فلش USB** — وقتی چند فلش وصل باشه، هر کدوم با ظرفیت و فضای آزادش نمایش داده می‌شه
- **مرورگر فایل استایل‌دار** — پوشه‌ها با آیکون پوشه، عکس‌ها با تصویر کوچک (thumbnail)، ویدیوها با علامت پخش روی تصویر و آهنگ‌ها با آیکون موسیقی
- **پخش ویدیو** با ExoPlayer (پشتیبانی از MP4, MKV, AVI, MOV, WEBM و ...)
- **پخش آهنگ** با کنترل کامل (قبلی، بعدی، جلو، عقب، مکث)
- **نمایش عکس** با ناوبری بعدی/قبلی از همون پوشه
- **علاقه‌مندی‌ها** — هر فایلی رو می‌تونی به علاقه‌مندی‌ها اضافه کنی
- **اخیراً پخش‌شده** — تاریخچه پخش‌های اخیر
- **جستجو** — جستجوی فایل‌ها داخل فلش
- **نمایش اطلاعات فایل** — نام، مسیر، حجم، تاریخ و نوع
- **دو زبانه** — فارسی و انگلیسی از داخل تنظیمات قابل تغییر
- **فوتر ثابت** — «Created By Mohammad Shaerpour» در پایین همه‌ی صفحات

---

## مشخصات فنی

| آیتم | مقدار |
| --- | --- |
| `applicationId` | `com.mshaerpour.mediaflash` |
| `minSdk` | 21 (Android 5.0) |
| `targetSdk` | 34 (Android 14) |
| زبان کد | Kotlin |
| UI Framework | Jetpack Compose + androidx.tv |
| Player | androidx.media3 (ExoPlayer) |
| Image loading | Coil |
| Persistence | AndroidX DataStore |
| دسترسی USB | SAF (Storage Access Framework) + DocumentFile |

---

## نحوه‌ی Build و خروجی APK

این پروژه رو می‌تونی به دو روش build کنی:

### روش ۱ — خودکار با GitHub Actions (ساده‌ترین)

1. یک حساب GitHub بساز (اگه نداری).
2. کل این پوشه (`MediaFlashTV`) رو به یک repository جدید آپلود کن. می‌تونی با Git این کار رو بکنی:

   ```bash
   cd MediaFlashTV
   git init
   git add .
   git commit -m "Initial commit: Media Flash Android TV app"
   git branch -M main
   git remote add origin https://github.com/<your-username>/media-flash-tv.git
   git push -u origin main
   ```

3. به تب **Actions** در ریپازیتوری برو. کم‌کم workflow به نام `Build APK` اجرا می‌شه.
4. وقتی اجرا تموم شد، روی اون اجرا کلیک کن و در پایین صفحه بخش **Artifacts** یک فایل به نام `media-flash-apk` برای دانلود وجود داره.
5. اگه بخوای نسخه‌ی Release هم داشته باشی، یک tag بساز (مثلاً `v1.0.0`) و push کن:

   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

   بعد از اتمام build، یک GitHub Release خودکار ساخته می‌شه و APK بهش پیوست می‌شه.

### روش ۲ — Build محلی روی کامپیوتر

#### پیش‌نیازها
- **JDK 17** (یا بالاتر)
- **Android SDK** با `platform-tools` و `build-tools;34.0.0` و `platforms;android-34`
- **Gradle 8.7** (یا اجرای `gradle wrapper` داخل پوشه)

#### مراحل

```bash
cd MediaFlashTV

# اگر gradle-wrapper.jar وجود نداشت، با gradle نصب‌شده بسازش
gradle wrapper --gradle-version 8.7 --distribution-type bin
chmod +x ./gradlew

# Build نسخه‌ی Debug
./gradlew assembleDebug

# Build نسخه‌ی Release (به keystore نیاز داره - پایین‌تر ببین)
./gradlew assembleRelease
```

APK ساخته‌شده در این مسیر قرار می‌گیره:

```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release-unsigned.apk
```

#### نصب روی تلویزیون

```bash
# با ADB نصب کن (تلویزیون باید در حالت Developer Mode و USB Debugging باشه)
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## ساخت نسخه‌ی امضاشده (Release Signing)

برای build release با امضای واقعی، فایل `app/build.gradle.kts` رو باز کن و `signingConfigs` اضافه کن:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("<path-to>/your-key.jks")
            storePassword = "..."
            keyAlias = "..."
            keyPassword = "..."
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            ...
        }
    }
}
```

برای ساخت keystore:

```bash
keytool -genkey -v -keystore your-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias mediaflash
```

برای استفاده در GitHub Actions (بدون لو رفتن پسورد در ریپازیتوری)، از **Secrets** استفاده کن:

1. در ریپازیتوری به `Settings → Secrets and variables → Actions` برو.
2. این‌ها رو اضافه کن: `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
3. در workflow، اون‌ها رو decode کن و در `signing.properties` بریز، یا فایل `keystore.properties` بساز.

---

## ساختار پروژه

```
MediaFlashTV/
├── .github/workflows/build-apk.yml     ← GitHub Actions (APK خودکار)
├── app/
│   ├── build.gradle.kts                ← تنظیمات module
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/mshaerpour/mediaflash/
│       │   ├── MediaFlashApp.kt        ← Application class
│       │   ├── MainActivity.kt          ← Activity اصلی
│       │   ├── data/
│       │   │   ├── UsbDrive.kt
│       │   │   ├── UsbStorageManager.kt ← تشخیص فلش USB
│       │   │   ├── MediaFile.kt
│       │   │   ├── FileRepository.kt   ← مرورگر فایل
│       │   │   └── SettingsStore.kt    ← ذخیره‌سازی تنظیمات/علاقه‌مندی
│       │   ├── ui/
│       │   │   ├── theme/              ← تم دارک + رنگ سبز زمردی
│       │   │   ├── components/         ← فوتر، سایدبار، FileCard
│       │   │   ├── screens/            ← Home, Browser, Players, Settings
│       │   │   └── navigation/         ← NavHost
│       │   └── util/
│       │       ├── FileUtils.kt        ← دسته‌بندی فایل‌ها
│       │       └── LocaleManager.kt
│       └── res/
│           ├── drawable/               ← آیکون‌ها و آیکون برنامه
│           ├── mipmap-anydpi-v26/      ← آیکون لانچر تطبیقی
│           ├── values/                 ← استرینگ‌های انگلیسی
│           ├── values-fa/              ← استرینگ‌های فارسی
│           └── xml/                    ← تنظیمات backup و USB filter
├── gradle/libs.versions.toml           ← Version catalog
├── settings.gradle.kts
├── build.gradle.kts
└── README.md
```

---

## نکات مهم

### دسترسی به فلش در Android 11+
از Android 11 به بعد، Google اسکوپد استوریج (Scoped Storage) رو محدود کرده. این برنامه از **Storage Access Framework (SAF)** استفاده می‌کنه؛ بنابراین وقتی کاربر اولین فلش رو باز می‌کنه، Android یک دیالوگ permission نشون می‌ده که باید تایید بشه تا برنامه بتونه فایل‌ها رو بخونه. این دیالوگ از طریق `ACTION_OPEN_DOCUMENT_TREE` باز می‌شه.

در تلویزیون‌های قدیمی‌تر (Android 9 و پایین‌تر) که USB Mass Storage به صورت فایل‌سیستم سنتی mount می‌شه، برنامه از طریق `StorageManager` و مسیرهای مستقیم هم کار می‌کنه.

### تفاوت Build در محیط‌های مختلف
- **روی تلویزیون با اندروید 9 و بالاتر**: بدون مشکل کار می‌کنه
- **روی اندروید 11 به بالا**: در اولین اجرا کاربر باید دسترسی SAF بده
- **روی اندروید 5 تا 8**: ممکنه برای خواندن فلش به `READ_EXTERNAL_STORAGE` هم نیاز باشه (در manifest گنجونده شده)

### سازگاری با کنترل تلویزیون (D-pad)
همه‌ی المان‌های قابل focus شدن با دکمه‌های جهت‌نمای کنترل تلویزیون قابل پیمایش هستن. دکمه‌ی Center (OK) برای انتخاب و دکمه‌ی Back برای بازگشت استفاده می‌شه.

---

## راهنمای توسعه

اگه خواستی قابلیت جدید اضافه کنی:

1. **پلیر پیشرفته‌تر**: صفحه‌ی `VideoPlayerScreen.kt` رو بسط بده. ExoPlayer از subtitle، سرعت پخش و audio track selection پشتیبانی می‌کنه.
2. **Metadata آهنگ**: از `MediaMetadataRetriever` برای خوندن تگ‌های ID3 و نمایش نام آهنگ/آلبوم/هنرمند استفاده کن.
3. **Thumbnail ویدیو با Coil Video**: وابستگی `coil-video` در `libs.versions.toml` اضافه شده؛ از `VideoFrameUri` استفاده کن تا frame اول ویدیو به‌عنوان thumbnail نشون داده بشه.
4. **پخش پیوسته (Auto-play next)**: در `VideoPlayerScreen.kt` listener `Player.Listener` اضافه کن تا در `onPlaybackStateChanged` فایل بعدی همون پوشه رو پخش کنه.
5. **زیرنویس**: در ExoPlayer با `MediaItem.Builder().setSubtitleConfigurations(...)` زیرنویس رو اضافه کن.

---

## مجوز

این پروژه به‌صورت آزاد برای استفاده و توسعه‌ی شخصی منتشر شده.

**Credit**: `Created By Mohammad Shaerpour`

---

اگه سوالی داشتی یا خواستی قابلیتی اضافه کنی، خوشحال می‌شم کمک کنم.
