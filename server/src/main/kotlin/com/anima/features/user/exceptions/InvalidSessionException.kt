package com.anima.features.user.exceptions

// the access token is valid but does not describe a usable account anymore
// (user row gone, missing or unknown accountType claim); answered as 401 so the app
// refreshes once and ends the session if that does not help
class InvalidSessionException(message: String = "Session is no longer valid, please sign in again") :
    RuntimeException(message)
