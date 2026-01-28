from dataclasses import dataclass
from typing import Optional
from enum import Enum


class UserType(Enum):
    CUSTOMER = "customer"
    ADMIN = "admin"


@dataclass
class User:
    id: Optional[int] = None
    username: str = ""
    email: str = ""
    user_type: UserType = UserType.CUSTOMER
    created_at: Optional[str] = None

