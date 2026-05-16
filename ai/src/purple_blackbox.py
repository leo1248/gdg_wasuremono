from __future__ import annotations

from dataclasses import dataclass
from typing import Protocol


@dataclass(frozen=True)
class ClientRef:
    client_id: str
    name: str | None = None
    email: str | None = None
    phone: str | None = None


@dataclass(frozen=True)
class CallQuestion:
    question_id: str
    text: str


@dataclass(frozen=True)
class CallAnswer:
    question_id: str
    answer: str


@dataclass(frozen=True)
class CallSession:
    session_id: str
    client: ClientRef
    questions: list[CallQuestion]


@dataclass(frozen=True)
class ContactInfo:
    name: str | None
    email: str | None
    phone: str | None


@dataclass(frozen=True)
class FoundContactRecord:
    item_id: str
    contact: ContactInfo


@dataclass(frozen=True)
class LostRequestRecord:
    request_id: str
    client: ClientRef
    feature_text: str


@dataclass(frozen=True)
class EmailRequest:
    to_email: str
    subject: str
    body: str


@dataclass(frozen=True)
class EmailSendResult:
    message_id: str
    accepted: bool


class CallApi(Protocol):
    def start_lost_call(self, client: ClientRef) -> CallSession:
        ...

    def start_found_call(self, client: ClientRef) -> CallSession:
        ...

    def collect_answers(self, session_id: str) -> list[CallAnswer]:
        ...

    def build_feature_text(self, answers: list[CallAnswer]) -> str:
        ...


class FirebaseDb(Protocol):
    def save_found_contact(self, item_id: str, contact: ContactInfo) -> FoundContactRecord:
        ...

    def get_contact_by_item_id(self, item_id: str) -> ContactInfo | None:
        ...

    def queue_lost_request(self, client: ClientRef, feature_text: str) -> LostRequestRecord:
        ...


class EmailApi(Protocol):
    def send_email(self, request: EmailRequest) -> EmailSendResult:
        ...


class PurpleBlackBox(Protocol):
    call: CallApi
    firebase: FirebaseDb
    email: EmailApi

