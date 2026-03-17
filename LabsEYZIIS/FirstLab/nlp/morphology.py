from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, Iterable, Optional, Tuple

from core.models import CaseRu, LexemeEntry, LexiconStore, NumberRu
from nlp.text_processing import analyze_text


def _get_morph():
    from pymorphy3 import MorphAnalyzer

    return MorphAnalyzer(lang="ru")


def _common_prefix(words: Iterable[str]) -> str:
    words = list(words)
    if not words:
        return ""
    s1 = min(words)
    s2 = max(words)
    i = 0
    while i < len(s1) and i < len(s2) and s1[i] == s2[i]:
        i += 1
    return s1[:i]


def _ru_pos(pos: Optional[str]) -> str:
    mapping = {
        "NOUN": "Существительное",
        "ADJF": "Прилагательное (полное)",
        "ADJS": "Прилагательное (краткое)",
        "VERB": "Глагол (личная форма)",
        "INFN": "Глагол (инфинитив)",
        "PRTF": "Глагол (причастие полное)",
        "PRTS": "Глагол (причастие краткое)",
        "GRND": "Глагол (деепричастие)",
        "NUMR": "Числительное",
        "ADVB": "Наречие",
        "NPRO": "Местоимение",
        "PREP": "Предлог",
        "CONJ": "Союз",
        "PRCL": "Частица",
        "INTJ": "Междометие",
    }
    return mapping.get(pos or "", pos or "Неизвестно")


def _tag_to_russian(tag) -> str:
    parts: list[str] = []

    pos_ru = _ru_pos(getattr(tag, "POS", None))
    if pos_ru:
        parts.append(pos_ru.lower())


    anim = getattr(tag, "animacy", None)
    if anim == "anim":
        parts.append("одушевлённое")
    elif anim == "inan":
        parts.append("неодушевлённое")

    gen = getattr(tag, "gender", None)
    if gen == "masc":
        parts.append("мужской род")
    elif gen == "femn":
        parts.append("женский род")
    elif gen == "neut":
        parts.append("средний род")

    num = getattr(tag, "number", None)
    if num == "sing":
        parts.append("единственное число")
    elif num == "plur":
        parts.append("множественное число")

    case = getattr(tag, "case", None)
    case_names = {
        "nomn": "именительный падеж",
        "gent": "родительный падеж",
        "datv": "дательный падеж",
        "accs": "винительный падеж",
        "ablt": "творительный падеж",
        "loct": "предложный падеж",
        "loc2": "предложный падеж",
    }
    if case in case_names:
        parts.append(case_names[case])

    tense = getattr(tag, "tense", None)
    if tense == "pres":
        parts.append("настоящее время")
    elif tense == "past":
        parts.append("прошедшее время")
    elif tense == "futr":
        parts.append("будущее время")

    person = getattr(tag, "person", None)
    if person == "1per":
        parts.append("1-е лицо")
    elif person == "2per":
        parts.append("2-е лицо")
    elif person == "3per":
        parts.append("3-е лицо")

    degree = getattr(tag, "degree", None)
    if degree == "comp":
        parts.append("сравнительная степень")
    elif degree == "supr":
        parts.append("превосходная степень")

    return ", ".join(parts)


def analyze_text_to_lexicon(text: str) -> LexiconStore:
    stats = analyze_text(text)
    morph = _get_morph()

    freq_by_lemma: Dict[str, int] = {}
    parses_by_lemma: Dict[str, Dict[str, Tuple[float, object]]] = {}

    for token in stats.tokens:
        parses = morph.parse(token)
        if not parses:
            continue
        for p in parses:
            lemma = p.normal_form
            pos_code = getattr(p.tag, "POS", None) or ""
            score = getattr(p, "score", 0.0) or 0.0

            freq_by_lemma[lemma] = freq_by_lemma.get(lemma, 0) + 1

            by_pos = parses_by_lemma.setdefault(lemma, {})
            old = by_pos.get(pos_code)
            if old is None or score > old[0]:
                by_pos[pos_code] = (score, p)

    pos_priority_default = ["VERB", "INFN", "NOUN", "ADJF", "ADJS", "PRTF", "PRTS", "GRND"]
    pos_priority_short = ["PREP", "CONJ", "PRCL", "INTJ", "ADVB", "NOUN", "VERB", "INFN", "ADJF", "ADJS", "PRTF", "PRTS", "GRND"]

    store = LexiconStore()

    for lemma, by_pos in parses_by_lemma.items():
        count = freq_by_lemma.get(lemma)
        if count is None:
            continue

        chosen_parse = None
        chosen_pos_code = ""
        priority = pos_priority_short if len(lemma) <= 2 else pos_priority_default
        for pos_code in priority:
            if pos_code in by_pos:
                chosen_pos_code = pos_code
                chosen_parse = by_pos[pos_code][1]
                break
        if chosen_parse is None:
            chosen_pos_code, (_, chosen_parse) = next(iter(by_pos.items()))

        parse = chosen_parse
        pos_code = chosen_pos_code
        pos = _ru_pos(pos_code)
        tag_str = _tag_to_russian(parse.tag)

        gender = getattr(parse.tag, "gender", None)
        gender_ru = {"masc": "м.р.", "femn": "ж.р.", "neut": "ср.р."}.get(gender, None)

        stem = lemma
        endings: Dict[str, str] = {}
        forms: Dict[str, str] = {}

        try:
            lexeme = list(parse.lexeme)
        except Exception:
            lexeme = []

        if lexeme:
            words = [x.word for x in lexeme if getattr(x, "word", None)]
            stem = _common_prefix(words) or lemma

            for form in lexeme:
                case = getattr(form.tag, "case", None)
                number = getattr(form.tag, "number", None)
                if not case or not number:
                    continue

                case_ru = CaseRu.from_pymorphy(case)
                number_ru = NumberRu.from_pymorphy(number)
                if not case_ru or not number_ru:
                    continue

                key = f"{case_ru.value}, {number_ru.value}"
                if key in forms:
                    continue
                word = form.word
                forms[key] = word
                endings[key] = word[len(stem) :] if word.startswith(stem) else ""

        entry = LexemeEntry(
            lemma=lemma,
            stem=stem,
            pos=pos,
            pos_code=pos_code or "",
            tag=tag_str,
            endings=endings,
            forms=forms,
            gender=gender_ru,
            frequency=count,
        )
        store.add_or_update_lexeme(entry)

    return store


