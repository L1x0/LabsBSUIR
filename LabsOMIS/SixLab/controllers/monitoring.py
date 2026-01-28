from typing import Dict, List
from datetime import datetime
from infrastructure.storage import DataStorage


class MonitoringController:
    def __init__(self, storage: DataStorage):
        self.storage = storage
    
    def get_analytics_report(self, user_id: int = None) -> Dict:
        websites = self.storage.get_websites_by_user(user_id) if user_id else self.storage.get_all_websites()
        all_analyses = self.storage._load_data("analyses.json")
        all_recommendations = self.storage._load_data("recommendations.json")
        
        website_ids = [w.get('id') for w in websites]
        user_analyses = [a for a in all_analyses if a.get('website_id') in website_ids]
        user_recommendations = [r for r in all_recommendations if r.get('website_id') in website_ids]
        
        if user_analyses:
            avg_performance = sum(a.get('performance_score', 0) for a in user_analyses) / len(user_analyses)
            avg_ux = sum(a.get('ux_score', 0) for a in user_analyses) / len(user_analyses)
            avg_seo = sum(a.get('seo_score', 0) for a in user_analyses) / len(user_analyses)
            avg_conversion = sum(a.get('conversion_score', 0) for a in user_analyses) / len(user_analyses)
        else:
            avg_performance = avg_ux = avg_seo = avg_conversion = 0.0
        
        applied_recommendations = len([r for r in user_recommendations if r.get('applied', False)])
        pending_recommendations = len([r for r in user_recommendations if not r.get('applied', False)])
        
        type_stats = {}
        for website in websites:
            w_type = website.get('website_type', 'unknown')
            type_stats[w_type] = type_stats.get(w_type, 0) + 1
        
        return {
            "total_websites": len(websites),
            "total_analyses": len(user_analyses),
            "total_recommendations": len(user_recommendations),
            "applied_recommendations": applied_recommendations,
            "pending_recommendations": pending_recommendations,
            "average_scores": {
                "performance": round(avg_performance, 2),
                "ux": round(avg_ux, 2),
                "seo": round(avg_seo, 2),
                "conversion": round(avg_conversion, 2)
            },
            "website_types": type_stats,
            "recent_analyses": sorted(user_analyses, key=lambda x: x.get('analysis_date', ''), reverse=True)[:5]
        }
    
    def get_website_metrics(self, website_id: int) -> Dict:
        website = self.storage.get_website(website_id)
        if not website:
            return {}
        
        analyses = self.storage.get_analyses_by_website(website_id)
        recommendations = self.storage.get_recommendations_by_website(website_id)
        
        latest_analysis = analyses[-1] if analyses else None
        
        return {
            "website": {
                "id": website.get('id'),
                "name": website.get('name'),
                "type": website.get('website_type'),
                "created_at": website.get('created_at')
            },
            "latest_analysis": latest_analysis,
            "total_analyses": len(analyses),
            "total_recommendations": len(recommendations),
            "metrics_history": analyses
        }
    
    def get_behavioral_metrics(self, website_id: int) -> Dict:
        return {
            "website_id": website_id,
            "page_views": 0,
            "unique_visitors": 0,
            "bounce_rate": 0.0,
            "avg_session_duration": 0.0,
            "conversion_rate": 0.0,
            "note": "Поведенческие метрики требуют интеграции с системой аналитики"
        }

