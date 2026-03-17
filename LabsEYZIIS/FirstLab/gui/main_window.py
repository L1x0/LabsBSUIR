import tkinter as tk
from tkinter import filedialog, messagebox, ttk
from typing import Optional

from core.inflection import InflectionEngine
from core.models import ALL_CASES_RU, ALL_NUMBERS_RU, CaseRu, LexemeEntry, LexiconStore, NumberRu
from nlp.morphology import analyze_text_to_lexicon, generate_adjective_form, generate_verb_form
from nlp.text_processing import read_pdf_file, read_text_file
from storage.json_storage import export_markdown, load_lexicon_from_file, save_lexicon_to_file


class MainWindow:
    def __init__(self, root: tk.Tk) -> None:
        self.root = root
        self.lexicon = LexiconStore()
        self.engine = InflectionEngine(self.lexicon)
        self.current_lexeme: Optional[LexemeEntry] = None

        self._build_ui()
        self._refresh_lexeme_list()

    def _build_ui(self) -> None:
        self.root.title("Лексический словарь")
        self.root.geometry("1150x700")

        style = ttk.Style(self.root)
        if "clam" in style.theme_names():
            style.theme_use("clam")

        self._build_menu()

        main = ttk.Frame(self.root, padding=8)
        main.pack(fill="both", expand=True)

        left = ttk.Frame(main)
        left.pack(side="left", fill="y")
        right = ttk.Frame(main)
        right.pack(side="left", fill="both", expand=True, padx=(8, 0))

        search = ttk.Frame(left)
        search.pack(fill="x")
        ttk.Label(search, text="Поиск лексемы:").grid(row=0, column=0, sticky="w")
        self.search_var = tk.StringVar()
        ent = ttk.Entry(search, textvariable=self.search_var, width=18)
        ent.grid(row=0, column=1, sticky="w", padx=(4, 0))
        ent.bind("<KeyRelease>", lambda _e: self._refresh_lexeme_list())

        ttk.Label(search, text="Часть речи:").grid(row=1, column=0, sticky="w", pady=(4, 0))
        self.pos_filter_var = tk.StringVar(value="Все")
        ttk.Combobox(
            search,
            textvariable=self.pos_filter_var,
            values=["Все", "Существительные", "Прилагательные", "Глаголы", "Прочие"],
            state="readonly",
            width=18,
        ).grid(row=1, column=1, sticky="w", pady=(4, 0))
        self.pos_filter_var.trace_add("write", lambda *_: self._refresh_lexeme_list())

        list_frame = ttk.Frame(left)
        list_frame.pack(fill="both", expand=True, pady=(6, 0))
        self.tree = ttk.Treeview(
            list_frame,
            columns=("lemma", "freq"),
            show="headings",
            selectmode="browse",
            height=28,
        )
        self.tree.heading("lemma", text="Лексема")
        self.tree.heading("freq", text="Частота")
        self.tree.column("lemma", width=210, anchor="w")
        self.tree.column("freq", width=70, anchor="center")
        y = ttk.Scrollbar(list_frame, orient="vertical", command=self.tree.yview)
        self.tree.configure(yscrollcommand=y.set)
        self.tree.pack(side="left", fill="both", expand=True)
        y.pack(side="right", fill="y")
        self.tree.bind("<<TreeviewSelect>>", self._on_select)

        info = ttk.LabelFrame(right, text="Морфологическая информация")
        info.pack(fill="x")
        form = ttk.Frame(info)
        form.pack(fill="x", padx=6, pady=6)

        self.lemma_var = tk.StringVar()
        self.stem_var = tk.StringVar()
        self.pos_var = tk.StringVar()
        self.gender_var = tk.StringVar()
        self.freq_var = tk.StringVar()
        self.tag_var = tk.StringVar()

        ttk.Label(form, text="Лексема (лемма):").grid(row=0, column=0, sticky="w")
        ttk.Entry(form, textvariable=self.lemma_var, width=30, state="readonly").grid(
            row=0, column=1, sticky="w"
        )

        ttk.Label(form, text="Основа:").grid(row=1, column=0, sticky="w")
        ttk.Entry(form, textvariable=self.stem_var, width=30, state="readonly").grid(
            row=1, column=1, sticky="w"
        )

        ttk.Label(form, text="Часть речи:").grid(row=2, column=0, sticky="w")
        ttk.Entry(form, textvariable=self.pos_var, width=30, state="readonly").grid(
            row=2, column=1, sticky="w"
        )

        ttk.Label(form, text="Род:").grid(row=3, column=0, sticky="w")
        ttk.Entry(form, textvariable=self.gender_var, width=30, state="readonly").grid(
            row=3, column=1, sticky="w"
        )

        ttk.Label(form, text="Частота в тексте:").grid(row=4, column=0, sticky="w")
        ttk.Entry(form, textvariable=self.freq_var, width=10, state="readonly").grid(
            row=4, column=1, sticky="w"
        )

        ttk.Label(form, text="Морфологическая метка:").grid(row=5, column=0, sticky="w")
        ttk.Entry(form, textvariable=self.tag_var, width=70, state="readonly").grid(
            row=5, column=1, sticky="w"
        )

        gen = ttk.LabelFrame(right, text="Генерация словоформ")
        gen.pack(fill="both", expand=True, pady=(8, 0))

        top = ttk.Frame(gen)
        top.pack(fill="x", padx=6, pady=6)

        ttk.Label(top, text="Тип слова:").grid(row=0, column=0, sticky="w")
        self.gen_type_var = tk.StringVar(value="Существительное")
        type_cb = ttk.Combobox(
            top,
            textvariable=self.gen_type_var,
            values=["Существительное", "Прилагательное", "Глагол"],
            state="readonly",
            width=18,
        )
        type_cb.grid(row=0, column=1, sticky="w")

        self.case_label = ttk.Label(top, text="Падеж:")
        self.case_label.grid(row=1, column=0, sticky="w", pady=(6, 0))
        self.case_var = tk.StringVar(value=ALL_CASES_RU[0].value)
        self.case_cb = ttk.Combobox(
            top,
            textvariable=self.case_var,
            values=[c.value for c in ALL_CASES_RU],
            state="readonly",
            width=16,
        )
        self.case_cb.grid(row=1, column=1, sticky="w", pady=(6, 0))

        self.num_label = ttk.Label(top, text="Число:")
        self.num_label.grid(row=1, column=2, sticky="w", padx=(10, 0), pady=(6, 0))
        self.num_var = tk.StringVar(value=ALL_NUMBERS_RU[0].value)
        self.num_cb = ttk.Combobox(
            top,
            textvariable=self.num_var,
            values=[n.value for n in ALL_NUMBERS_RU],
            state="readonly",
            width=10,
        )
        self.num_cb.grid(row=1, column=3, sticky="w", pady=(6, 0))

        self.tense_label = ttk.Label(top, text="Время:")
        self.tense_label.grid(row=2, column=0, sticky="w", pady=(6, 0))
        self.tense_var = tk.StringVar(value="Настоящее")
        self.tense_cb = ttk.Combobox(
            top,
            textvariable=self.tense_var,
            values=["Настоящее", "Прошедшее", "Будущее"],
            state="readonly",
            width=18,
        )
        self.tense_cb.grid(row=2, column=1, sticky="w", pady=(6, 0))

        self.person_label = ttk.Label(top, text="Лицо:")
        self.person_label.grid(row=2, column=2, sticky="w", padx=(10, 0), pady=(6, 0))
        self.person_var = tk.StringVar(value="1-е лицо")
        self.person_cb = ttk.Combobox(
            top,
            textvariable=self.person_var,
            values=["1-е лицо", "2-е лицо", "3-е лицо"],
            state="readonly",
            width=12,
        )
        self.person_cb.grid(row=2, column=3, sticky="w", pady=(6, 0))

        self.verb_gender_label = ttk.Label(top, text="Род (прош. вр., ед.ч.):")
        self.verb_gender_label.grid(row=3, column=0, sticky="w", pady=(6, 0))
        self.verb_gender_var = tk.StringVar(value="м.р.")
        self.verb_gender_cb = ttk.Combobox(
            top,
            textvariable=self.verb_gender_var,
            values=["м.р.", "ж.р.", "ср.р."],
            state="readonly",
            width=10,
        )
        self.verb_gender_cb.grid(row=3, column=1, sticky="w", pady=(6, 0))

        ttk.Button(top, text="Сгенерировать", command=self._generate_one).grid(
            row=1, column=4, padx=(10, 0)
        )

        ttk.Label(top, text="Результат:").grid(row=4, column=0, sticky="w", pady=(6, 0))
        self.result_var = tk.StringVar()
        ttk.Entry(top, textvariable=self.result_var, width=40, state="readonly").grid(
            row=4, column=1, columnspan=4, sticky="w", pady=(6, 0)
        )

        self.text = tk.Text(gen, height=16)
        self.text.pack(fill="both", expand=True, padx=6, pady=(6, 6))

        ttk.Button(gen, text="Показать таблицу форм и окончаний", command=self._show_table).pack(
            anchor="w", padx=6, pady=(0, 6)
        )

        self.status_var = tk.StringVar(value="Откройте текст или PDF для анализа.")
        ttk.Label(self.root, textvariable=self.status_var, relief="sunken").pack(
            fill="x", side="bottom"
        )

        self.gen_type_var.trace_add("write", lambda *_: self._update_gen_controls())
        self._update_gen_controls()

    def _build_menu(self) -> None:
        bar = tk.Menu(self.root)

        m_file = tk.Menu(bar, tearoff=False)
        m_file.add_command(label="Открыть текст...", command=self._open_text)
        m_file.add_command(label="Открыть PDF...", command=self._open_pdf)
        m_file.add_separator()
        m_file.add_command(label="Загрузить словарь...", command=self._load_lexicon)
        m_file.add_command(label="Сохранить словарь...", command=self._save_lexicon)
        m_file.add_separator()
        m_file.add_command(label="Экспорт в Markdown...", command=self._export_md)
        m_file.add_separator()
        m_file.add_command(label="Выход", command=self.root.quit)
        bar.add_cascade(label="Файл", menu=m_file)

        m_help = tk.Menu(bar, tearoff=False)
        m_help.add_command(label="Справка", command=self._help)
        m_help.add_command(label="О программе", command=self._about)
        bar.add_cascade(label="Справка", menu=m_help)

        self.root.config(menu=bar)


    def _open_text(self) -> None:
        path = filedialog.askopenfilename(
            title="Открыть текстовый файл",
            filetypes=[("Текст", "*.txt"), ("Все файлы", "*.*")],
        )
        if not path:
            return
        try:
            text = read_text_file(path)
        except Exception as exc:
            messagebox.showerror("Ошибка", f"Не удалось прочитать файл:\n{exc}")
            return
        self._analyze(text)

    def _open_pdf(self) -> None:
        path = filedialog.askopenfilename(
            title="Открыть PDF",
            filetypes=[("PDF", "*.pdf"), ("Все файлы", "*.*")],
        )
        if not path:
            return
        try:
            text = read_pdf_file(path)
        except Exception as exc:
            messagebox.showerror(
                "Ошибка",
                "Не удалось извлечь текст из PDF.\n"
                "Проверьте, что установлена библиотека pdfplumber.\n\n"
                f"Детали:\n{exc}",
            )
            return
        self._analyze(text)

    def _analyze(self, text: str) -> None:
        try:
            self.lexicon = analyze_text_to_lexicon(text)
            self.engine.lexicon = self.lexicon
        except Exception as exc:
            messagebox.showerror("Ошибка", f"Не удалось выполнить морфологический разбор:\n{exc}")
            return
        count = sum(1 for _ in self.lexicon.iter_lexemes())
        self.status_var.set(f"Найдено лексем: {count}. Выберите лексему слева.")
        self._clear_card()
        self._refresh_lexeme_list()


    def _load_lexicon(self) -> None:
        path = filedialog.askopenfilename(
            title="Загрузить словарь (JSON)",
            filetypes=[("JSON", "*.json"), ("Все файлы", "*.*")],
        )
        if not path:
            return
        try:
            self.lexicon = load_lexicon_from_file(path)
            self.engine.lexicon = self.lexicon
        except Exception as exc:
            messagebox.showerror("Ошибка", f"Не удалось загрузить словарь:\n{exc}")
            return
        self.status_var.set("Словарь загружен.")
        self._clear_card()
        self._refresh_lexeme_list()

    def _save_lexicon(self) -> None:
        path = filedialog.asksaveasfilename(
            title="Сохранить словарь (JSON)",
            defaultextension=".json",
            filetypes=[("JSON", "*.json"), ("Все файлы", "*.*")],
        )
        if not path:
            return
        try:
            save_lexicon_to_file(self.lexicon, path)
        except Exception as exc:
            messagebox.showerror("Ошибка", f"Не удалось сохранить словарь:\n{exc}")
            return
        self.status_var.set("Словарь сохранён.")

    def _export_md(self) -> None:
        path = filedialog.asksaveasfilename(
            title="Экспорт в Markdown",
            defaultextension=".md",
            filetypes=[("Markdown", "*.md"), ("Все файлы", "*.*")],
        )
        if not path:
            return
        try:
            md = export_markdown(self.lexicon)
            with open(path, "w", encoding="utf-8") as f:
                f.write(md)
        except Exception as exc:
            messagebox.showerror("Ошибка", f"Не удалось экспортировать:\n{exc}")
            return
        self.status_var.set("Экспорт выполнен.")


    def _refresh_lexeme_list(self) -> None:
        prefix = self.search_var.get().strip()
        entries = self.lexicon.search_prefix(prefix)

        for iid in self.tree.get_children():
            self.tree.delete(iid)
        for e in entries:
            if not self._passes_pos_filter(e):
                continue
            freq = "" if e.frequency is None else str(e.frequency)
            self.tree.insert("", "end", values=(e.lemma, freq))

    def _passes_pos_filter(self, entry: LexemeEntry) -> bool:
        pf = self.pos_filter_var.get()
        code = (entry.pos_code or "").upper()
        if pf == "Все":
            return True
        if pf == "Существительные":
            return code == "NOUN"
        if pf == "Прилагательные":
            return code in {"ADJF", "ADJS"}
        if pf == "Глаголы":
            return code in {"VERB", "INFN", "PRTF", "PRTS", "GRND"}
        if pf == "Прочие":
            return code not in {"NOUN", "ADJF", "ADJS", "VERB", "INFN", "PRTF", "PRTS", "GRND"}
        return True

    def _on_select(self, _event: object) -> None:
        sel = self.tree.selection()
        if not sel:
            return
        lemma = self.tree.set(sel[0], "lemma")
        entry = self.lexicon.get_lexeme(lemma)
        if not entry:
            return
        self.current_lexeme = entry
        self._fill_card(entry)

    def _fill_card(self, e: LexemeEntry) -> None:
        self.lemma_var.set(e.lemma)
        self.stem_var.set(e.stem)
        self.pos_var.set(e.pos)
        self.gender_var.set(e.gender or "")
        self.freq_var.set("" if e.frequency is None else str(e.frequency))
        self.tag_var.set(e.tag)
        self.result_var.set("")
        self.text.delete("1.0", "end")
        self._auto_select_gen_type(e)

    def _clear_card(self) -> None:
        self.current_lexeme = None
        self.lemma_var.set("")
        self.stem_var.set("")
        self.pos_var.set("")
        self.gender_var.set("")
        self.freq_var.set("")
        self.tag_var.set("")
        self.result_var.set("")
        self.text.delete("1.0", "end")


    def _pos_group(self, entry: LexemeEntry) -> str:
        code = (entry.pos_code or "").upper()
        if code == "NOUN":
            return "Существительное"
        if code in {"ADJF", "ADJS"}:
            return "Прилагательное"
        if code in {"VERB", "INFN", "PRTF", "PRTS", "GRND"}:
            return "Глагол"
        return "Неизвестно"

    def _auto_select_gen_type(self, entry: LexemeEntry) -> None:
        g = self._pos_group(entry)
        if g in {"Существительное", "Прилагательное", "Глагол"}:
            if self.gen_type_var.get() != g:
                self.gen_type_var.set(g)

    def _is_gen_type_allowed(self, entry: LexemeEntry, gen_type: str) -> bool:
        return self._pos_group(entry) == gen_type

    def _update_gen_controls(self) -> None:
        kind = self.gen_type_var.get()
        if kind in ("Существительное", "Прилагательное"):
            self.case_label.grid()
            self.case_cb.grid()
            self.num_label.grid()
            self.num_cb.grid()
        else:
            self.case_label.grid_remove()
            self.case_cb.grid_remove()
            self.num_label.grid_remove()
            self.num_cb.grid_remove()

        if kind == "Глагол":
            self.tense_label.grid()
            self.tense_cb.grid()
            self.person_label.grid()
            self.person_cb.grid()
            self.verb_gender_label.grid()
            self.verb_gender_cb.grid()
        else:
            self.tense_label.grid_remove()
            self.tense_cb.grid_remove()
            self.person_label.grid_remove()
            self.person_cb.grid_remove()
            if kind == "Прилагательное":
                self.verb_gender_label.configure(text="Род:")
                self.verb_gender_label.grid()
                self.verb_gender_cb.grid()
            else:
                self.verb_gender_label.configure(text="Род (прош. вр., ед.ч.):")
                self.verb_gender_label.grid_remove()
                self.verb_gender_cb.grid_remove()

    def _generate_one(self) -> None:
        if not self.current_lexeme:
            messagebox.showwarning("Внимание", "Сначала выберите лексему слева.")
            return
        gen_type = self.gen_type_var.get()
        if not self._is_gen_type_allowed(self.current_lexeme, gen_type):
            messagebox.showinfo(
                "Ограничение",
                "«Перевод» между частями речи отключён, потому что это словообразование.\n"
                "Вы можете генерировать только словоформы внутри части речи выбранной лексемы.",
            )
            self._auto_select_gen_type(self.current_lexeme)
            return
        case = next((c for c in ALL_CASES_RU if c.value == self.case_var.get()), None)
        num = next((n for n in ALL_NUMBERS_RU if n.value == self.num_var.get()), None)
        if gen_type == "Существительное":
            if not case or not num:
                return
            w = self.engine.generate(self.current_lexeme, case, num)
            self.result_var.set(w or "<не удалось>")
        elif gen_type == "Прилагательное":
            if not case or not num:
                return
            gender_ru = self.verb_gender_var.get()
            w = generate_adjective_form(
                self.current_lexeme.lemma,
                case_ru=case,
                number_ru=num,
                gender_ru=gender_ru,
            )
            self.result_var.set(w or "<не удалось>")
        elif gen_type == "Глагол":
            tense = self.tense_var.get()
            person_label = self.person_var.get()
            person = None
            if "1-е" in person_label:
                person = "1"
            elif "2-е" in person_label:
                person = "2"
            elif "3-е" in person_label:
                person = "3"
            gender_ru = self.verb_gender_var.get()
            w = generate_verb_form(
                self.current_lexeme.lemma,
                tense=tense,
                person=person,
                number_ru=num,
                gender_ru=gender_ru,
            )
            self.result_var.set(w or "<не удалось>")

    def _show_table(self) -> None:
        if not self.current_lexeme:
            messagebox.showwarning("Внимание", "Сначала выберите лексему слева.")
            return
        kind = self.gen_type_var.get()
        if not self._is_gen_type_allowed(self.current_lexeme, kind):
            messagebox.showinfo(
                "Ограничение",
                "«Перевод» между частями речи отключён, потому что это словообразование.\n"
                "Таблица строится только для части речи выбранной лексемы.",
            )
            self._auto_select_gen_type(self.current_lexeme)
            kind = self.gen_type_var.get()
        self.text.delete("1.0", "end")

        if kind == "Существительное":
            self.text.insert("end", "Таблица форм и окончаний (падеж, число)\n\n")
            table = self.engine.generate_table(self.current_lexeme)
            for case in ALL_CASES_RU:
                for num in ALL_NUMBERS_RU:
                    key = f"{case.value}, {num.value}"
                    form = table.get(key) or "-"
                    ending = self.current_lexeme.endings.get(key, "")
                    self.text.insert("end", f"{key}: {form}    (окончание: {ending!r})\n")
        elif kind == "Прилагательное":
            self.text.insert("end", "Таблица форм прилагательного (с учётом выбранного рода)\n\n")
            gender_ru = self.verb_gender_var.get()
            for case in ALL_CASES_RU:
                for num in ALL_NUMBERS_RU:
                    key = f"{case.value}, {num.value}, род={gender_ru}"
                    form = generate_adjective_form(
                        self.current_lexeme.lemma,
                        case_ru=case,
                        number_ru=num,
                        gender_ru=gender_ru,
                    ) or "-"
                    self.text.insert("end", f"{key}: {form}\n")
        elif kind == "Глагол":
            self.text.insert("end", "Формы глагола (с учётом выбранных параметров)\n\n")
            tense = self.tense_var.get()
            gender_ru = self.verb_gender_var.get()
            for num in ALL_NUMBERS_RU:
                for person_label, person in (("1-е лицо", "1"), ("2-е лицо", "2"), ("3-е лицо", "3")):
                    form = generate_verb_form(
                        self.current_lexeme.lemma,
                        tense=tense,
                        person=person,
                        number_ru=num,
                        gender_ru=gender_ru,
                    ) or "-"
                    key = f"{tense}, {num.value}, {person_label}, род={gender_ru}"
                    self.text.insert("end", f"{key}: {form}\n")


    def _help(self) -> None:
        messagebox.showinfo(
            "Справка",
            "1) Откройте текст или PDF.\n"
            "2) Программа автоматически выделит лексемы (леммы) и выполнит морфоразбор.\n"
            "3) Выберите лексему слева, чтобы увидеть морфологическую информацию.\n"
            "4) Для существительных можно генерировать словоформы по падежу и числу.\n"
            "5) Словарь можно сохранить/загрузить (JSON) и экспортировать (Markdown).\n",
        )

    def _about(self) -> None:
        messagebox.showinfo(
            "О программе",
            "Лабораторная работа: анализ текста (русский язык),\n"
            "выделение лексем и морфологический разбор,\n"
            "генерация словоформ для существительных.",
        )


def run_app() -> None:
    root = tk.Tk()
    MainWindow(root)
    root.mainloop()

