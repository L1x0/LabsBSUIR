from dataclasses import dataclass, field
from typing import List, Optional, Dict
from enum import Enum


class OptimizationPriority(Enum):
    SEO = "seo"
    UX = "ux"
    PERFORMANCE = "performance"
    CONVERSION = "conversion"


@dataclass
class BusinessGoal:
    id: Optional[int] = None
    website_id: int = 0
    goal_name: str = ""
    priority: OptimizationPriority = OptimizationPriority.SEO
    target_value: float = 0.0
    current_value: float = 0.0
    description: str = ""
    created_at: Optional[str] = None


@dataclass
class OptimizationHistory:
    id: Optional[int] = None
    website_id: int = 0
    recommendation_id: Optional[int] = None
    change_type: str = ""
    change_description: str = ""
    before_metrics: Dict = field(default_factory=dict)
    after_metrics: Dict = field(default_factory=dict)
    effectiveness_score: float = 0.0
    applied_at: Optional[str] = None

