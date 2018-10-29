package com.vlad.inventory.service

import com.vlad.inventory.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UnitService {
    @Autowired
    lateinit var productUnitRepository: ProductUnitRepository

    @Autowired
    lateinit var ownerRepository: OwnerRepository

    @Autowired
    lateinit var attributeValueRepository: AttributeValueRepository

    @Autowired
    lateinit var attributeTypeRepository: AttributeTypeRepository

    fun postUnit(productUnitDTO: ProductUnitDTO): ProductUnitDTO {
        val owner = ownerRepository
                .findById(productUnitDTO.ownerId)
                .orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find owner with id " + productUnitDTO.ownerId) }
        val productUnit = ProductUnit(
                id = null,
                name = productUnitDTO.name,
                owner = owner
        )
        return productUnitRepository.save(productUnit).toDTO()
    }

    fun getAllAttributes(unitId: Long): MultipleAttributeValuesResponse {
        val productUnit = productUnitRepository
                .findById(unitId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find unit with id " + unitId) }
        val attributes = attributeValueRepository
                .findByProductUnit(productUnit)
                .map { it -> it.toOutputDTO() }
                .toList()
        return MultipleAttributeValuesResponse(attributes)
    }

    fun postAttribute(unitId: Long, attributeValueDTO: AttributeValueDTO): AttributeValueDTO? {
        val productUnit = productUnitRepository
                .findById(unitId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find unit with id " + unitId) }
        val attributeType = attributeTypeRepository
                .findById(attributeValueDTO.attributeTypeId)
                .orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find attribute type with id " + attributeValueDTO.attributeTypeId) }
        if (attributeType.type == AttributeValueType.FLOAT) {
            val floatValue = attributeValueDTO.floatValue ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "floatValue not set for float attribute")
            val attributeValue = AttributeValue(id = null, productUnit = productUnit, attributeType = attributeType, floatValue = floatValue, intValue = null, stringValue = null)
            return attributeValueRepository.save(attributeValue).toOutputDTO()
        }
        if (attributeType.type == AttributeValueType.STRING) {
            val stringValue = attributeValueDTO.stringValue ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "stringValue not set for string attribute")
            val attributeValue = AttributeValue(id = null, productUnit = productUnit, attributeType = attributeType, floatValue = null, intValue = null, stringValue = stringValue)
            return attributeValueRepository.save(attributeValue).toOutputDTO()
        }
        if (attributeType.type == AttributeValueType.INT) {
            val intValue = attributeValueDTO.intValue ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "intValue not set for int attribute")
            val attributeValue = AttributeValue(id = null, productUnit = productUnit, attributeType = attributeType, floatValue = null, intValue = intValue, stringValue = null)
            return attributeValueRepository.save(attributeValue).toOutputDTO()
        }
        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error. Unexpected attribute type " + attributeType.type)
        return null
    }

    fun getAll(): MultipleUnitsResponse {
        return MultipleUnitsResponse(productUnitRepository
                .findAll()
                .map { it -> it.toDTO() }
                .toList())
    }
}