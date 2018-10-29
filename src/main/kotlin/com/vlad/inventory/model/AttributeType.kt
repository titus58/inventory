package com.vlad.inventory.model

import javax.persistence.*

enum class AttributeValueType {
    STRING, INT, FLOAT
}

@Entity
data class AttributeType(
        @Id @GeneratedValue
        var id: Id,
        var namespace: String,
        var name: String,
        @Enumerated(EnumType.STRING)
        var type: AttributeValueType
)
