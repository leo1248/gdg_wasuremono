import argparse
from dataclasses import asdict
import json
from pathlib import Path
import sys

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from src.text_matcher import match_text_to_item, match_text_to_item_detail


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Match a text object description against Gemini image summaries stored in GCS."
    )
    parser.add_argument("text", help="Object description text to search for.")
    parser.add_argument(
        "--prefix",
        default="photo-features/analyses/",
        help="GCS analyses prefix. Default: photo-features/analyses/",
    )
    parser.add_argument(
        "--json",
        action="store_true",
        help="Print a JSON result for server-to-server integration.",
    )
    args = parser.parse_args()

    if args.json:
        result = match_text_to_item_detail(args.text, analyses_prefix=args.prefix)
        print(json.dumps(asdict(result), ensure_ascii=False))
        return

    number = match_text_to_item(args.text, analyses_prefix=args.prefix)
    print(number if number is not None else "DB에 없음")


if __name__ == "__main__":
    main()
