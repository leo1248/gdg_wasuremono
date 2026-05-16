import argparse
import json
from pathlib import Path
import sys

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from src.gcs_db import backfill_item_ids


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Add stable item_id fields to existing GCS image analysis metadata."
    )
    parser.add_argument(
        "--prefix",
        default="photo-features/analyses/",
        help="GCS analyses prefix. Default: photo-features/analyses/",
    )
    args = parser.parse_args()

    updated = backfill_item_ids(args.prefix)
    print(json.dumps(updated, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
