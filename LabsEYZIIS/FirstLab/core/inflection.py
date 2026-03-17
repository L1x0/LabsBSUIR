from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, Optional

from .models import (
    ALL_CASES_RU,
    ALL_NUMBERS_RU,
    CaseRu,
    LexemeEntry,
    LexiconStore,
    NumberRu,
)


@dataclass
class InflectionEngine:
    lexicon: LexiconStore

    def generate(self, entry: LexemeEntry, case: CaseRu, number: NumberRu) -> Optional[str]:
        key = f"{case.value}, {number.value}"
        if key in entry.forms:
            return entry.forms[key]
        try:
            from nlp.morphology import generate_noun_form

            return generate_noun_form(entry.lemma, case, number)
        except Exception:
            return None

    def generate_table(self, entry: LexemeEntry) -> Dict[str, Optional[str]]:
        table: Dict[str, Optional[str]] = {}
        for case in ALL_CASES_RU:
            for number in ALL_NUMBERS_RU:
                key = f"{case.value}, {number.value}"
                table[key] = self.generate(entry, case, number)
        return table

    def generate_by_lemma(self, lemma: str, case: CaseRu, number: NumberRu) -> Optional[str]:
        entry = self.lexicon.get_lexeme(lemma)
        if not entry:
            return None
        return self.generate(entry, case, number)

