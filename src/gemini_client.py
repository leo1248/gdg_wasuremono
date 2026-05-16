from google import genai

from src.config import get_settings, require


def get_gemini_client() -> genai.Client:
    settings = get_settings()
    return genai.Client(api_key=require(settings.gemini_api_key, "GEMINI_API_KEY"))


def generate_text(prompt: str) -> str:
    settings = get_settings()
    client = get_gemini_client()
    response = client.models.generate_content(
        model=settings.gemini_model,
        contents=prompt,
    )
    return response.text or ""
