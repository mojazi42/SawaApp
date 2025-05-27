package com.example.sawaapplication.screens.home.domain.model


sealed class EventFilterType {
    object DEFAULT : EventFilterType()
    object Finished : EventFilterType()
    object Still : EventFilterType()
}