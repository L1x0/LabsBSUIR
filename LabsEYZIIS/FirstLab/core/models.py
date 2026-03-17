from __future__ import annotations

from dataclasses import dataclass, field
from enum import Enum
from bisect import bisect_left
from typing import Dict, Iterable, List, Optional, Tuple


class CaseRu(Enum):
    NOM = "Именительный"
    GEN = "Родительный"
    DAT = "Дательный"
    ACC = "Винительный"
    INS = "Творительный"
    PREP = "Предложный"

    @staticmethod
    def from_pymorphy(case_code: str) -> Optional["CaseRu"]:
        mapping = {
            "nomn": CaseRu.NOM,
            "gent": CaseRu.GEN,
            "datv": CaseRu.DAT,
            "accs": CaseRu.ACC,
            "ablt": CaseRu.INS,
            "loct": CaseRu.PREP,
            "loc2": CaseRu.PREP,
        }
        return mapping.get(case_code)


class NumberRu(Enum):
    SG = "Ед. число"
    PL = "Мн. число"

    @staticmethod
    def from_pymorphy(num_code: str) -> Optional["NumberRu"]:
        return {"sing": NumberRu.SG, "plur": NumberRu.PL}.get(num_code)


ALL_CASES_RU: List[CaseRu] = [
    CaseRu.NOM,
    CaseRu.GEN,
    CaseRu.DAT,
    CaseRu.ACC,
    CaseRu.INS,
    CaseRu.PREP,
]

ALL_NUMBERS_RU: List[NumberRu] = [NumberRu.SG, NumberRu.PL]


@dataclass
class LexemeEntry:
    lemma: str
    stem: str
    pos: str = "Неизвестно"
    pos_code: str = ""
    tag: str = ""
    gender: Optional[str] = None
    endings: Dict[str, str] = field(default_factory=dict)
    forms: Dict[str, str] = field(default_factory=dict)
    notes: str = ""
    frequency: Optional[int] = None

    def to_dict(self) -> dict:
        return {
            "lemma": self.lemma,
            "stem": self.stem,
            "pos": self.pos,
            "pos_code": self.pos_code,
            "tag": self.tag,
            "gender": self.gender,
            "endings": dict(self.endings),
            "forms": dict(self.forms),
            "notes": self.notes,
            "frequency": self.frequency,
        }

    @staticmethod
    def from_dict(data: dict) -> "LexemeEntry":
        return LexemeEntry(
            lemma=data["lemma"],
            stem=data["stem"],
            pos=data.get("pos", "Неизвестно"),
            pos_code=data.get("pos_code", ""),
            tag=data.get("tag", ""),
            gender=data.get("gender"),
            endings=dict(data.get("endings", {})),
            forms=dict(data.get("forms", {})),
            notes=data.get("notes", ""),
            frequency=data.get("frequency"),
        )


@dataclass
class DeclensionRule:
    id: str
    name: str
    endings: Dict[FormKey, str] = field(default_factory=dict)
    notes: str = ""

    def to_dict(self) -> dict:
        return {
            "id": self.id,
            "name": self.name,
            "endings": dict(self.endings),
            "notes": self.notes,
        }

    @staticmethod
    def from_dict(data: dict) -> "DeclensionRule":
        return DeclensionRule(
            id=data["id"],
            name=data.get("name", data["id"]),
            endings=dict(data.get("endings", {})),
            notes=data.get("notes", ""),
        )


@dataclass
class LexiconStore:
    by_lemma: Dict[str, LexemeEntry] = field(default_factory=dict)
    alphabetical: List[str] = field(default_factory=list)
    rules: Dict[str, DeclensionRule] = field(default_factory=dict)

    def add_or_update_lexeme(self, entry: LexemeEntry) -> None:
        exists = entry.lemma in self.by_lemma
        self.by_lemma[entry.lemma] = entry
        if not exists:
            self._insert_alphabetical(entry.lemma)

    def _insert_alphabetical(self, lemma: str) -> None:
        idx = bisect_left(self.alphabetical, lemma)
        if idx >= len(self.alphabetical) or self.alphabetical[idx] != lemma:
            self.alphabetical.insert(idx, lemma)

    def remove_lexeme(self, lemma: str) -> None:
        if lemma in self.by_lemma:
            del self.by_lemma[lemma]
        try:
            self.alphabetical.remove(lemma)
        except ValueError:
            pass

    def get_lexeme(self, lemma: str) -> Optional[LexemeEntry]:
        return self.by_lemma.get(lemma)

    def iter_lexemes(self) -> Iterable[LexemeEntry]:
        for lemma in self.alphabetical:
            entry = self.by_lemma.get(lemma)
            if entry:
                yield entry

    def search_prefix(self, prefix: str) -> List[LexemeEntry]:
        prefix = prefix or ""
        if not prefix:
            return list(self.iter_lexemes())
        start_idx = bisect_left(self.alphabetical, prefix)
        result: List[LexemeEntry] = []
        for lemma in self.alphabetical[start_idx:]:
            if not lemma.startswith(prefix):
                break
            entry = self.by_lemma.get(lemma)
            if entry:
                result.append(entry)
        return result

    def add_or_update_rule(self, rule: DeclensionRule) -> None:
        self.rules[rule.id] = rule

    def to_json_dict(self) -> dict:
        return {
            "rules": [r.to_dict() for r in self.rules.values()],
            "lexemes": [e.to_dict() for e in self.iter_lexemes()],
        }

    @staticmethod
    def from_json_dict(data: dict) -> "LexiconStore":
        rules_list = data.get("rules", [])
        lexemes_list = data.get("lexemes", [])

        store = LexiconStore()
        for r_data in rules_list:
            rule = DeclensionRule.from_dict(r_data)
            store.rules[rule.id] = rule
        for e_data in lexemes_list:
            entry = LexemeEntry.from_dict(e_data)
            store.add_or_update_lexeme(entry)
        return store

