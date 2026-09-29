package com.anima.features.event.exceptions

class EventNotFoundException : RuntimeException("Event not found")
class EventForbiddenException : RuntimeException("Only the organizer can change this event")
