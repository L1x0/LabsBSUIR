from __future__ import annotations

import re
from collections import Counter
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable, List, Tuple


WORD_RE = re.compile(r"[а-яё]+", re.IGNORECASE)
ALLOWED_SINGLE_LETTERS = {"а", "и", "в", "к", "с", "у", "о", "я"}


@dataclass
class TokenStats:
    tokens: List[str]
    frequencies: List[Tuple[str, int]]


def read_text_file(path: str | Path, encoding: str = "utf-8") -> str:
    p = Path(path)
    return p.read_text(encoding=encoding)


def read_pdf_file(path: str | Path) -> str:
    from pathlib import Path as _Path

    import pdfplumber

    p = _Path(path)
    text_chunks: list[str] = []
    with pdfplumber.open(p) as pdf:
        for page in pdf.pages:
            page_text = page.extract_text() or ""
            text_chunks.append(page_text)
    return "\n".join(text_chunks)


def normalize_text(text: str, *, lower: bool = True, replace_yo: bool = True) -> str:
    if lower:
        text = text.lower()
    if replace_yo:
        text = text.replace("ё", "е")
    return text


def tokenize_russian(text: str) -> List[str]:
    tokens = WORD_RE.findall(text)
    cleaned: list[str] = []
    for t in tokens:
        if len(t) == 1 and t not in ALLOWED_SINGLE_LETTERS:
            continue
        if len(t) >= 2 or t in ALLOWED_SINGLE_LETTERS:
            cleaned.append(t)
    return cleaned


def build_frequencies(tokens: Iterable[str]) -> List[Tuple[str, int]]:
    counter = Counter(tokens)
    items = list(counter.items())
    items.sort(key=lambda kv: (-kv[1], kv[0]))
    return items


def analyze_text(text: str) -> TokenStats:
    norm = normalize_text(text)
    tokens = tokenize_russian(norm)
    freqs = build_frequencies(tokens)
    return TokenStats(tokens=tokens, frequencies=freqs)

