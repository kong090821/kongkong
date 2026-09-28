$ErrorActionPreference = "Stop"

$sdkDir = "C:\Users\user\AppData\Local\Android\Sdk"
$buildToolsDir = "$sdkDir\build-tools\36.0.0"
$platformDir = "$sdkDir\platforms\android-37.0"
$androidJar = "$platformDir\android.jar"
$jbr = "C:\Program Files\Android\Android Studio\jbr"

$env:JAVA_HOME = $jbr
$env:PATH = "$jbr\bin;$env:PATH"

$aapt2 = "$buildToolsDir\aapt2.exe"
$d8 = "$buildToolsDir\d8.bat"
$zipalign = "$buildToolsDir\zipalign.exe"
$apksigner = "$buildToolsDir\apksigner.bat"
$javac = "$jbr\bin\javac.exe"

$buildDir = "build_temp"
if (Test-Path $buildDir) { Remove-Item $buildDir -Recurse -Force }
New-Item -ItemType Directory -Path "$buildDir\compiled_res", "$buildDir\gen", "$buildDir\classes", "$buildDir\dex", "dist" -Force | Out-Null

Write-Host "1. Compiling Android Resources..."
& $aapt2 compile --dir "android/app/src/main/res" -o "$buildDir/compiled_res.zip"

Write-Host "2. Linking Resources & Generating R.java (APK & Proto format)..."
& $aapt2 link -I $androidJar `
    --manifest "android/app/src/main/AndroidManifest.xml" `
    --java "$buildDir/gen" `
    -o "$buildDir/unaligned_res.apk" `
    -A "android/app/src/main/assets" `
    --auto-add-overlay `
    "$buildDir/compiled_res.zip"

& $aapt2 link -I $androidJar `
    --manifest "android/app/src/main/AndroidManifest.xml" `
    --proto-format `
    -o "$buildDir/base_proto.zip" `
    -A "android/app/src/main/assets" `
    --auto-add-overlay `
    "$buildDir/compiled_res.zip"

Write-Host "3. Compiling Java sources with javac (Java 17 target)..."
$javaFiles = @(
    "android/app/src/main/java/com/kongkong/maternitybag/MainActivity.java",
    "$buildDir/gen/com/kongkong/maternitybag/R.java"
)
& $javac -encoding UTF-8 -cp $androidJar -source 17 -target 17 -d "$buildDir/classes" $javaFiles

Write-Host "4. Dexing bytecodes with D8..."
$classFiles = Get-ChildItem "$buildDir/classes" -Recurse -Filter "*.class" | ForEach-Object { $_.FullName }
& $d8 $classFiles --lib $androidJar --output "$buildDir/dex"

Write-Host "5. Creating Final APK..."
Copy-Item "$buildDir/unaligned_res.apk" "$buildDir/app_with_dex.apk"
$jarTool = "$jbr\bin\jar.exe"
Push-Location "$buildDir/dex"
& $jarTool -uf "../app_with_dex.apk" "classes.dex"
Pop-Location

Write-Host "6. Zipalign APK..."
& $zipalign -f -p 4 "$buildDir/app_with_dex.apk" "dist/kongkong-release.apk"

Write-Host "7. Signing APK with release keystore..."
& $apksigner sign --ks "release-keystore.jks" --ks-pass "pass:kongkong1234!" --key-pass "pass:kongkong1234!" --out "dist/kongkong-release-signed.apk" "dist/kongkong-release.apk"

Write-Host "8. Assembling Base Module for Android App Bundle (AAB)..."
$aabDir = "$buildDir/aab_base"
New-Item -ItemType Directory -Path "$aabDir/base/dex", "$aabDir/base/manifest", "$aabDir/base/res", "$aabDir/base/assets" -Force | Out-Null

Copy-Item "$buildDir/dex/classes.dex" "$aabDir/base/dex/classes.dex"
Copy-Item -Path "android/app/src/main/assets/*" -Destination "$aabDir/base/assets" -Recurse -Force

# Extract base proto package into AAB base structure
Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::ExtractToDirectory("$buildDir/base_proto.zip", "$buildDir/extracted_proto")
if (Test-Path "$buildDir/extracted_proto/AndroidManifest.xml") {
    Copy-Item "$buildDir/extracted_proto/AndroidManifest.xml" "$aabDir/base/manifest/AndroidManifest.xml"
}
if (Test-Path "$buildDir/extracted_proto/resources.pb") {
    Copy-Item "$buildDir/extracted_proto/resources.pb" "$aabDir/base/resources.pb"
}
if (Test-Path "$buildDir/extracted_proto/res") {
    Copy-Item "$buildDir/extracted_proto/res/*" "$aabDir/base/res" -Recurse -Force
}

# Zip base module into Android App Bundle (.aab) with forward slashes
$aabOut = "dist/kongkong-release.aab"
if (Test-Path $aabOut) { Remove-Item $aabOut -Force }

$fullAabOut = (Resolve-Path "dist").Path + "\kongkong-release.aab"
$jarTool = "$jbr\bin\jar.exe"
Push-Location $aabDir
& $jarTool -c -M -f $fullAabOut base
Pop-Location

Write-Host "Signing AAB with release keystore (SHA256withRSA)..."
$jarsigner = "$jbr\bin\jarsigner.exe"
& $jarsigner -sigalg SHA256withRSA -digestalg SHA-256 -keystore "release-keystore.jks" -storepass "kongkong1234!" -keypass "kongkong1234!" $aabOut "kongkong"

Write-Host "SUCCESS! Both signed APK and AAB have been generated in dist/:"
Get-ChildItem "dist" | Select-Object Name, Length, LastWriteTime
