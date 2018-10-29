package com.vlad.inventory.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*

@Entity
data class ProductUnit(
    @Id @GeneratedValue
    var id: Long?,
    @Column(nullable = false)
    var description: String,
    @ManyToOne
    var owner: Owner,
    @ManyToOne
    var productType: ProductType,
    var mass: String,
    @Temporal(TemporalType.DATE)
    var expiryDate: Date,
    var latitude: Double?,
    var longitude: Double?
) {
    @field:CreationTimestamp
    lateinit var creationTimestamp: Date

    fun toDTO(): ProductUnitDTO {
        // TODO set location
        return ProductUnitDTO(id = id, description = description, ownerId = owner.id, ownerName = owner.name, productTypeId = productType.id,
                productTypeName = productType.name, attributes = null, isValid = null, unsatisfiedConstraints = null, creationTimestamp = creationTimestamp,
                mass = mass, expiryDate = expiryDate, location = null)
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProductUnitDTO(
        var id: Long?,
        var ownerId: Long,
        var ownerName: String?,
        var productTypeId: Long?,
        var productTypeName: String?,
        var description: String,
        var creationTimestamp: Date?,
        var mass: String,
        var expiryDate: Date,

        // Output only fields
        var attributes: List<AttributeValueDTO>?,
        var isValid: Boolean?,
        var unsatisfiedConstraints: List<ConstraintDTO>?,
        var location: Location?
)

data class Location(
        var latitude: Double,
        var longitude: Double
)

data class MultipleUnitsResponse(
        var units: List<ProductUnitDTO>
)