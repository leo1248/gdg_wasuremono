from __future__ import annotations

from dataclasses import dataclass
from datetime import datetime, timezone
import mimetypes
from pathlib import Path
from uuid import uuid4

from google.genai import types

from src.config import get_settings
from src.gcs_client import upload_file, upload_json, upload_text
from src.gemini_client import get_gemini_client


DEFAULT_IMAGE_PROMPT = """
이 이미지를 분석해서 특징을 한국어로 정리해줘.

다음 항목을 포함해:
- 주요 피사체
- 배경과 장소 추정
- 눈에 띄는 객체
- 색감과 분위기
- 활용 가능한 키워드 5개

확실하지 않은 내용은 추정이라고 표시해.
""".strip()

SUPPORTED_IMAGE_SUFFIXES = {
    ".bmp",
    ".gif",
    ".jpeg",
    ".jpg",
    ".png",
    ".webp",
}


@dataclass(frozen=True)
class ImageProcessResult:
    item_id: str
    local_image_path: str
    image_gcs_uri: str
    summary_gcs_uri: str
    metadata_gcs_uri: str
    summary: str


def summarize_image(image_path: str, prompt: str = DEFAULT_IMAGE_PROMPT) -> str:
    path = Path(image_path)
    if not path.exists():
        raise FileNotFoundError(f"Image not found: {path}")
    if not path.is_file():
        raise ValueError(f"Image path must be a file: {path}")

    mime_type = mimetypes.guess_type(path.name)[0]
    if not mime_type or not mime_type.startswith("image/"):
        raise ValueError(f"Unsupported image type: {path.name}")

    client = get_gemini_client()
    settings = get_settings()
    response = client.models.generate_content(
        model=settings.gemini_model,
        contents=[
            types.Part.from_text(text=prompt),
            types.Part.from_bytes(data=path.read_bytes(), mime_type=mime_type),
        ],
    )
    return response.text or ""


def process_image(image_path: str, gcs_prefix: str = "photo-features") -> ImageProcessResult:
    path = Path(image_path)
    summary = summarize_image(str(path))

    now = datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%SZ")
    item_id = f"{path.stem}-{now}-{uuid4().hex[:8]}"
    normalized_prefix = gcs_prefix.strip("/")

    image_blob = f"{normalized_prefix}/images/{item_id}{path.suffix.lower()}"
    summary_blob = f"{normalized_prefix}/analyses/{item_id}.txt"
    metadata_blob = f"{normalized_prefix}/analyses/{item_id}.json"

    image_gcs_uri = upload_file(str(path), image_blob)
    summary_gcs_uri = upload_text(summary, summary_blob)

    metadata = {
        "item_id": item_id,
        "local_image_path": str(path.resolve()),
        "image_gcs_uri": image_gcs_uri,
        "summary_gcs_uri": summary_gcs_uri,
        "summary": summary,
    }
    metadata_gcs_uri = upload_json(metadata, metadata_blob)

    return ImageProcessResult(
        item_id=item_id,
        local_image_path=str(path.resolve()),
        image_gcs_uri=image_gcs_uri,
        summary_gcs_uri=summary_gcs_uri,
        metadata_gcs_uri=metadata_gcs_uri,
        summary=summary,
    )


def find_images(path: str, recursive: bool = False) -> list[Path]:
    target = Path(path)
    if target.is_file():
        return [target] if target.suffix.lower() in SUPPORTED_IMAGE_SUFFIXES else []
    if not target.exists():
        raise FileNotFoundError(f"Path not found: {target}")
    if not target.is_dir():
        raise ValueError(f"Path must be an image file or directory: {target}")

    pattern = "**/*" if recursive else "*"
    return sorted(
        file_path
        for file_path in target.glob(pattern)
        if file_path.is_file() and file_path.suffix.lower() in SUPPORTED_IMAGE_SUFFIXES
    )


def process_images_in_path(
    path: str,
    gcs_prefix: str = "photo-features",
    recursive: bool = False,
) -> list[ImageProcessResult]:
    images = find_images(path, recursive=recursive)
    if not images:
        raise FileNotFoundError(f"No supported image files found in: {path}")
    return [process_image(str(image), gcs_prefix=gcs_prefix) for image in images]
