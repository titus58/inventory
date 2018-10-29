package com.vlad.inventory.model

import com.fasterxml.jackson.databind.ObjectMapper
import javax.persistence.*

enum class ConstraintType {
    PRESENCE, // Checks for the presence of an attribute. Works with any type of attribute
    RANGE_INT, // Checks that an integer attribute is within a range [min, max]
    RANGE_FLOAT, // Checks that a float attribute is within a range [min, max]
    WHITELIST // Checks that a string attribute belongs to a whitelist of values. Ex: coffee in ['arabica', 'robusto']
}

@Entity
data class ConstraintEntity(
    @Id @GeneratedValue
    var id: Long?,
    @ManyToOne
    var productType: ProductType,
    @ManyToOne
    var attributeType: AttributeType,
    @Enumerated(EnumType.STRING)
    var constraintType: ConstraintType,
    var internalData: String
) {
    fun toOutputDTO(): ConstraintDTO {
        val ret = ConstraintDTO(id = id, productTypeId = productType.id, attributeTypeId = attributeType.id, constraintType = constraintType, rangeFloatData = null, rangeIntData = null, whitelistData = null)
        if (constraintType == ConstraintType.RANGE_FLOAT) {
            val mapper = ObjectMapper()
            val rangeFloatData: List<Float> = mapper.readValue(internalData, mapper.typeFactory.constructCollectionType(List::class.java, Float::class.java))
            ret.rangeFloatData = rangeFloatData
        }
        if (constraintType == ConstraintType.RANGE_INT) {
            val mapper = ObjectMapper()
            val rangeIntData: List<Int> = mapper.readValue(internalData, mapper.typeFactory.constructCollectionType(List::class.java, Int::class.java))
            ret.rangeIntData = rangeIntData
        }
        if (constraintType == ConstraintType.WHITELIST) {
            val mapper = ObjectMapper()
            val whitelistData: List<String> = mapper.readValue(internalData, mapper.typeFactory.constructCollectionType(List::class.java, String::class.java))
            ret.whitelistData = whitelistData
        }
        return ret
    }
}

data class ConstraintDTO(
        var id: Long?,
        var productTypeId: Long?,
        var attributeTypeId: Long?,
        var constraintType: ConstraintType,
        var rangeIntData: List<Int>?,
        var rangeFloatData: List<Float>?,
        var whitelistData: List<String>?
)

data class MultipleConstraintsResponse(
        var constraints: List<ConstraintDTO>
)