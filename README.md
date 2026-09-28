# 출산가방 - 안드로이드 출산 준비물 체크리스트 어플

아기를 맞이하는 예비 부모님들을 위한 깔끔하고 감성적인 안드로이드 체크리스트 애플리케이션입니다.  
**OneDrive 클라우드 동기화를 통해 여러 대의 컴퓨터에서 원활하게 이어 개발할 수 있도록 환경이 구축되어 있습니다.**

> 🌟 **[새 컴퓨터로 프로젝트를 고정·이전하시나요?]**  
> 지금까지 작업한 모든 기능과 새로운 컴퓨터에서의 세팅 방법이 완벽하게 정리된 **[`새_컴퓨터_이전_및_작업인수인계_가이드.md`](새_컴퓨터_이전_및_작업인수인계_가이드.md)** 문서를 확인해 주세요!

---

## ⚡ 빠른 실행 & 멀티 PC 협업 도구

| 실행 파일 | 설명 |
| :--- | :--- |
| **`setup_environment.bat`** | **[새 PC 시작 시]** 현재 컴퓨터의 Android SDK 경로를 자동 감지하여 `local.properties`를 자동 설정합니다. |
| **`clean_cloud_temp.bat`** | **[PC 이동 전]** 불필요한 빌드 임시파일/캐시(`.gradle`, `build`)를 정리하여 OneDrive 동기화 충돌을 방지합니다. |
| **`앱_미리보기_실행.bat`** | **[원클릭 실행]** Android Studio가 없어도 브라우저에서 실제 모바일 앱과 동일하게 작동하는 화면을 즉시 띄웁니다. |
| **`시트_데이터_동기화.bat`** | **[데이터 동기화]** 엑셀(`출산준비물_통합데이터.xlsx`) 수정한 내용을 프로젝트 데이터로 1초 만에 가져옵니다. |

> 📖 멀티 PC 연동에 관한 상세 안내는 [`MULTI_PC_GUIDE.md`](MULTI_PC_GUIDE.md) 및 [`구글시트_연동_가이드.md`](구글시트_연동_가이드.md)를 참고해 주세요.

---

## 📱 앱 개요 및 주요 4대 기능

1. **출산가방 체크리스트**: 분만 병원과 산후조리원 입소 시 챙겨야 할 필수 준비물 체크 (산모 30개, 아기 16개, 보호자 21개)
2. **육아용품 체크리스트**: 수유, 수면, 목욕, 위생, 외출 등 신생아 맞이 필수 용품 리스트
3. **시기별 할일 체크리스트**: 임신 주수별 정기 검진, 출생신고 등 시기별 해야 할 일
4. **출산 혜택 정리**: 첫만남이용권, 부모급여, 아동수당 및 전국·지자체 지원금 모아보기
5. **내 가방 (MyBag)**: 내가 담은 필수 준비물과 저장한 지원 혜택을 한눈에 관리

---

## 💻 실행 및 확인 방법

### 1. 웹 브라우저에서 즉시 모바일 프리뷰 확인하기 (가장 빠름)
별도 설치나 무거운 빌드 과정 없이, 브라우저에서 실제 스마트폰처럼 작동하는 화면을 바로 확인하실 수 있습니다:
- 폴더 내 **`앱_미리보기_실행.bat`**을 더블 클릭합니다.
- 로그인, 온보딩 설문(맞춤 추천), 체크리스트 체크, 필터 및 검색 등 모든 인터랙션을 직접 체험할 수 있습니다.

### 2. Android Studio에서 실행하기
1. **`setup_environment.bat`**을 실행하여 현재 컴퓨터의 SDK 경로를 확인/세팅합니다.
2. Android Studio 실행 후 `Open...` 메뉴에서 본 프로젝트 폴더(`출산준비물어플`)를 선택합니다.
3. Gradle 동기화(Sync) 완료 후 에뮬레이터 또는 안드로이드 폰을 연결하여 실행(`Shift + F10`)합니다.

---

## 🚀 구글 플레이스토어 출시 현황
* **패키지명 (Application ID)**: `com.kongkong.babybag`
* **버전**: `1.0.1` (버전 코드: `2`)
* **타겟 SDK**: Android 16 (API 36) / **최소 SDK**: Android 7.0 (API 24)
* **심사 상태**: **구글 플레이 콘솔 검토 중 (In Review)** (2026.09.28 제출 완료)
* **공식 정책 웹페이지**:
  * [개인정보처리방침 (Privacy Policy)](https://kong090821.github.io/kongkong/privacy.html)
  * [계정 및 데이터 삭제 정책 (Data Deletion)](https://kong090821.github.io/kongkong/delete-account.html)

---

## 🛠 기술 스택
- **Language**: Java 17 / JavaScript (ES6+)
- **Application Framework**: Android WebView Native Wrapper + Modern Responsive WebApp
- **Build System**: Google Official `bundletool` & `aapt2` Automated Pipeline (`build_android.ps1`)
- **Backend / Sync**: Firebase Firestore & Auth, Google Sheets API (실시간 데이터 연동)
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 16 (API 36)
