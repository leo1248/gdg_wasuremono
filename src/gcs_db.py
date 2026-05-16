from __future__ import annotations

import json
from pathlib import PurePosixPath
from typing import Any

from src.gcs_client import get_bucket


DEFAULT_ANALYSES_PREFIX = "photo-features/analyses/"


def load_items_from_gcs(prefix: str = DEFAULT_ANALYSES_PREFIX) -> list[dict[str, Any]]:
    normalized_prefix = prefix.strip("/") + "/"
    bucket = get_bucket()
    json_blobs = sorted(
        (
            blob
            for blob in bucket.list_blobs(prefix=normalized_prefix)
            if blob.name.endswith(".json")
        ),
        key=lambda blob: blob.name,
    )

    items: list[dict[str, Any]] = []
    for index, blob in enumerate(json_blobs, start=1):
        metadata = json.loads(blob.download_as_text(encoding="utf-8"))
        description = metadata.get("summary", "").strip()
        if not description:
            continue
        item_id = metadata.get("item_id") or PurePosixPath(blob.name).stem

        items.append(
            {
                "number": int(metadata.get("number") or index),
                "item_id": item_id,
                "description": description,
                "metadata_blob": blob.name,
                "image_gcs_uri": metadata.get("image_gcs_uri", ""),
                "summary_gcs_uri": metadata.get("summary_gcs_uri", ""),
            }
        )

    return items


def backfill_item_ids(prefix: str = DEFAULT_ANALYSES_PREFIX) -> list[dict[str, str]]:
    normalized_prefix = prefix.strip("/") + "/"
    bucket = get_bucket()
    updated: list[dict[str, str]] = []

    for blob in sorted(bucket.list_blobs(prefix=normalized_prefix), key=lambda item: item.name):
        if not blob.name.endswith(".json"):
            continue

        metadata = json.loads(blob.download_as_text(encoding="utf-8"))
        if metadata.get("item_id"):
            continue

        item_id = PurePosixPath(blob.name).stem
        metadata["item_id"] = item_id
        blob.upload_from_string(
            json.dumps(metadata, ensure_ascii=False, indent=2),
            content_type="application/json; charset=utf-8",
        )
        updated.append({"metadata_blob": blob.name, "item_id": item_id})

    return updated
