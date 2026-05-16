import argparse
from dataclasses import asdict
import json
from pathlib import Path
import sys

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from src.image_pipeline import process_images_in_path


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Summarize local image files with Gemini and store the images plus summaries in GCS."
    )
    parser.add_argument("path", help="Path to a local image file or directory.")
    parser.add_argument(
        "--prefix",
        default="photo-features",
        help="GCS folder prefix. Default: photo-features",
    )
    parser.add_argument(
        "--recursive",
        action="store_true",
        help="Process images in nested directories when the path is a directory.",
    )
    args = parser.parse_args()

    results = process_images_in_path(
        args.path,
        gcs_prefix=args.prefix,
        recursive=args.recursive,
    )
    print(json.dumps([asdict(result) for result in results], ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
