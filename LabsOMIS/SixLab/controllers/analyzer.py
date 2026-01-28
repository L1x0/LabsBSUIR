
import re
from typing import Dict, List
from datetime import datetime
from models.analysis import AnalysisResult, OptimizationType
from infrastructure.storage import DataStorage


class WebsiteAnalyzer:
    def __init__(self, storage: DataStorage):
        self.storage = storage
    
    def analyze_website(self, website_id: int) -> Dict:
        website = self.storage.get_website(website_id)
        if not website:
            raise ValueError(f"Веб-сайт с ID {website_id} не найден")
        
        content = website.get("content", {})
        html = content.get("html", "")
        css = content.get("css", "")

        performance_score = self._analyze_performance(html, css)

        ux_score = self._analyze_ux(html, css)

        seo_score = self._analyze_seo(html)
        
        conversion_score = self._analyze_conversion(html)
        
        issues = self._identify_issues(html, css, performance_score, ux_score, seo_score)
        
        recommendations = self._generate_recommendations(html, css, performance_score,
                                                         ux_score, seo_score, conversion_score)
        
        analysis_result = {
            "website_id": website_id,
            "analysis_date": datetime.now().isoformat(),
            "performance_score": performance_score,
            "ux_score": ux_score,
            "seo_score": seo_score,
            "conversion_score": conversion_score,
            "issues": issues,
            "recommendations": recommendations
        }
        
        self.storage.save_analysis(analysis_result)
        
        return analysis_result
    
    def _analyze_performance(self, html: str, css: str) -> float:
        score = 100.0
        
        html_size = len(html)
        if html_size > 100000:
            score -= 20
        elif html_size > 50000:
            score -= 10
        
        css_size = len(css)
        if css_size > 50000:
            score -= 15
        elif css_size > 25000:
            score -= 7
        
        inline_styles = len(re.findall(r'style\s*=', html))
        if inline_styles > 10:
            score -= 10
        
        images = len(re.findall(r'<img', html, re.IGNORECASE))
        if images > 0:
            score -= 5
        
        return max(0.0, min(100.0, score))
    
    def _analyze_ux(self, html: str, css: str) -> float:
        score = 50.0
        
        if re.search(r'<nav', html, re.IGNORECASE):
            score += 15
        
        if re.search(r'@media', css, re.IGNORECASE):
            score += 15
        
        if re.search(r'<form', html, re.IGNORECASE):
            score += 10
        
        h_tags = len(re.findall(r'<h[1-6]', html, re.IGNORECASE))
        if h_tags >= 3:
            score += 10
        
        if re.search(r'<footer', html, re.IGNORECASE):
            score += 5
        
        return min(100.0, score)
    
    def _analyze_seo(self, html: str) -> float:
        score = 0.0
        
        if re.search(r'<title>', html, re.IGNORECASE):
            score += 20
        
        if re.search(r'<meta.*description', html, re.IGNORECASE):
            score += 20
        
        if re.search(r'<meta.*keywords', html, re.IGNORECASE):
            score += 10
        
        h1_count = len(re.findall(r'<h1', html, re.IGNORECASE))
        if h1_count == 1:
            score += 15
        elif h1_count > 1:
            score += 5
        
        img_tags = len(re.findall(r'<img', html, re.IGNORECASE))
        alt_attrs = len(re.findall(r'alt\s*=', html, re.IGNORECASE))
        if img_tags > 0:
            alt_ratio = alt_attrs / img_tags if img_tags > 0 else 0
            score += alt_ratio * 20
        
        semantic_tags = ['<header', '<nav', '<main', '<article', '<section', '<footer']
        for tag in semantic_tags:
            if re.search(tag, html, re.IGNORECASE):
                score += 3
        
        return min(100.0, score)
    
    def _analyze_conversion(self, html: str) -> float:
        score = 30.0
        
        forms = len(re.findall(r'<form', html, re.IGNORECASE))
        if forms > 0:
            score += 20
        
        buttons = len(re.findall(r'<button', html, re.IGNORECASE))
        if buttons > 0:
            score += 15
        
        links = len(re.findall(r'<a\s+href', html, re.IGNORECASE))
        if links > 5:
            score += 15
        
        if re.search(r'contact|контакт|телефон|email', html, re.IGNORECASE):
            score += 20
        
        return min(100.0, score)
    
    def _identify_issues(self, html: str, css: str, perf_score: float, 
                        ux_score: float, seo_score: float) -> List[str]:
        issues = []
        
        if perf_score < 70:
            issues.append("Низкая производительность: большой размер файлов или неоптимизированный код")
        
        if ux_score < 60:
            issues.append("Проблемы с пользовательским опытом: отсутствие адаптивности или навигации")
        
        if seo_score < 50:
            issues.append("Плохая SEO-оптимизация: отсутствуют мета-теги или структурированные данные")
        
        if not re.search(r'@media', css, re.IGNORECASE):
            issues.append("Отсутствует адаптивный дизайн (нет media queries)")
        
        if len(re.findall(r'<h1', html, re.IGNORECASE)) != 1:
            issues.append("Неправильное использование заголовков H1 (должен быть один)")
        
        return issues
    
    def _generate_recommendations(self, html: str, css: str, perf_score: float,
                                 ux_score: float, seo_score: float, conv_score: float) -> List[str]:
        recommendations = []
        
        if perf_score < 80:
            recommendations.append("Оптимизировать размер HTML и CSS файлов")
            recommendations.append("Использовать минификацию кода")
            recommendations.append("Оптимизировать изображения")
        
        if ux_score < 70:
            recommendations.append("Добавить адаптивный дизайн для мобильных устройств")
            recommendations.append("Улучшить навигацию сайта")
        
        if seo_score < 60:
            recommendations.append("Добавить мета-теги (description, keywords)")
            recommendations.append("Улучшить структуру заголовков (H1-H6)")
            recommendations.append("Добавить alt-атрибуты к изображениям")
        
        if conv_score < 60:
            recommendations.append("Добавить формы обратной связи")
            recommendations.append("Улучшить призывы к действию (CTA)")
        
        return recommendations

