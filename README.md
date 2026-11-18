# مسبحة — عدّاد تسبيح

تطبيق أندرويد صغير وخفيف (أقل من 100 كيلوبايت) لعدّ التسبيح، بدون أي مكتبات خارجية
وبدون Gradle: يُبنى بأدوات Android SDK المباشرة (`aapt2` + `javac` + `d8` + `apksigner`).

## المزايا

- عدّاد كبير بلمسة واحدة، مع اهتزاز خفيف عند كل تسبيحة.
- الهدف 33 لكل دورة، وشريط تقدّم، وعدّاد للدورات المكتملة.
- خمسة أذكار تتبدّل بزر «الذكر التالي».
- زر تصفير، وحفظ الحالة تلقائيًا عند إغلاق التطبيق.
- واجهة داكنة عربية (RTL) مبنية بالكود.

## البنية

```
AndroidManifest.xml            بيان التطبيق (package: com.buddyapps.tasbih)
app.properties                 versionCode / versionName / minSdk
java/com/buddyapps/tasbih/     كود التطبيق (Java 8، بلا AndroidX)
res/                           الاسم والأيقونة (vector)
scripts/build-apk.sh           سكربت البناء الكامل
.github/workflows/build.yml    سير عمل GitHub Actions
```

## البناء

### على GitHub Actions

كل دفعة إلى `main` (أو تشغيل يدوي من تبويب Actions) تبني APK موقّعًا وترفعه
كـ Artifact باسم `tasbih-apk`. ووسم `v*` يرفقه أيضًا بإصدار Release.

### محليًا

يحتاج JDK 17 و Android SDK (build-tools + platform) و Python 3:

```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$ANDROID_HOME/build-tools/36.0.0:$PATH
bash scripts/build-apk.sh .
# الناتج: build/app.apk
```

مفتاح التوقيع يُنشأ تلقائيًا مرة واحدة في `~/.buddy/keys` (أو المجلد الذي يحدده `KEYDIR`).
لأن كل تشغيل في CI ينشئ مفتاحًا جديدًا، ثبّت إصدارًا واحدًا؛ وللترقيات المستمرة
استخدم مفتاحًا ثابتًا عبر GitHub Secrets.

## التثبيت

نزّل `app.apk` من صفحة Actions، ثم افتحه على الهاتف واسمح بالتثبيت من هذا المصدر.
