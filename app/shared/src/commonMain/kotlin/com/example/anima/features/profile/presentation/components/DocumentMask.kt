package com.example.anima.features.profile.presentation.components

private const val MASK_CHAR = '•'

// masks the first and the last group, keeps the middle ones
fun maskDocument(document: String): String {
    val groupCount = document.count { char -> !char.isDigit() } + 1
    if (groupCount < 3) return document

    var groupIndex = 0

    return buildString {
        for (char in document) {
            if (char.isDigit()) {
                val isEdgeGroup = groupIndex == 0 || groupIndex == groupCount - 1
                append(if (isEdgeGroup) MASK_CHAR else char)
            } else {
                groupIndex++
                append(char)
            }
        }
    }
}
