from __future__ import annotations

import json
from typing import Any

from google.genai import types

from src.config import get_settings
from src.gcs_db import DEFAULT_ANALYSES_PREFIX, load_items_from_gcs
from src.gemini_client import get_gemini_client


MATCH_PROMPT_TEMPLATE = """
너는 물건 설명 매칭 시스템이다.

사용자가 입력한 설명과 DB에 저장된 이미지 설명들을 비교해서, 같은 물건이거나 매우 유사한 물건이면 해당 DB 번호를 선택해라.
색상, 물건 종류, 형태, 재질, 주요 특징이 충분히 일치해야 한다.
애매하거나 DB에 같은 물건이 없다고 판단되면 null을 선택해라.

반드시 JSON 하나만 출력해라.
출력 형식:
{{
  "number": 1 또는 null,
  "reason": "짧은 판단 이유"
}}

[사용자 설명]
{query}

[DB 목록]
{db_items}
""".strip()


def _compact_items(items: list[dict[str, Any]]) -> list[dict[str, Any]]:
    return [
        {
            "number": item["number"],
            "description": item["description"],
        }
        for item in items
    ]


def match_text_to_item(
    query: str,
    analyses_prefix: str = DEFAULT_ANALYSES_PREFIX,
) -> int | None:
    items = load_items_from_gcs(analyses_prefix)
    if not items:
        return None

    prompt = MATCH_PROMPT_TEMPLATE.format(
        query=query.strip(),
        db_items=json.dumps(_compact_items(items), ensure_ascii=False, indent=2),
    )

    settings = get_settings()
    client = get_gemini_client()
    response = client.models.generate_content(
        model=settings.gemini_model,
        contents=prompt,
        config=types.GenerateContentConfig(response_mime_type="application/json"),
    )

    raw_text = response.text or "{}"
    try:
        parsed = json.loads(raw_text)
    except json.JSONDecodeError:
        return None

    number = parsed.get("number")
    if number is None:
        return None

    valid_numbers = {int(item["number"]) for item in items}
    try:
        matched_number = int(number)
    except (TypeError, ValueError):
        return None

    return matched_number if matched_number in valid_numbers else None
