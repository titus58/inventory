package com.vlad.inventory.model

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
        return ProductUnitDTO(id = id, name = name, ownerId = owner.id)
    }
}

data class ProductUnitDTO(
        var id: Long?,
        var name: String,
        var ownerId: Long
)
data class MultipleUnitsResponse(
        var units: List<ProductUnitDTO>
)