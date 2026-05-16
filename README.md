# Gemini + GCS 기본 환경

Python에서 Gemini API와 Google Cloud Storage를 바로 호출할 수 있는 최소 설정입니다.
Gemini는 Google 공식 문서에서 권장하는 `google-genai` SDK를 사용합니다.

## 1. 가상환경 생성

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install -r requirements.txt
```

## 2. 환경변수 설정

`.env.example`을 복사해 `.env`를 만들고 값을 채우세요.

```powershell
Copy-Item .env.example .env
```

필수 값:

- `GEMINI_API_KEY`: Google AI Studio에서 발급한 Gemini API 키
- `GCP_PROJECT_ID`: GCP 프로젝트 ID
- `GCS_BUCKET_NAME`: 사용할 GCS 버킷 이름
- `GOOGLE_APPLICATION_CREDENTIALS`: 서비스 계정 JSON 파일 경로

서비스 계정 JSON 파일은 예를 들어 `service-account.json`으로 프로젝트 루트에 두면 됩니다. 이 파일은 `.gitignore`에 포함되어 커밋되지 않습니다.

## 3. 연결 확인

```powershell
python scripts/check_integrations.py
```

정상 연결되면 Gemini 응답과 GCS 버킷 경로가 출력됩니다.

## 사용 예시

```python
from src.gemini_client import generate_text
from src.gcs_client import upload_file

answer = generate_text("한 문장으로 자기소개를 작성해줘.")
uploaded_uri = upload_file("local-file.txt", "uploads/local-file.txt")

print(answer)
print(uploaded_uri)
```

## 사진 특징 정리 후 GCS 저장

로컬에 있는 사진을 Gemini로 분석하고, 원본 사진과 분석 결과를 GCS에 저장합니다.

```powershell
python scripts/process_image.py "C:\path\to\photo.jpg"
```

폴더 안의 사진을 처리하려면 폴더 경로를 넘기면 됩니다.

```powershell
python scripts/process_image.py "C:\path\to\photos"
```

하위 폴더까지 처리하려면 `--recursive`를 추가하세요.

```powershell
python scripts/process_image.py "C:\path\to\photos" --recursive
```

저장 위치:

- 원본 사진: `gs://<bucket>/photo-features/images/...`
- 특징 요약 텍스트: `gs://<bucket>/photo-features/analyses/...txt`
- 처리 메타데이터: `gs://<bucket>/photo-features/analyses/...json`

메타데이터 JSON에는 각 사진 분석 항목의 고유 ID인 `item_id`가 포함됩니다.
`item_id`는 같은 이름의 `.txt` 분석 파일과 연결되는 안정적인 식별자입니다.

GCS 경로 prefix를 바꾸고 싶으면 `--prefix`를 사용하세요.

```powershell
python scripts/process_image.py "C:\path\to\photo.jpg" --prefix "uploads/session-1"
```

## 텍스트 설명으로 GCS DB 검색

GCS에 저장된 이미지 분석 결과와 사용자가 입력한 물건 설명을 Gemini로 비교합니다.
일치하는 물건이 있으면 번호를 출력하고, 없으면 `DB에 없음`을 출력합니다.

```powershell
python scripts/match_text.py "검은색 가죽 여성용 핸드백"
```

비교 대상은 기본적으로 아래 GCS 경로입니다.

```text
gs://<bucket>/photo-features/analyses/*.json
```

다른 prefix를 쓰려면:

```powershell
python scripts/match_text.py "검은색 가죽 여성용 핸드백" --prefix "uploads/session-1/analyses/"
```

기존 GCS 분석 JSON에 `item_id`를 추가하려면:

```powershell
python scripts/backfill_gcs_item_ids.py
```
