from __future__ import annotations

import json
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

        items.append(
            {
                "number": int(metadata.get("number") or index),
                "description": description,
                "metadata_blob": blob.name,
                "image_gcs_uri": metadata.get("image_gcs_uri", ""),
                "summary_gcs_uri": metadata.get("summary_gcs_uri", ""),
            }
        )

    return items

