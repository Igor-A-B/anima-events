package com.anima.features.subscription.exceptions

class NotAVisitorException : RuntimeException("Only visitors can subscribe to events")
class EventAlreadyFinishedException : RuntimeException("This event is already finished")
class SubscriptionNotCancellableException : RuntimeException("This subscription can't be cancelled")
