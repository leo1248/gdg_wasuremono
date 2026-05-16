from dataclasses import dataclass
import os

from dotenv import load_dotenv


load_dotenv()


@dataclass(frozen=True)
class Settings:
    gemini_api_key: str
    gemini_model: str
    gcp_project_id: str
    gcs_bucket_name: str
    google_application_credentials: str


def get_settings() -> Settings:
    return Settings(
        gemini_api_key=os.getenv("GEMINI_API_KEY", ""),
        gemini_model=os.getenv("GEMINI_MODEL", "gemini-1.5-flash"),
        gcp_project_id=os.getenv("GCP_PROJECT_ID", ""),
        gcs_bucket_name=os.getenv("GCS_BUCKET_NAME", ""),
        google_application_credentials=os.getenv("GOOGLE_APPLICATION_CREDENTIALS", ""),
    )


def require(value: str, name: str) -> str:
    placeholder_prefixes = ("your_", "your-", "YOUR_", "YOUR-")
    if not value or value.startswith(placeholder_prefixes):
        raise RuntimeError(f"{name} is required. Set it in your .env file.")
    return value
