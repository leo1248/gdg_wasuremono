from pathlib import Path
import sys

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from src.gemini_client import generate_text
from src.gcs_client import get_bucket


def main() -> None:
    gemini_result = generate_text("Reply with only: Gemini connected")
    bucket = get_bucket()

    print(f"Gemini: {gemini_result.strip()}")
    print(f"GCS bucket: gs://{bucket.name}")


if __name__ == "__main__":
    main()
