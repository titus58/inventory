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

    @Autowired
    lateinit var productTypeRepository: ProductTypeRepository

    @Autowired
    lateinit var constraintEntityRepository: ConstraintEntityRepository

    fun postUnit(productUnitDTO: ProductUnitDTO): ProductUnitDTO {
        val productTypeId = productUnitDTO.productTypeId ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "productTypeId not set")
        val productType = productTypeRepository
                .findById(productTypeId)
                .orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find productType with id " + productTypeId) }
        val owner = ownerRepository
                .findById(productUnitDTO.ownerId)
                .orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find owner with id " + productUnitDTO.ownerId) }
        val productUnit = ProductUnit(
                id = null,
                description = productUnitDTO.description,
                owner = owner,
                productType = productType,
                mass = productUnitDTO.mass,
                expiryDate = productUnitDTO.expiryDate,
                latitude = null,
                longitude = null
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

    fun checkConstraintIsSatisfied(constraintEntity: ConstraintEntity, productUnit: ProductUnit): Boolean  {
        val attributeType = constraintEntity.attributeType
        val attributeValue = attributeValueRepository.findByProductUnitAndAttributeType(productUnit = productUnit, attributeType = attributeType)
        if (attributeValue.isEmpty()) {
            return false
        }
        val firstValue = attributeValue[0]
        val constraintDto = constraintEntity.toOutputDTO()
        if (constraintEntity.constraintType == ConstraintType.PRESENCE) {
            return true // We checked above that the attribute exist
        }
        if (constraintEntity.constraintType == ConstraintType.RANGE_FLOAT && firstValue.attributeType.type == AttributeValueType.FLOAT) {
            // TODO: this is not a good way to deal with nullables
            val rangeFloatData = constraintDto.rangeFloatData!!
            val floatValue = firstValue.floatValue!!
            return floatValue >= rangeFloatData[0] && floatValue <= rangeFloatData[1]
        }
        if (constraintEntity.constraintType == ConstraintType.RANGE_INT && firstValue.attributeType.type == AttributeValueType.INT) {
            // TODO: this is not a good way to deal with nullables
            val rangeIntData = constraintDto.rangeIntData!!
            val intValue = firstValue.intValue!!
            return intValue >= rangeIntData[0] && intValue <= rangeIntData[1]
        }
        if (constraintEntity.constraintType == ConstraintType.WHITELIST && firstValue.attributeType.type == AttributeValueType.STRING) {
            val whitelist = constraintDto.whitelistData!!
            val stringValue = firstValue.stringValue!!
            return whitelist.contains(stringValue)
        }
        return false
    }

    fun getEnhancedUnitDTO(productUnit: ProductUnit, returnAttributes: Boolean, validate: Boolean): ProductUnitDTO {
        var dto = productUnit.toDTO()
        if (!returnAttributes && !validate) {
            return dto
        }
        if (validate) {
             val unsatisfiedConstraints = constraintEntityRepository
                    .findByProductType(productUnit.productType)
                    .filterNot { checkConstraintIsSatisfied(it, productUnit) }
                     .map { it.toOutputDTO() }
            dto.isValid = unsatisfiedConstraints.isEmpty()
            dto.unsatisfiedConstraints = unsatisfiedConstraints
        }
        if (returnAttributes) {
            dto.attributes = attributeValueRepository
                    .findByProductUnit(productUnit)
                    .map { it.toOutputDTO() }
        }
        return dto
    }

    fun getAll(returnAttributes: Boolean, validate: Boolean): List<ProductUnitDTO> {
        return productUnitRepository
                .findAll()
                .map { getEnhancedUnitDTO(it, returnAttributes = returnAttributes, validate = validate) }
                .toList()
    }

    fun getLocation(unitId: Long): Location?  {
        val productUnit = productUnitRepository
                .findById(unitId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find unit with id " + unitId) }
        return productUnit.getLocation()
    }

    fun postLocation(unitId: Long, location: Location): Location {
        val productUnit = productUnitRepository
                .findById(unitId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find unit with id " + unitId) }
        productUnit.longitude = location.longitude
        productUnit.latitude = location.latitude
        productUnitRepository.save(productUnit)
        return location
    }
}