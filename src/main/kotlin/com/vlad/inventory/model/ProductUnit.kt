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
    var owner: Owner,
    @ManyToOne
    var productType: ProductType
) {
    fun toDTO(): ProductUnitDTO {
        return ProductUnitDTO(id = id, name = name, ownerId = owner.id, ownerName = owner.name, productTypeId = productType.id,
                productTypeName = productType.name, attributes = null, isValid = null, unsatisfiedConstraints = null)
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProductUnitDTO(
        var id: Long?,
        var name: String,
        var ownerId: Long,
        var ownerName: String?,
        var productTypeId: Long?,
        var productTypeName: String?,
        var attributes: List<AttributeValueDTO>?,
        var isValid: Boolean?,
        var unsatisfiedConstraints: List<ConstraintDTO>?
)
data class MultipleUnitsResponse(
        var units: List<ProductUnitDTO>
)