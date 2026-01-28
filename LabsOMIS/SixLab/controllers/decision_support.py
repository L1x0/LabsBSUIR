from typing import Dict, List
from datetime import datetime
from models.analysis import OptimizationType, OptimizationRecommendation
from infrastructure.storage import DataStorage
from infrastructure.ai_client import OllamaClient


class DecisionSupport:

    def __init__(self, storage: DataStorage, ai_client: OllamaClient = None):
        self.storage = storage
        self.ai_client = ai_client or OllamaClient()
    
    def generate_recommendations(self, website_id: int, analysis_result: Dict) -> List[Dict]:
        recommendations = []
        
        if analysis_result.get("performance_score", 100) < 80:
            rec = self._create_recommendation(
                website_id,
                OptimizationType.PERFORMANCE,
                "Оптимизация производительности",
                "Рекомендуется минифицировать код, оптимизировать изображения и использовать кэширование",
                5
            )
            recommendations.append(rec)
        
        if analysis_result.get("ux_score", 100) < 70:
            rec = self._create_recommendation(
                website_id,
                OptimizationType.UX,
                "Улучшение пользовательского опыта",
                "Добавить адаптивный дизайн, улучшить навигацию и структуру страницы",
                4
            )
            recommendations.append(rec)
        
        if analysis_result.get("seo_score", 100) < 60:
            rec = self._create_recommendation(
                website_id,
                OptimizationType.SEO,
                "SEO-оптимизация",
                "Добавить мета-теги, улучшить структуру заголовков и добавить alt-атрибуты",
                5
            )
            recommendations.append(rec)
        
        if analysis_result.get("conversion_score", 100) < 60:
            rec = self._create_recommendation(
                website_id,
                OptimizationType.CONVERSION,
                "Повышение конверсии",
                "Добавить формы обратной связи, улучшить призывы к действию и контактную информацию",
                4
            )
            recommendations.append(rec)
        
        if self.ai_client and self.ai_client.is_available():
            ai_recommendations = self._generate_ai_recommendations(website_id, analysis_result)
            recommendations.extend(ai_recommendations)
        
        existing_recommendations = self.storage.get_recommendations_by_website(website_id)
        unique_recommendations = self._filter_duplicates(recommendations, existing_recommendations)
        
        saved_count = 0
        for rec in unique_recommendations:
            self.storage.save_recommendation(rec)
            saved_count += 1
        
        return unique_recommendations
    
    def _create_recommendation(self, website_id: int, opt_type: OptimizationType,
                              title: str, description: str, priority: int) -> Dict:
        return {
            "website_id": website_id,
            "optimization_type": opt_type.value,
            "title": title,
            "description": description,
            "priority": priority,
            "estimated_impact": self._estimate_impact(opt_type),
            "applied": False
        }
    
    def _estimate_impact(self, opt_type: OptimizationType) -> str:
        impacts = {
            OptimizationType.PERFORMANCE: "Высокое влияние на скорость загрузки",
            OptimizationType.UX: "Среднее влияние на удобство использования",
            OptimizationType.SEO: "Высокое влияние на поисковую видимость",
            OptimizationType.CONVERSION: "Среднее влияние на конверсию"
        }
        return impacts.get(opt_type, "Среднее влияние")
    
    def _filter_duplicates(self, new_recommendations: List[Dict], existing_recommendations: List[Dict]) -> List[Dict]:
        unique_recommendations = []
        
        for new_rec in new_recommendations:
            is_duplicate = False
            
            for existing_rec in existing_recommendations:
                if (new_rec.get('optimization_type') == existing_rec.get('optimization_type') and
                    self._are_similar(new_rec.get('description', ''), existing_rec.get('description', ''))):
                    is_duplicate = True
                    break
            
            if not is_duplicate:
                unique_recommendations.append(new_rec)
        
        return unique_recommendations
    
    def _are_similar(self, text1: str, text2: str, threshold: float = 0.7) -> bool:
        if not text1 or not text2:
            return False
        
        text1_lower = text1.lower().strip()
        text2_lower = text2.lower().strip()
        
        if text1_lower == text2_lower:
            return True
        
        if len(text1_lower) < 50 or len(text2_lower) < 50:
            if text1_lower in text2_lower or text2_lower in text1_lower:
                return True
        
        words1 = set(text1_lower.split())
        words2 = set(text2_lower.split())
        
        if len(words1) == 0 or len(words2) == 0:
            return False
        
        intersection = len(words1 & words2)
        union = len(words1 | words2)
        
        if union == 0:
            return False
        
        similarity = intersection / union
        
        return similarity >= threshold
    
    def _generate_ai_recommendations(self, website_id: int, analysis_result: Dict) -> List[Dict]:
        recommendations = []
        
        try:
            prompt = f"""Проанализируй результаты анализа веб-сайта и предложи 1-2 конкретные рекомендации по улучшению.
Оценки:
- Производительность: {analysis_result.get('performance_score', 0)}/100
- UX: {analysis_result.get('ux_score', 0)}/100
- SEO: {analysis_result.get('seo_score', 0)}/100
- Конверсия: {analysis_result.get('conversion_score', 0)}/100

Проблемы: {', '.join(analysis_result.get('issues', []))}

Верни только рекомендации, по одной на строку, без дополнительных комментариев."""
            
            ai_response = self.ai_client.generate(prompt)
            
            ai_recs = [line.strip() for line in ai_response.split('\n') if line.strip()]
            for rec_text in ai_recs[:2]:
                if rec_text and len(rec_text) > 10:
                    recommendations.append({
                        "website_id": website_id,
                        "optimization_type": "general",
                        "title": "AI-рекомендация",
                        "description": rec_text,
                        "priority": 3,
                        "estimated_impact": "Требует оценки",
                        "applied": False
                    })
        except Exception as e:
            print(f"Ошибка при генерации AI-рекомендаций: {e}")
        
        return recommendations
    
    def apply_recommendation(self, recommendation_id: int) -> bool:
        all_recommendations = self.storage._load_data("recommendations.json")
        recommendation = None
        
        for rec in all_recommendations:
            if rec.get("id") == recommendation_id:
                recommendation = rec
                break
        
        if not recommendation or recommendation.get("applied"):
            return False
        
        website_id = recommendation.get("website_id")
        website = self.storage.get_website(website_id)
        
        if not website:
            return False
        
        opt_type = recommendation.get("optimization_type")
        content = website.get("content", {})
        html = content.get("html", "")
        css = content.get("css", "")
        
        if opt_type == "seo":
            if "<meta" not in html.lower() or "description" not in html.lower():
                meta_tags = '<meta name="description" content="Описание сайта">\n    <meta name="keywords" content="ключевые слова">'
                html = html.replace("<head>", f"<head>\n    {meta_tags}")
                content["html"] = html
                website["content"] = content
                self.storage.update_website(website_id, website)
        
        elif opt_type == "performance":
            pass
        
        self.storage.mark_recommendation_applied(recommendation_id)
        
        return True
    
    def get_recommendations_for_website(self, website_id: int) -> List[Dict]:
        return self.storage.get_recommendations_by_website(website_id)
    
    def suggest_ab_test(self, website_id: int, element: str) -> Dict:

        return {
            "website_id": website_id,
            "element": element,
            "variant_a": "Текущая версия",
            "variant_b": "Оптимизированная версия",
            "description": f"Предлагается провести A/B тест для элемента: {element}",
            "estimated_improvement": "10-15% улучшение конверсии"
        }

