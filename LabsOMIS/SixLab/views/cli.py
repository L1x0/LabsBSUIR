"""CLI интерфейс системы"""
from typing import Optional
from controllers.website_generator import WebsiteGenerator
from controllers.analyzer import WebsiteAnalyzer
from controllers.decision_support import DecisionSupport
from controllers.monitoring import MonitoringController
from infrastructure.storage import DataStorage
from infrastructure.ai_client import OllamaClient


class CLIInterface:
    """Консольный интерфейс системы"""
    
    def __init__(self):
        self.storage = DataStorage()
        self.ai_client = OllamaClient()
        self.generator = WebsiteGenerator(self.storage, self.ai_client)
        self.analyzer = WebsiteAnalyzer(self.storage)
        self.decision_support = DecisionSupport(self.storage, self.ai_client)
        self.monitoring = MonitoringController(self.storage)
        self.current_user_id: Optional[int] = None
    
    def run(self):
        """Запуск интерфейса"""
        print("=" * 60)
        print("Интеллектуальная система автоматического создания")
        print("и оптимизации веб-сайтов")
        print("=" * 60)
        print()
        
        # Проверка доступности AI
        if self.ai_client.is_available():
            print("✓ Ollama (gemma3) доступен")
        else:
            print("⚠ Ollama (gemma3) недоступен, AI-функции будут ограничены")
        print()
        
        # Авторизация/регистрация пользователя
        self._authenticate()
        
        # Главное меню
        while True:
            self._show_main_menu()
            choice = input("\nВыберите действие: ").strip()
            
            if choice == "1":
                self._create_website()
            elif choice == "2":
                self._list_websites()
            elif choice == "3":
                self._analyze_website()
            elif choice == "4":
                self._show_recommendations()
            elif choice == "5":
                self._apply_recommendation()
            elif choice == "6":
                self._export_website()
            elif choice == "7":
                self._show_monitoring()
            elif choice == "8":
                self._ab_testing()
            elif choice == "0":
                print("\nДо свидания!")
                break
            else:
                print("\nНеверный выбор. Попробуйте снова.")
    
    def _authenticate(self):
        """Авторизация пользователя"""
        print("Вход в систему")
        print("-" * 60)
        username = input("Введите имя пользователя: ").strip()
        
        if not username:
            username = "user_" + str(len(self.storage.get_all_users()) + 1)
        
        # Простая регистрация/авторизация
        users = self.storage.get_all_users()
        user = None
        for u in users:
            if u.get("username") == username:
                user = u
                break
        
        if not user:
            user_data = {
                "username": username,
                "email": "",
                "user_type": "customer",
                "created_at": ""
            }
            self.current_user_id = self.storage.save_user(user_data)
            print(f"\n✓ Новый пользователь создан: {username}")
        else:
            self.current_user_id = user.get("id")
            print(f"\n✓ Добро пожаловать, {username}!")
        
        print()
    
    def _show_main_menu(self):
        """Отображение главного меню (согласно макету из ЛР5)"""
        print("\n" + "=" * 60)
        print("ГЛАВНОЕ МЕНЮ")
        print("=" * 60)
        print("1. Создать новый веб-сайт")
        print("2. Список моих веб-сайтов")
        print("3. Анализировать веб-сайт")
        print("4. Просмотреть рекомендации")
        print("5. Применить рекомендацию")
        print("6. Экспортировать веб-сайт")
        print("7. Мониторинг и аналитика")
        print("8. A/B тестирование")
        print("0. Выход")
    
    def _create_website(self):
        """Создание нового веб-сайта"""
        print("\n" + "=" * 60)
        print("СОЗДАНИЕ НОВОГО ВЕБ-САЙТА")
        print("=" * 60)
        
        name = input("Название сайта: ").strip()
        if not name:
            print("Ошибка: название обязательно")
            return
        
        print("\nТип сайта:")
        print("1. Корпоративный сайт")
        print("2. Интернет-магазин")
        print("3. Блог")
        
        type_choice = input("Выберите тип (1-3): ").strip()
        type_map = {"1": "corporate", "2": "ecommerce", "3": "blog"}
        website_type = type_map.get(type_choice, "corporate")
        
        description = input("Описание сайта (необязательно): ").strip()
        target_audience = input("Целевая аудитория (необязательно): ").strip()
        
        try:
            website = self.generator.generate_website(
                self.current_user_id,
                name,
                website_type,
                description,
                target_audience
            )
            print(f"\n✓ Веб-сайт '{name}' успешно создан!")
            print(f"  ID: {website['id']}")
            print(f"  URL: {website['url']}")
        except Exception as e:
            print(f"\n✗ Ошибка при создании сайта: {e}")
    
    def _list_websites(self):
        """Список веб-сайтов пользователя"""
        print("\n" + "=" * 60)
        print("МОИ ВЕБ-САЙТЫ")
        print("=" * 60)
        
        websites = self.storage.get_websites_by_user(self.current_user_id)
        
        if not websites:
            print("У вас пока нет веб-сайтов.")
            return
        
        for i, website in enumerate(websites, 1):
            print(f"\n{i}. {website.get('name')}")
            print(f"   ID: {website.get('id')}")
            print(f"   Тип: {website.get('website_type')}")
            print(f"   URL: {website.get('url')}")
            if website.get('description'):
                print(f"   Описание: {website.get('description')[:50]}...")
    
    def _analyze_website(self):
        """Анализ веб-сайта"""
        print("\n" + "=" * 60)
        print("АНАЛИЗ ВЕБ-САЙТА")
        print("=" * 60)
        
        website_id = input("Введите ID веб-сайта: ").strip()
        
        try:
            website_id = int(website_id)
        except ValueError:
            print("Ошибка: неверный ID")
            return
        
        try:
            print("\nВыполняется анализ...")
            analysis = self.analyzer.analyze_website(website_id)
            
            print("\n" + "-" * 60)
            print("РЕЗУЛЬТАТЫ АНАЛИЗА")
            print("-" * 60)
            print(f"Производительность: {analysis['performance_score']:.1f}/100")
            print(f"UX: {analysis['ux_score']:.1f}/100")
            print(f"SEO: {analysis['seo_score']:.1f}/100")
            print(f"Конверсия: {analysis['conversion_score']:.1f}/100")
            
            if analysis.get('issues'):
                print("\nВыявленные проблемы:")
                for issue in analysis['issues']:
                    print(f"  • {issue}")
            
            # Генерируем рекомендации
            print("\nГенерация рекомендаций...")
            recommendations = self.decision_support.generate_recommendations(website_id, analysis)
            print(f"✓ Создано рекомендаций: {len(recommendations)}")
            
        except Exception as e:
            print(f"\n✗ Ошибка при анализе: {e}")
    
    def _show_recommendations(self):
        """Просмотр рекомендаций"""
        print("\n" + "=" * 60)
        print("РЕКОМЕНДАЦИИ")
        print("=" * 60)
        
        website_id = input("Введите ID веб-сайта: ").strip()
        
        try:
            website_id = int(website_id)
        except ValueError:
            print("Ошибка: неверный ID")
            return
        
        recommendations = self.decision_support.get_recommendations_for_website(website_id)
        
        if not recommendations:
            print("Нет доступных рекомендаций для этого сайта.")
            return
        
        print(f"\nНайдено рекомендаций: {len(recommendations)}\n")
        
        for i, rec in enumerate(recommendations, 1):
            print(f"{i}. {rec.get('title')}")
            print(f"   Тип: {rec.get('optimization_type')}")
            print(f"   Приоритет: {rec.get('priority')}/5")
            print(f"   Описание: {rec.get('description')}")
            print(f"   Влияние: {rec.get('estimated_impact')}")
            print(f"   ID: {rec.get('id')}")
            print()
    
    def _apply_recommendation(self):
        """Применение рекомендации"""
        print("\n" + "=" * 60)
        print("ПРИМЕНЕНИЕ РЕКОМЕНДАЦИИ")
        print("=" * 60)
        
        rec_id = input("Введите ID рекомендации: ").strip()
        
        try:
            rec_id = int(rec_id)
        except ValueError:
            print("Ошибка: неверный ID")
            return
        
        try:
            success = self.decision_support.apply_recommendation(rec_id)
            if success:
                print("\n✓ Рекомендация успешно применена!")
            else:
                print("\n✗ Не удалось применить рекомендацию")
        except Exception as e:
            print(f"\n✗ Ошибка: {e}")
    
    def _export_website(self):
        """Экспорт веб-сайта"""
        print("\n" + "=" * 60)
        print("ЭКСПОРТ ВЕБ-САЙТА")
        print("=" * 60)
        
        website_id = input("Введите ID веб-сайта: ").strip()
        
        try:
            website_id = int(website_id)
        except ValueError:
            print("Ошибка: неверный ID")
            return
        
        website = self.storage.get_website(website_id)
        
        if not website:
            print("Веб-сайт не найден")
            return
        
        # Создаем директорию для экспорта
        import os
        from pathlib import Path
        
        export_dir = Path("exports") / f"website_{website_id}"
        export_dir.mkdir(parents=True, exist_ok=True)
        
        # Сохраняем HTML
        html_file = export_dir / "index.html"
        content = website.get("content", {})
        html_content = content.get("html", "")
        html_content = html_content.replace("{{name}}", website.get("name", ""))
        html_content = html_content.replace("{{description}}", website.get("description", ""))
        
        with open(html_file, 'w', encoding='utf-8') as f:
            f.write(html_content)
        
        # Сохраняем CSS
        css_file = export_dir / "style.css"
        css_content = content.get("css", "")
        with open(css_file, 'w', encoding='utf-8') as f:
            f.write(css_content)
        
        print(f"\n✓ Веб-сайт экспортирован в: {export_dir}")
        print(f"  HTML: {html_file}")
        print(f"  CSS: {css_file}")
    
    def _show_monitoring(self):
        """Страница мониторинга и аналитики (согласно макету из ЛР5)"""
        print("\n" + "=" * 60)
        print("МОНИТОРИНГ И АНАЛИТИКА")
        print("=" * 60)
        
        print("\n1. Общая аналитика")
        print("2. Метрики конкретного сайта")
        print("3. Поведенческие метрики")
        print("0. Назад")
        
        choice = input("\nВыберите опцию: ").strip()
        
        if choice == "1":
            self._show_general_analytics()
        elif choice == "2":
            self._show_website_metrics()
        elif choice == "3":
            self._show_behavioral_metrics()
        elif choice == "0":
            return
        else:
            print("Неверный выбор")
    
    def _show_general_analytics(self):
        """Общая аналитика"""
        print("\n" + "-" * 60)
        print("ОБЩАЯ АНАЛИТИКА")
        print("-" * 60)
        
        report = self.monitoring.get_analytics_report(self.current_user_id)
        
        print(f"\nВсего веб-сайтов: {report['total_websites']}")
        print(f"Всего анализов: {report['total_analyses']}")
        print(f"Всего рекомендаций: {report['total_recommendations']}")
        print(f"  • Применено: {report['applied_recommendations']}")
        print(f"  • Ожидают применения: {report['pending_recommendations']}")
        
        print("\nСредние показатели:")
        avg = report['average_scores']
        print(f"  • Производительность: {avg['performance']}/100")
        print(f"  • UX: {avg['ux']}/100")
        print(f"  • SEO: {avg['seo']}/100")
        print(f"  • Конверсия: {avg['conversion']}/100")
        
        if report['website_types']:
            print("\nРаспределение по типам сайтов:")
            for w_type, count in report['website_types'].items():
                print(f"  • {w_type}: {count}")
        
        if report['recent_analyses']:
            print("\nПоследние анализы:")
            for analysis in report['recent_analyses'][:3]:
                print(f"  • Сайт ID {analysis.get('website_id')} - {analysis.get('analysis_date', '')[:10]}")
    
    def _show_website_metrics(self):
        """Метрики конкретного сайта"""
        website_id = input("\nВведите ID веб-сайта: ").strip()
        
        try:
            website_id = int(website_id)
        except ValueError:
            print("Ошибка: неверный ID")
            return
        
        metrics = self.monitoring.get_website_metrics(website_id)
        
        if not metrics:
            print("Веб-сайт не найден")
            return
        
        print("\n" + "-" * 60)
        print(f"МЕТРИКИ: {metrics['website']['name']}")
        print("-" * 60)
        print(f"Тип: {metrics['website']['type']}")
        print(f"Создан: {metrics['website']['created_at']}")
        print(f"Всего анализов: {metrics['total_analyses']}")
        print(f"Всего рекомендаций: {metrics['total_recommendations']}")
        
        if metrics['latest_analysis']:
            print("\nПоследний анализ:")
            la = metrics['latest_analysis']
            print(f"  • Производительность: {la.get('performance_score', 0):.1f}/100")
            print(f"  • UX: {la.get('ux_score', 0):.1f}/100")
            print(f"  • SEO: {la.get('seo_score', 0):.1f}/100")
            print(f"  • Конверсия: {la.get('conversion_score', 0):.1f}/100")
    
    def _show_behavioral_metrics(self):
        """Поведенческие метрики"""
        website_id = input("\nВведите ID веб-сайта: ").strip()
        
        try:
            website_id = int(website_id)
        except ValueError:
            print("Ошибка: неверный ID")
            return
        
        metrics = self.monitoring.get_behavioral_metrics(website_id)
        
        print("\n" + "-" * 60)
        print("ПОВЕДЕНЧЕСКИЕ МЕТРИКИ")
        print("-" * 60)
        print(f"Просмотры страниц: {metrics['page_views']}")
        print(f"Уникальные посетители: {metrics['unique_visitors']}")
        print(f"Показатель отказов: {metrics['bounce_rate']}%")
        print(f"Средняя длительность сессии: {metrics['avg_session_duration']} сек")
        print(f"Конверсия: {metrics['conversion_rate']}%")
        print(f"\nПримечание: {metrics['note']}")
    
    def _ab_testing(self):
        """A/B тестирование (согласно сценарию из документации)"""
        print("\n" + "=" * 60)
        print("A/B ТЕСТИРОВАНИЕ")
        print("=" * 60)
        
        website_id = input("Введите ID веб-сайта: ").strip()
        
        try:
            website_id = int(website_id)
        except ValueError:
            print("Ошибка: неверный ID")
            return
        
        element = input("Какой элемент хотите протестировать? (например: заголовок, кнопка, форма): ").strip()
        
        if not element:
            element = "элемент интерфейса"
        
        test_suggestion = self.decision_support.suggest_ab_test(website_id, element)
        
        print("\n" + "-" * 60)
        print("ПРЕДЛОЖЕНИЕ A/B ТЕСТА")
        print("-" * 60)
        print(f"Элемент: {test_suggestion['element']}")
        print(f"Вариант A: {test_suggestion['variant_a']}")
        print(f"Вариант B: {test_suggestion['variant_b']}")
        print(f"Описание: {test_suggestion['description']}")
        print(f"Ожидаемое улучшение: {test_suggestion['estimated_improvement']}")
        print("\nПримечание: Для полной реализации A/B тестирования требуется")
        print("интеграция с системой аналитики и механизм разделения трафика.")

