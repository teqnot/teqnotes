package com.example.model

import kotlinx.serialization.Serializable

@Serializable
enum class BlockType {
    HEADER,
    PARAGRAPH,
    TASK_LIST,
    BULLET_LIST,
    QUOTE,
    IMAGE,
    CHART,
    CODE_BLOCK
}