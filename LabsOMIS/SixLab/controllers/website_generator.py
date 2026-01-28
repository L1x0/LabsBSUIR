from typing import Dict, Optional
from datetime import datetime
from models.website import Website, WebsiteType, WebsiteTemplate
from infrastructure.storage import DataStorage
from infrastructure.ai_client import OllamaClient


class WebsiteGenerator:

    def __init__(self, storage: DataStorage, ai_client: Optional[OllamaClient] = None):
        self.storage = storage
        self.ai_client = ai_client or OllamaClient()
        self._init_default_templates()
    
    def _init_default_templates(self):
        templates = self.storage._load_data("templates.json")
        if len(templates) == 0:
            default_templates = [
                {
                    "name": "Корпоративный сайт - Базовый",
                    "website_type": "corporate",
                    "html_template": self._get_corporate_html_template(),
                    "css_template": self._get_corporate_css_template(),
                    "description": "Базовый корпоративный шаблон"
                },
                {
                    "name": "Интернет-магазин - Базовый",
                    "website_type": "ecommerce",
                    "html_template": self._get_ecommerce_html_template(),
                    "css_template": self._get_ecommerce_css_template(),
                    "description": "Базовый шаблон интернет-магазина"
                },
                {
                    "name": "Блог - Базовый",
                    "website_type": "blog",
                    "html_template": self._get_blog_html_template(),
                    "css_template": self._get_blog_css_template(),
                    "description": "Базовый шаблон блога"
                }
            ]
            for template in default_templates:
                self.storage.save_template(template)
    
    def generate_website(self, user_id: int, name: str, website_type: str, 
                        description: str = "", target_audience: str = "") -> Dict:

        templates = self.storage.get_templates_by_type(website_type)
        if not templates:
            all_templates = self.storage._load_data("templates.json")
            template = all_templates[0] if all_templates else None
        else:
            template = templates[0]
        
        if not template:
            raise ValueError("Нет доступных шаблонов")
        
        html_content = template.get("html_template", "")
        css_content = template.get("css_template", "")
        
        if self.ai_client and self.ai_client.is_available() and description:
            prompt = f"""Создай краткое описание для главной страницы веб-сайта типа {website_type}.
Описание сайта: {description}
Целевая аудитория: {target_audience}
Верни только текст описания, без дополнительных комментариев."""
            
            ai_description = self.ai_client.generate(prompt)
            html_content = html_content.replace("{{description}}", ai_description)
            html_content = html_content.replace("{{name}}", name)
        
        website_data = {
            "name": name,
            "website_type": website_type,
            "user_id": user_id,
            "url": f"site_{user_id}_{len(self.storage.get_all_websites()) + 1}",
            "description": description,
            "target_audience": target_audience,
            "content": {
                "html": html_content,
                "css": css_content,
                "js": ""
            },
            "metrics": {},
            "created_at": datetime.now().isoformat(),
            "updated_at": datetime.now().isoformat()
        }
        
        website_id = self.storage.save_website(website_data)
        website_data["id"] = website_id
        
        return website_data
    
    def _get_corporate_html_template(self) -> str:
        return """<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{{name}}</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <header>
        <nav>
            <div class="logo">{{name}}</div>
            <ul>
                <li><a href="#home">Главная</a></li>
                <li><a href="#about">О нас</a></li>
                <li><a href="#services">Услуги</a></li>
                <li><a href="#contact">Контакты</a></li>
            </ul>
        </nav>
    </header>
    <main>
        <section id="home" class="hero">
            <h1>Добро пожаловать в {{name}}</h1>
            <p>{{description}}</p>
        </section>
        <section id="about">
            <h2>О нас</h2>
            <p>Мы предоставляем качественные услуги для наших клиентов.</p>
        </section>
        <section id="services">
            <h2>Наши услуги</h2>
            <div class="services-grid">
                <div class="service-card">Услуга 1</div>
                <div class="service-card">Услуга 2</div>
                <div class="service-card">Услуга 3</div>
            </div>
        </section>
        <section id="contact">
            <h2>Свяжитесь с нами</h2>
            <form>
                <input type="text" placeholder="Имя">
                <input type="email" placeholder="Email">
                <textarea placeholder="Сообщение"></textarea>
                <button type="submit">Отправить</button>
            </form>
        </section>
    </main>
    <footer>
        <p>&copy; 2024 {{name}}. Все права защищены.</p>
    </footer>
</body>
</html>"""
    
    def _get_corporate_css_template(self) -> str:
        return """* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: Arial, sans-serif;
    line-height: 1.6;
    color: #333;
}

header {
    background: #2c3e50;
    color: white;
    padding: 1rem 0;
}

nav {
    display: flex;
    justify-content: space-between;
    align-items: center;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 2rem;
}

.logo {
    font-size: 1.5rem;
    font-weight: bold;
}

nav ul {
    display: flex;
    list-style: none;
    gap: 2rem;
}

nav a {
    color: white;
    text-decoration: none;
}

.hero {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 4rem 2rem;
    text-align: center;
}

.hero h1 {
    font-size: 3rem;
    margin-bottom: 1rem;
}

section {
    padding: 4rem 2rem;
    max-width: 1200px;
    margin: 0 auto;
}

.services-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 2rem;
    margin-top: 2rem;
}

.service-card {
    background: #f4f4f4;
    padding: 2rem;
    border-radius: 8px;
    text-align: center;
}

form {
    max-width: 500px;
    margin: 0 auto;
}

form input, form textarea {
    width: 100%;
    padding: 0.75rem;
    margin-bottom: 1rem;
    border: 1px solid #ddd;
    border-radius: 4px;
}

form button {
    background: #667eea;
    color: white;
    padding: 0.75rem 2rem;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

footer {
    background: #2c3e50;
    color: white;
    text-align: center;
    padding: 2rem;
}

@media (max-width: 768px) {
    nav ul {
        flex-direction: column;
        gap: 1rem;
    }
    
    .hero h1 {
        font-size: 2rem;
    }
}"""
    
    def _get_ecommerce_html_template(self) -> str:

        return """<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{{name}} - Интернет-магазин</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <header>
        <nav>
            <div class="logo">{{name}}</div>
            <ul>
                <li><a href="#home">Главная</a></li>
                <li><a href="#products">Товары</a></li>
                <li><a href="#cart">Корзина</a></li>
            </ul>
        </nav>
    </header>
    <main>
        <section id="home" class="hero">
            <h1>{{name}}</h1>
            <p>{{description}}</p>
        </section>
        <section id="products">
            <h2>Наши товары</h2>
            <div class="products-grid">
                <div class="product-card">
                    <div class="product-image">Изображение товара</div>
                    <h3>Товар 1</h3>
                    <p class="price">1000 руб.</p>
                    <button>В корзину</button>
                </div>
                <div class="product-card">
                    <div class="product-image">Изображение товара</div>
                    <h3>Товар 2</h3>
                    <p class="price">2000 руб.</p>
                    <button>В корзину</button>
                </div>
                <div class="product-card">
                    <div class="product-image">Изображение товара</div>
                    <h3>Товар 3</h3>
                    <p class="price">3000 руб.</p>
                    <button>В корзину</button>
                </div>
            </div>
        </section>
    </main>
    <footer>
        <p>&copy; 2024 {{name}}. Все права защищены.</p>
    </footer>
</body>
</html>"""
    
    def _get_ecommerce_css_template(self) -> str:

        return """* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: Arial, sans-serif;
    line-height: 1.6;
    color: #333;
}

header {
    background: #e74c3c;
    color: white;
    padding: 1rem 0;
}

nav {
    display: flex;
    justify-content: space-between;
    align-items: center;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 2rem;
}

.logo {
    font-size: 1.5rem;
    font-weight: bold;
}

nav ul {
    display: flex;
    list-style: none;
    gap: 2rem;
}

nav a {
    color: white;
    text-decoration: none;
}

.hero {
    background: #ecf0f1;
    padding: 4rem 2rem;
    text-align: center;
}

.hero h1 {
    font-size: 3rem;
    margin-bottom: 1rem;
}

section {
    padding: 4rem 2rem;
    max-width: 1200px;
    margin: 0 auto;
}

.products-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 2rem;
    margin-top: 2rem;
}

.product-card {
    background: white;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 1.5rem;
    text-align: center;
    transition: transform 0.3s;
}

.product-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.product-image {
    width: 100%;
    height: 200px;
    background: #f4f4f4;
    margin-bottom: 1rem;
    display: flex;
    align-items: center;
    justify-content: center;
}

.price {
    font-size: 1.5rem;
    font-weight: bold;
    color: #e74c3c;
    margin: 1rem 0;
}

.product-card button {
    background: #e74c3c;
    color: white;
    padding: 0.75rem 2rem;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    width: 100%;
}

footer {
    background: #2c3e50;
    color: white;
    text-align: center;
    padding: 2rem;
}

@media (max-width: 768px) {
    .products-grid {
        grid-template-columns: 1fr;
    }
}"""
    
    def _get_blog_html_template(self) -> str:
        return """<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{{name}} - Блог</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <header>
        <nav>
            <div class="logo">{{name}}</div>
            <ul>
                <li><a href="#home">Главная</a></li>
                <li><a href="#posts">Статьи</a></li>
                <li><a href="#about">О блоге</a></li>
            </ul>
        </nav>
    </header>
    <main>
        <section id="home" class="hero">
            <h1>{{name}}</h1>
            <p>{{description}}</p>
        </section>
        <section id="posts">
            <h2>Последние статьи</h2>
            <div class="posts-list">
                <article class="post-card">
                    <h3>Заголовок статьи 1</h3>
                    <p class="post-date">01.01.2024</p>
                    <p>Краткое описание статьи...</p>
                    <a href="#" class="read-more">Читать далее</a>
                </article>
                <article class="post-card">
                    <h3>Заголовок статьи 2</h3>
                    <p class="post-date">02.01.2024</p>
                    <p>Краткое описание статьи...</p>
                    <a href="#" class="read-more">Читать далее</a>
                </article>
                <article class="post-card">
                    <h3>Заголовок статьи 3</h3>
                    <p class="post-date">03.01.2024</p>
                    <p>Краткое описание статьи...</p>
                    <a href="#" class="read-more">Читать далее</a>
                </article>
            </div>
        </section>
    </main>
    <footer>
        <p>&copy; 2024 {{name}}. Все права защищены.</p>
    </footer>
</body>
</html>"""
    
    def _get_blog_css_template(self) -> str:

        return """* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: Georgia, serif;
    line-height: 1.8;
    color: #333;
}

header {
    background: #27ae60;
    color: white;
    padding: 1rem 0;
}

nav {
    display: flex;
    justify-content: space-between;
    align-items: center;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 2rem;
}

.logo {
    font-size: 1.5rem;
    font-weight: bold;
}

nav ul {
    display: flex;
    list-style: none;
    gap: 2rem;
}

nav a {
    color: white;
    text-decoration: none;
}

.hero {
    background: #ecf0f1;
    padding: 4rem 2rem;
    text-align: center;
}

.hero h1 {
    font-size: 3rem;
    margin-bottom: 1rem;
}

section {
    padding: 4rem 2rem;
    max-width: 1200px;
    margin: 0 auto;
}

.posts-list {
    display: flex;
    flex-direction: column;
    gap: 2rem;
    margin-top: 2rem;
}

.post-card {
    background: white;
    border-left: 4px solid #27ae60;
    padding: 2rem;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.post-card h3 {
    margin-bottom: 0.5rem;
    color: #27ae60;
}

.post-date {
    color: #7f8c8d;
    font-size: 0.9rem;
    margin-bottom: 1rem;
}

.read-more {
    display: inline-block;
    margin-top: 1rem;
    color: #27ae60;
    text-decoration: none;
    font-weight: bold;
}

.read-more:hover {
    text-decoration: underline;
}

footer {
    background: #2c3e50;
    color: white;
    text-align: center;
    padding: 2rem;
}

@media (max-width: 768px) {
    nav ul {
        flex-direction: column;
        gap: 1rem;
    }
}"""

