import requests
import json
from typing import Optional, Dict


class OllamaClient:
    def __init__(self, base_url: str = "http://localhost:11434", model: str = "gemma3"):
        self.base_url = base_url
        self.model = model
        self.api_url = f"{base_url}/api/generate"
    
    def generate(self, prompt: str, system_prompt: Optional[str] = None) -> str:
        try:
            payload = {
                "model": self.model,
                "prompt": prompt,
                "stream": False
            }
            
            if system_prompt:
                payload["system"] = system_prompt
            
            response = requests.post(self.api_url, json=payload, timeout=30)
            response.raise_for_status()
            
            result = response.json()
            return result.get("response", "")
        except requests.exceptions.RequestException as e:
            print(f"Ошибка при обращении к Ollama: {e}")
            return f"[Ошибка генерации: {str(e)}]"
        except Exception as e:
            print(f"Неожиданная ошибка: {e}")
            return f"[Ошибка: {str(e)}]"
    
    def is_available(self) -> bool:
        try:
            response = requests.get(f"{self.base_url}/api/tags", timeout=5)
            return response.status_code == 200
        except:
            return False

