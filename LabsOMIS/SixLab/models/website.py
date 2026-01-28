from dataclasses import dataclass, field
from typing import List, Dict, Optional
from enum import Enum


class WebsiteType(Enum):
    ECOMMERCE = "ecommerce"
    CORPORATE = "corporate"
    BLOG = "blog"


@dataclass
class Website:
    id: Optional[int] = None
    name: str = ""
    website_type: WebsiteType = WebsiteType.CORPORATE
    user_id: Optional[int] = None
    url: str = ""
    description: str = ""
    target_audience: str = ""
    content: Dict = field(default_factory=dict)
    metrics: Dict = field(default_factory=dict)
    created_at: Optional[str] = None
    updated_at: Optional[str] = None


@dataclass
class WebsiteTemplate:
    id: Optional[int] = None
    name: str = ""
    website_type: WebsiteType = WebsiteType.CORPORATE
    html_template: str = ""
    css_template: str = ""
    description: str = ""