def generate_noun_form(lemma: str, case_ru: CaseRu, number_ru: NumberRu) -> Optional[str]:
    morph = _get_morph()
    parses = morph.parse(lemma)
    if not parses:
        return None
    p = parses[0]

    case_map = {
        CaseRu.NOM: "nomn",
        CaseRu.GEN: "gent",
        CaseRu.DAT: "datv",
        CaseRu.ACC: "accs",
        CaseRu.INS: "ablt",
        CaseRu.PREP: "loct",
    }
    num_map = {NumberRu.SG: "sing", NumberRu.PL: "plur"}

    desired = {case_map[case_ru], num_map[number_ru]}
    inf = p.inflect(desired)
    return inf.word if inf else None


def generate_adjective_form(
    lemma: str,
    case_ru: CaseRu,
    number_ru: NumberRu,
    gender_ru: Optional[str],
) -> Optional[str]:

    morph = _get_morph()
    parses = morph.parse(lemma)
    if not parses:
        return None
    p = parses[0]

    case_map = {
        CaseRu.NOM: "nomn",
        CaseRu.GEN: "gent",
        CaseRu.DAT: "datv",
        CaseRu.ACC: "accs",
        CaseRu.INS: "ablt",
        CaseRu.PREP: "loct",
    }
    num_map = {NumberRu.SG: "sing", NumberRu.PL: "plur"}
    g_map = {"м.р.": "masc", "ж.р.": "femn", "ср.р.": "neut"}

    grammemes = {case_map[case_ru], num_map[number_ru]}
    g_code = g_map.get(gender_ru or "")
    if g_code:
        grammemes.add(g_code)

    inf = p.inflect(grammemes)
    return inf.word if inf else None


def generate_verb_form(
    lemma: str,
    *,
    tense: str,
    person: Optional[str],
    number_ru: Optional[NumberRu],
    gender_ru: Optional[str],
) -> Optional[str]:

    morph = _get_morph()
    parses = morph.parse(lemma)
    if not parses:
        return None
    p = parses[0]

    grammemes: set[str] = set()

    tense_map = {"Настоящее": "pres", "Прошедшее": "past", "Будущее": "futr"}
    t_code = tense_map.get(tense)
    if t_code:
        grammemes.add(t_code)

    if number_ru:
        num_map = {NumberRu.SG: "sing", NumberRu.PL: "plur"}
        n_code = num_map.get(number_ru)
        if n_code:
            grammemes.add(n_code)

    if t_code == "past":
        g_map = {"м.р.": "masc", "ж.р.": "femn", "ср.р.": "neut"}
        g_code = g_map.get(gender_ru or "")
        if g_code:
            grammemes.add(g_code)
    else:
        p_map = {"1": "1per", "2": "2per", "3": "3per"}
        per_code = p_map.get(person or "")
        if per_code:
            grammemes.add(per_code)

    if not grammemes:
        return None

    inf = p.inflect(grammemes)
    if inf:
        return inf.word

    if t_code == "futr":
        inf_parses = morph.parse(lemma)
        if not inf_parses:
            return None
        inf_lem = inf_parses[0].normal_form

        aux = None
        num = None
        if number_ru:
            num = {NumberRu.SG: "sg", NumberRu.PL: "pl"}.get(number_ru)

        if num == "sg":
            if person == "1":
                aux = "буду"
            elif person == "2":
                aux = "будешь"
            elif person == "3":
                aux = "будет"
        elif num == "pl":
            if person == "1":
                aux = "будем"
            elif person == "2":
                aux = "будете"
            elif person == "3":
                aux = "будут"

        if aux is None:
            aux = "буду"

        return f"{aux} {inf_lem}"

    return None

