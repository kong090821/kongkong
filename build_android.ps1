$userSdk = "$env:LOCALAPPDATA\Android\Sdk"
if (Test-Path $userSdk) {
    $sdkDir = $userSdk
} else {
    $sdkDir = "C:\Users\user\AppData\Local\Android\Sdk"
}
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

Write-Host "0. Syncing root index.html & master_data.js to assets..."
Copy-Item "index.html" "android/app/src/main/assets/index.html" -Force
Copy-Item "index.html" "preview/index.html" -Force
if (Test-Path "master_data.js") {
    Copy-Item "master_data.js" "android/app/src/main/assets/master_data.js" -Force
    Copy-Item "master_data.js" "preview/master_data.js" -Force
}

Write-Host "1. Compiling Android Resources..."
& $aapt2 compile --dir "android/app/src/main/res" -o "$buildDir/compiled_res.zip"

Write-Host "2. Linking Resources & Generating R.java (APK & Proto format)..."
& $aapt2 link -I $androidJar `
    --manifest "android/app/src/main/AndroidManifest.xml" `
    --min-sdk-version 24 `
    --target-sdk-version 36 `
    --version-code 19 `
    --version-name "1.1.9" `
    --java "$buildDir/gen" `
    -o "$buildDir/unaligned_res.apk" `
    -A "android/app/src/main/assets" `
    --auto-add-overlay `
    "$buildDir/compiled_res.zip"

& $aapt2 link -I $androidJar `
    --manifest "android/app/src/main/AndroidManifest.xml" `
    --min-sdk-version 24 `
    --target-sdk-version 36 `
    --version-code 19 `
    --version-name "1.1.9" `
    --proto-format `
    -o "$buildDir/base_proto.zip" `
    -A "android/app/src/main/assets" `
    --auto-add-overlay `
    "$buildDir/compiled_res.zip"

Write-Host "3. Compiling Java sources with javac (Java 17 target)..."
$javaFiles = @(
    "android/app/src/main/java/com/kongkong/babybag/MainActivity.java",
    "$buildDir/gen/com/kongkong/babybag/R.java"
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
$baseModuleDir = "$buildDir/base_module"
New-Item -ItemType Directory -Path "$baseModuleDir/dex", "$baseModuleDir/manifest", "$baseModuleDir/res", "$baseModuleDir/assets" -Force | Out-Null

Copy-Item "$buildDir/dex/classes.dex" "$baseModuleDir/dex/classes.dex"
Copy-Item -Path "android/app/src/main/assets/*" -Destination "$baseModuleDir/assets" -Recurse -Force

# Extract base proto package into AAB base structure
Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::ExtractToDirectory("$buildDir/base_proto.zip", "$buildDir/extracted_proto")
if (Test-Path "$buildDir/extracted_proto/AndroidManifest.xml") {
    Copy-Item "$buildDir/extracted_proto/AndroidManifest.xml" "$baseModuleDir/manifest/AndroidManifest.xml"
}
if (Test-Path "$buildDir/extracted_proto/resources.pb") {
    Copy-Item "$buildDir/extracted_proto/resources.pb" "$baseModuleDir/resources.pb"
}
if (Test-Path "$buildDir/extracted_proto/res") {
    Copy-Item "$buildDir/extracted_proto/res/*" "$baseModuleDir/res" -Recurse -Force
}

Write-Host "9. Packaging base.zip module for bundletool..."
$baseZip = "$buildDir/base.zip"
if (Test-Path $baseZip) { Remove-Item $baseZip -Force }
$fullBaseZip = (Resolve-Path $buildDir).Path + "\base.zip"
$jarTool = "$jbr\bin\jar.exe"
Push-Location $baseModuleDir
& $jarTool -c -M -f $fullBaseZip manifest dex res assets resources.pb
Pop-Location

Write-Host "10. Building official Android App Bundle (.aab) with bundletool..."
$aabOut = "dist/kongkong-release.aab"
if (Test-Path $aabOut) { Remove-Item $aabOut -Force }

& "$jbr\bin\java.exe" -jar "bundletool.jar" build-bundle --modules="$baseZip" --output="$aabOut"

Write-Host "11. Signing AAB with release keystore (SHA256withRSA)..."
$jarsigner = "$jbr\bin\jarsigner.exe"
& $jarsigner -sigalg SHA256withRSA -digestalg SHA-256 -keystore "release-keystore.jks" -storepass "kongkong1234!" -keypass "kongkong1234!" $aabOut "kongkong"

Write-Host "12. Validating App Bundle with Google bundletool..."
& "$jbr\bin\java.exe" -jar "bundletool.jar" validate --bundle="$aabOut"

Write-Host "SUCCESS! Both signed APK and official AAB have been generated in dist/:"
Get-ChildItem "dist" | Select-Object Name, Length, LastWriteTime
