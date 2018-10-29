package com.vlad.inventory.model

import com.fasterxml.jackson.annotation.JsonInclude
import javax.persistence.*

@Entity
data class ProductUnit(
    @Id @GeneratedValue
    var id: Long?,
    @Column(nullable = false)
    var name: String,
    @ManyToOne
    var owner: Owner
) {
    fun toDTO(): ProductUnitDTO {
        return ProductUnitDTO(id = id, name = name, ownerId = owner.id, attributes = null)
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProductUnitDTO(
        var id: Long?,
        var name: String,
        var ownerId: Long,
        var attributes: List<AttributeValueDTO>?
)
data class MultipleUnitsResponse(
        var units: List<ProductUnitDTO>
)