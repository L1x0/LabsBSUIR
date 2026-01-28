from flask import Flask, render_template, request, redirect, url_for, jsonify, session
from controllers.website_generator import WebsiteGenerator
from controllers.analyzer import WebsiteAnalyzer
from controllers.decision_support import DecisionSupport
from controllers.monitoring import MonitoringController
from infrastructure.storage import DataStorage
from infrastructure.ai_client import OllamaClient
from datetime import datetime
import os

app = Flask(__name__)
app.secret_key = os.urandom(24)

storage = DataStorage()
ai_client = OllamaClient()
generator = WebsiteGenerator(storage, ai_client)
analyzer = WebsiteAnalyzer(storage)
decision_support = DecisionSupport(storage, ai_client)
monitoring = MonitoringController(storage)


@app.route('/')
def index():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    return redirect(url_for('dashboard'))


@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form.get('username', '').strip()
        if not username:
            username = f"user_{len(storage.get_all_users()) + 1}"
        
        users = storage.get_all_users()
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
                "created_at": datetime.now().isoformat()
            }
            user_id = storage.save_user(user_data)
        else:
            user_id = user.get("id")
        
        session['user_id'] = user_id
        session['username'] = username
        return redirect(url_for('dashboard'))
    
    return render_template('login.html')


@app.route('/logout')
def logout():
    session.clear()
    return redirect(url_for('login'))


@app.route('/dashboard')
def dashboard():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    
    user_id = session['user_id']
    websites = storage.get_websites_by_user(user_id)
    
    analytics = monitoring.get_analytics_report(user_id)
    
    return render_template('dashboard.html', 
                         username=session.get('username', 'Пользователь'),
                         websites=websites,
                         analytics=analytics)


@app.route('/websites')
def websites_list():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    
    user_id = session['user_id']
    websites = storage.get_websites_by_user(user_id)
    
    return render_template('websites.html', websites=websites)


@app.route('/websites/create', methods=['GET', 'POST'])
def create_website():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    
    if request.method == 'POST':
        if request.is_json:
            data = request.get_json()
            name = data.get('name', '').strip()
            website_type = data.get('type', 'corporate')
            description = data.get('description', '').strip()
            target_audience = data.get('target_audience', '').strip()
        else:
            name = request.form.get('name', '').strip()
            website_type = request.form.get('type', 'corporate')
            description = request.form.get('description', '').strip()
            target_audience = request.form.get('target_audience', '').strip()
        
        if not name:
            return jsonify({'error': 'Название обязательно'}), 400
        
        try:
            website = generator.generate_website(
                session['user_id'],
                name,
                website_type,
                description,
                target_audience
            )
            return jsonify({'success': True, 'website_id': website['id']})
        except Exception as e:
            return jsonify({'error': str(e)}), 500
    
    return render_template('create_website.html')


@app.route('/websites/<int:website_id>')
def website_detail(website_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    
    website = storage.get_website(website_id)
    if not website or website.get('user_id') != session['user_id']:
        return redirect(url_for('websites_list'))
    
    analyses = storage.get_analyses_by_website(website_id)
    recommendations = storage.get_recommendations_by_website(website_id)
    
    return render_template('website_detail.html',
                         website=website,
                         analyses=analyses,
                         recommendations=recommendations)


@app.route('/websites/<int:website_id>/analyze', methods=['POST'])
def analyze_website(website_id):
    if 'user_id' not in session:
        return jsonify({'error': 'Не авторизован'}), 401
    
    try:
        analysis = analyzer.analyze_website(website_id)
        recommendations = decision_support.generate_recommendations(website_id, analysis)
        return jsonify({
            'success': True,
            'analysis': analysis,
            'recommendations_count': len(recommendations)
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/websites/<int:website_id>/recommendations')
def website_recommendations(website_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    
    website = storage.get_website(website_id)
    if not website or website.get('user_id') != session['user_id']:
        return redirect(url_for('websites_list'))
    
    recommendations = storage.get_recommendations_by_website(website_id)
    
    return render_template('recommendations.html',
                         website=website,
                         recommendations=recommendations)


@app.route('/recommendations/<int:rec_id>/apply', methods=['POST'])
def apply_recommendation(rec_id):
    if 'user_id' not in session:
        return jsonify({'error': 'Не авторизован'}), 401
    
    try:
        success = decision_support.apply_recommendation(rec_id)
        if success:
            return jsonify({'success': True})
        else:
            return jsonify({'error': 'Не удалось применить рекомендацию'}), 400
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/monitoring')
def monitoring_page():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    
    user_id = session['user_id']
    report = monitoring.get_analytics_report(user_id)
    websites = storage.get_websites_by_user(user_id)
    
    websites_metrics = []
    for website in websites:
        metrics = monitoring.get_website_metrics(website['id'])
        websites_metrics.append(metrics)
    
    return render_template('monitoring.html',
                         report=report,
                         websites_metrics=websites_metrics)


@app.route('/websites/<int:website_id>/preview')
def preview_website(website_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    
    website = storage.get_website(website_id)
    if not website or website.get('user_id') != session['user_id']:
        return redirect(url_for('websites_list'))
    
    content = website.get("content", {})
    html_content = content.get("html", "")
    css_content = content.get("css", "")
    
    html_content = html_content.replace("{{name}}", website.get("name", ""))
    html_content = html_content.replace("{{description}}", website.get("description", ""))
    
    if css_content:
        if "</head>" in html_content:
            html_content = html_content.replace("</head>", f"<style>{css_content}</style></head>")
        elif "<head>" in html_content:
            html_content = html_content.replace("<head>", f"<head><style>{css_content}</style>")
        else:
            if "<body>" in html_content:
                html_content = html_content.replace("<body>", f"<body><style>{css_content}</style>")
            else:
                html_content = f"<style>{css_content}</style>{html_content}"
    
    return html_content


@app.route('/websites/<int:website_id>/export')
def export_website(website_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    
    website = storage.get_website(website_id)
    if not website or website.get('user_id') != session['user_id']:
        return redirect(url_for('websites_list'))
    
    from pathlib import Path
    export_dir = Path("exports") / f"website_{website_id}"
    export_dir.mkdir(parents=True, exist_ok=True)
    
    content = website.get("content", {})
    html_content = content.get("html", "")
    html_content = html_content.replace("{{name}}", website.get("name", ""))
    html_content = html_content.replace("{{description}}", website.get("description", ""))
    
    html_file = export_dir / "index.html"
    with open(html_file, 'w', encoding='utf-8') as f:
        f.write(html_content)
    
    css_file = export_dir / "style.css"
    css_content = content.get("css", "")
    with open(css_file, 'w', encoding='utf-8') as f:
        f.write(css_content)
    
    return jsonify({
        'success': True,
        'message': f'Веб-сайт экспортирован в {export_dir}'
    })


@app.route('/ab-testing/<int:website_id>', methods=['GET', 'POST'])
def ab_testing(website_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    
    website = storage.get_website(website_id)
    if not website or website.get('user_id') != session['user_id']:
        return redirect(url_for('websites_list'))
    
    if request.method == 'POST':
        element = request.form.get('element', '').strip()
        if not element:
            element = "элемент интерфейса"
        
        test_suggestion = decision_support.suggest_ab_test(website_id, element)
        return jsonify({'success': True, 'suggestion': test_suggestion})
    
    return render_template('ab_testing.html', website=website)


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=8000)

