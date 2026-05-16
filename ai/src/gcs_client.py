from pathlib import Path
from typing import Any

from google.cloud import storage

from src.config import get_settings, require


def get_bucket():
    settings = get_settings()
    project_id = require(settings.gcp_project_id, "GCPㅁ_PROJECT_ID")
    bucket_name = require(settings.gcs_bucket_name, "GCS_BUCKET_NAME")
    return storage.Client(project=project_id).bucket(bucket_name)


def upload_file(local_path: str, destination_blob_name: str) -> str:
    path = Path(local_path)
    if not path.exists():
        raise FileNotFoundError(f"File not found: {path}")

    blob = get_bucket().blob(destination_blob_name)
    blob.upload_from_filename(str(path))
    return f"gs://{blob.bucket.name}/{blob.name}"


def upload_text(
    text: str,
    destination_blob_name: str,
    content_type: str = "text/plain; charset=utf-8",
) -> str:
    blob = get_bucket().blob(destination_blob_name)
    blob.upload_from_string(text, content_type=content_type)
    return f"gs://{blob.bucket.name}/{blob.name}"


def upload_json(data: dict[str, Any], destination_blob_name: str) -> str:
    import json

    return upload_text(
        json.dumps(data, ensure_ascii=False, indent=2),
        destination_blob_name,
        content_type="application/json; charset=utf-8",
    )


def download_file(source_blob_name: str, local_path: str) -> str:
    path = Path(local_path)
    path.parent.mkdir(parents=True, exist_ok=True)

    blob = get_bucket().blob(source_blob_name)
    blob.download_to_filename(str(path))
    return str(path)
