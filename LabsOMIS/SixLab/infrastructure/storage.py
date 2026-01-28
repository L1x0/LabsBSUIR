import json
import os
from typing import Dict, List, Optional, Any
from pathlib import Path


class DataStorage:
    
    def __init__(self, storage_dir: str = "data"):
        self.storage_dir = Path(storage_dir)
        self.storage_dir.mkdir(exist_ok=True)
        self._init_storage()
    
    def _init_storage(self):
        files = ["users.json", "websites.json", "templates.json", "analyses.json", "recommendations.json"]
        for file in files:
            file_path = self.storage_dir / file
            if not file_path.exists():
                with open(file_path, 'w', encoding='utf-8') as f:
                    json.dump([], f, ensure_ascii=False, indent=2)
    
    def _load_data(self, filename: str) -> List[Dict]:
        file_path = self.storage_dir / filename
        if not file_path.exists():
            return []
        with open(file_path, 'r', encoding='utf-8') as f:
            return json.load(f)
    
    def _save_data(self, filename: str, data: List[Dict]):
        file_path = self.storage_dir / filename
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
    
    def save_user(self, user_data: Dict) -> int:
        users = self._load_data("users.json")
        user_id = len(users) + 1
        user_data['id'] = user_id
        users.append(user_data)
        self._save_data("users.json", users)
        return user_id
    
    def get_user(self, user_id: int) -> Optional[Dict]:
        users = self._load_data("users.json")
        for user in users:
            if user.get('id') == user_id:
                return user
        return None
    
    def get_all_users(self) -> List[Dict]:
        return self._load_data("users.json")
    
    def save_website(self, website_data: Dict) -> int:
        websites = self._load_data("websites.json")
        website_id = len(websites) + 1
        website_data['id'] = website_id
        websites.append(website_data)
        self._save_data("websites.json", websites)
        return website_id
    
    def get_website(self, website_id: int) -> Optional[Dict]:
        websites = self._load_data("websites.json")
        for website in websites:
            if website.get('id') == website_id:
                return website
        return None
    
    def get_websites_by_user(self, user_id: int) -> List[Dict]:
        websites = self._load_data("websites.json")
        return [w for w in websites if w.get('user_id') == user_id]
    
    def update_website(self, website_id: int, updates: Dict):
        websites = self._load_data("websites.json")
        for i, website in enumerate(websites):
            if website.get('id') == website_id:
                websites[i].update(updates)
                self._save_data("websites.json", websites)
                return True
        return False
    
    def get_all_websites(self) -> List[Dict]:
        return self._load_data("websites.json")
    
    def save_template(self, template_data: Dict) -> int:
        templates = self._load_data("templates.json")
        template_id = len(templates) + 1
        template_data['id'] = template_id
        templates.append(template_data)
        self._save_data("templates.json", templates)
        return template_id
    
    def get_template(self, template_id: int) -> Optional[Dict]:
        templates = self._load_data("templates.json")
        for template in templates:
            if template.get('id') == template_id:
                return template
        return None
    
    def get_templates_by_type(self, website_type: str) -> List[Dict]:
        templates = self._load_data("templates.json")
        return [t for t in templates if t.get('website_type') == website_type]
    
    def save_analysis(self, analysis_data: Dict) -> int:
        analyses = self._load_data("analyses.json")
        analysis_id = len(analyses) + 1
        analysis_data['id'] = analysis_id
        analyses.append(analysis_data)
        self._save_data("analyses.json", analyses)
        return analysis_id
    
    def get_analyses_by_website(self, website_id: int) -> List[Dict]:
        analyses = self._load_data("analyses.json")
        return [a for a in analyses if a.get('website_id') == website_id]
    
    def save_recommendation(self, recommendation_data: Dict) -> int:
        recommendations = self._load_data("recommendations.json")
        rec_id = len(recommendations) + 1
        recommendation_data['id'] = rec_id
        recommendations.append(recommendation_data)
        self._save_data("recommendations.json", recommendations)
        return rec_id
    
    def get_recommendations_by_website(self, website_id: int) -> List[Dict]:
        recommendations = self._load_data("recommendations.json")
        return [r for r in recommendations if r.get('website_id') == website_id and not r.get('applied', False)]
    
    def mark_recommendation_applied(self, recommendation_id: int):
        recommendations = self._load_data("recommendations.json")
        for i, rec in enumerate(recommendations):
            if rec.get('id') == recommendation_id:
                recommendations[i]['applied'] = True
                self._save_data("recommendations.json", recommendations)
                return True
        return False

