from __future__ import annotations

import json
from pathlib import Path
from typing import Any

from core.models import LexiconStore


def save_lexicon_to_file(store: LexiconStore, path: str | Path) -> None:
    p = Path(path)
    data = store.to_json_dict()
    p.write_text(json.dumps(data, ensure_ascii=False, indent=2), encoding="utf-8")


def load_lexicon_from_file(path: str | Path) -> LexiconStore:
    p = Path(path)
    raw = p.read_text(encoding="utf-8")
    data: Any = json.loads(raw)
    return LexiconStore.from_json_dict(data)


def export_markdown(store: LexiconStore) -> str:
    lines: list[str] = ["# Словарь словоформ", ""]
    for entry in store.iter_lexemes():
        lines.append(f"## {entry.lemma}")
        lines.append("")
        lines.append(f"- Основа: **{entry.stem}**")
        if entry.pos:
            lines.append(f"- Часть речи: **{entry.pos}**")
        if entry.gender:
            lines.append(f"- Род: **{entry.gender}**")
        if entry.tag:
            lines.append(f"- Морфологическая метка: `{entry.tag}`")
        if entry.frequency is not None:
            lines.append(f"- Частота в тексте: **{entry.frequency}**")
        if entry.notes:
            lines.append(f"- Примечания: {entry.notes}")
        if entry.endings:
            lines.append("")
            lines.append("Таблица окончаний (по падежу и числу):")
            lines.append("")
            for k in sorted(entry.endings.keys()):
                lines.append(f"- {k}: `{entry.endings[k]}`")
        lines.append("")
    return "\n".join(lines)

