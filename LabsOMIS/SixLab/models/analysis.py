from dataclasses import dataclass, field
from typing import List, Dict, Optional
from enum import Enum


class OptimizationType(Enum):
    PERFORMANCE = "performance"
    UX = "ux"
    SEO = "seo"
    CONVERSION = "conversion"


@dataclass
class AnalysisResult:
    website_id: int
    analysis_date: str
    performance_score: float = 0.0
    ux_score: float = 0.0
    seo_score: float = 0.0
    conversion_score: float = 0.0
    issues: List[str] = field(default_factory=list)
    recommendations: List[str] = field(default_factory=list)


@dataclass
class OptimizationRecommendation:
    id: Optional[int] = None
    website_id: int = 0
    optimization_type: OptimizationType = OptimizationType.PERFORMANCE
    title: str = ""
    description: str = ""
    priority: int = 0
    estimated_impact: str = ""
    applied: bool = False

