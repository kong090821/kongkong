# 꽁꽁 출산가방 프로젝트 개발 가이드라인 (GEMINI.md)

## 📌 작업 완료 시 필수 절차

### 1. 작업 완료 시 자동 실행 규칙 (Every Task Completion)
모든 개발 및 수정 작업이 완료되면 반드시 다음 3단계를 자동으로 수행합니다:

1. **수정 정도에 따른 버전업 유무 및 버전 판단**:
   - **Major (예: 1.1.4 -> 2.0.0 / versionCode 증가)**: 대규모 구조 개편, 호환성이 깨지는 주요 변경
   - **Minor (예: 1.1.4 -> 1.2.0 / versionCode + 1)**: 신규 기능 추가, 주요 UI 개편, 데이터 스키마 확장
   - **Patch (예: 1.1.4 -> 1.1.5 / versionCode + 1)**: 버그 수정, 텍스트/스타일 미세 조정, 소규모 리소스 교체
   - **버전 유지**: 단순 주석, 테스트 코드, 개발 환경 설정 등 앱 배포 버전에 영향 없는 경미한 수정
   - **버전 반영 대상**:
     - `android/app/build.gradle` (`versionCode`, `versionName`)
     - `build_android.ps1` (`--version-code`, `--version-name`)
     - `android/app/src/main/AndroidManifest.xml` (필요 시)

2. **작업 노트(PATCH_NOTES.md) 업데이트**:
   - `PATCH_NOTES.md` 최상단에 작업 날짜(YYYY-MM-DD), 변경된 버전, 작업 세부 내역([UI/UX], [기능], [데이터], [버그수정] 등)을 상세히 기록합니다.

3. **깃허브 푸쉬 (GitHub Push)**:
   - 변경 사항을 스테이징(`git add .`)
   - 작업 내용을 반영한 명확한 커밋 메시지 작성(`git commit -m "..."`)
   - `git push origin main` 실행하여 원격 저장소에 자동 푸쉬

---

### 2. 사용자 요청 시에만 실행하는 규칙 (On Explicit User Request Only)
- **APK / AAB(ABB) 빌드 파일 생성**:
  - 기본 작업 완료 시 자동으로 빌드하지 **않습니다**.
  - **사용자가 명시적으로 요구할 때만** `build_android.ps1`을 실행하여 최신 버전에 맞는 서명된 APK(`dist/kongkong-release-signed.apk`) 및 AAB(`dist/kongkong-release.aab`)를 생성합니다.
