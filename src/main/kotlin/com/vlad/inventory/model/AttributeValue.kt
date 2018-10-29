package com.vlad.inventory.model

import com.fasterxml.jackson.annotation.JsonInclude
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class AttributeValue(
        @Id @GeneratedValue
        var id: Long?,
        @ManyToOne
        var productUnit: ProductUnit,
        @ManyToOne
        var attributeType: AttributeType,
        // TODO: having multiple nullable columns is not an extensible representation. A better way would be to have
        // a single column that stores internal data for an attribute type
        var intValue: Int?,
        var floatValue: Float?,
        var stringValue: String?
) {
    fun toOutputDTO(): AttributeValueDTO =
            AttributeValueDTO(
                    id = id,
                    attributeTypeId = attributeType.id,
                    namespace = attributeType.namespace,
                    name = attributeType.name,
                    type = attributeType.type,
                    intValue = intValue,
                    floatValue = floatValue,
                    stringValue = stringValue
            )
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AttributeValueDTO(
        var id: Long?,
        // attributes for output
        var namespace: String?,
        var name: String?,
        var type: AttributeValueType?,

        // Attributes for input
        var attributeTypeId: Long,
        var intValue: Int?,
        var floatValue: Float?,
        var stringValue: String?
)

data class MultipleAttributeValuesResponse(
        val attributes: List<AttributeValueDTO>
)