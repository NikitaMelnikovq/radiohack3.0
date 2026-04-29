from pydantic import BaseModel, ConfigDict


class User(BaseModel):
    model_config = ConfigDict(frozen=True)

    id: int
    email: str
    phone_number: str
    full_name: str
    financial_segment: str
