# AI Bridge

This backend can optionally call the Python AI/GCS project in `./ai`.

The bridge is disabled by default. Enable it with:

```powershell
$env:AI_BRIDGE_ENABLED="true"
```

Default paths:

```text
AI_BRIDGE_PYTHON_EXECUTABLE=ai/.venv/Scripts/python.exe
AI_BRIDGE_PROJECT_DIR=ai
AI_BRIDGE_ANALYSES_PREFIX=photo-features/analyses/
AI_BRIDGE_UPLOAD_PREFIX=photo-features
```

## Found Item

Send `image_path` when registering a found item.

```json
{
  "description": "검은색 가죽 가방을 발견했습니다.",
  "foundLocation": "station",
  "foundTime": "today",
  "handoverStatus": "kept",
  "image_path": "C:\\Users\\PC\\Downloads\\bag.png",
  "name": "finder",
  "phoneNumber": "010-0000-0000",
  "allowPhoneContact": true
}
```

When enabled, the backend calls:

```text
python scripts/process_image.py <image_path> --prefix photo-features
```

## Lost Item

The lost item `description` is matched against GCS image analysis metadata.

When enabled, the backend calls:

```text
python scripts/match_text.py <description> --prefix photo-features/analyses/ --json
```

## Python Setup

```powershell
cd ai
python -m venv .venv
.\.venv\Scripts\python -m pip install -r requirements.txt
Copy-Item .env.example .env
```

Fill `.env` with Gemini and GCS credentials before enabling the bridge.
