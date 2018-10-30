package com.vlad.inventory.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.vlad.inventory.model.*
import com.vlad.inventory.service.AttributeTypeRepository
import com.vlad.inventory.service.ConstraintEntityRepository
import com.vlad.inventory.service.ProductTypeRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@Controller
class ProductTypeController {
    val log = LoggerFactory.getLogger(this::class.java)

    // TODO: Controller should not be wired to repositories
    @Autowired
    lateinit var productTypeRepository: ProductTypeRepository

    @Autowired
    lateinit var constraintEntityRepository: ConstraintEntityRepository

    @Autowired
    lateinit var attributeTypeRepository: AttributeTypeRepository

    @GetMapping("/product-types")
    @ResponseBody
    fun getAllProductTypes(): MultipleProductTypesResponse {
        val productTypes = productTypeRepository
                .findAll()
                .map { it -> it.toDTO() }
        return MultipleProductTypesResponse(productTypes)
    }


    @GetMapping("/product-types/{productTypeId}")
    @ResponseBody
    fun getSingleProductType(@PathVariable productTypeId: Long): ProductTypeDTO {
        val productType = productTypeRepository
                .findById(productTypeId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find product type with id  " + productTypeId) }
        return productType.toDTO()
    }

    @PostMapping("/product-types")
    @ResponseBody
    fun postProductType(@RequestBody productTypeDTO: ProductTypeDTO): ProductTypeDTO {
        val productType = productTypeDTO.toEntity()
        return productTypeRepository.save(productType).toDTO()
    }

    @DeleteMapping("/product-types/{productTypeId}/constraints/{constraintId}")
    @ResponseBody
    fun deleteConstraint(@PathVariable productTypeId: Long, @PathVariable constraintId: Long) {
        val constraint = constraintEntityRepository
                .findById(constraintId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Non existing constraint with id " + constraintId)}
        if (constraint.productType.id != productTypeId) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Constraint " + constraintId + " does not belong to product type " + productTypeId)
        }
        constraintEntityRepository.delete(constraint)
    }

    @DeleteMapping("/product-types/{productTypeId}")
    @ResponseBody
    fun deleteProductType(@PathVariable productTypeId: Long) {
        val productType = productTypeRepository
                .findById(productTypeId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find product type with id  " + productTypeId) }
        productTypeRepository.delete(productType)
    }

    @GetMapping("/product-types/{productTypeId}/constraints")
    @ResponseBody
    fun getAllConstraints(@PathVariable productTypeId: Long): MultipleConstraintsResponse {
        val productType = productTypeRepository
                .findById(productTypeId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find product type with id  " + productTypeId) }

        val constraints = constraintEntityRepository
                .findByProductType(productType)
                .map { it -> it.toOutputDTO() }
                .toList()
        return MultipleConstraintsResponse(constraints)
    }

    @PostMapping("/product-types/{productTypeId}/constraints")
    @ResponseBody
    fun postConstraint(@PathVariable productTypeId: Long, @RequestBody constraintDTO: ConstraintDTO): ConstraintDTO? {
        // TODO: move this function to Service
        val productType = productTypeRepository
                .findById(productTypeId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find product type with id  " + productTypeId) }
        val attributeTypeId = constraintDTO.attributeTypeId ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "attribute type id not set")
        val attributeType = attributeTypeRepository
                .findById(attributeTypeId)
                .orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find attribute type with id  " + attributeTypeId) }
        if (constraintDTO.constraintType == ConstraintType.PRESENCE) {
            val constraint = ConstraintEntity(id = null, productType = productType, attributeType = attributeType, constraintType = constraintDTO.constraintType, internalData = "")
            return constraintEntityRepository.save(constraint).toOutputDTO()
        }
        if (constraintDTO.constraintType == ConstraintType.RANGE_FLOAT) {
            if (attributeType.type != AttributeValueType.FLOAT) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "RANGE_FLOAT constraints can only be set on float attributes")
            }
            val rangeFloat: List<Float> = constraintDTO.rangeFloatData ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "rangeFloatData field missing from rangeFloat constraint")
            if (rangeFloat.size != 2) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "rangeFloatData must have exactly two items")
            }
            if (rangeFloat[0] >= rangeFloat[1]) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "the elements of rangeFloatData must be sorted")
            }
            val objectMapper = ObjectMapper()
            val strData = objectMapper.writeValueAsString(rangeFloat)
            log.info(strData)
            val ent = ConstraintEntity(id = null, productType = productType, attributeType = attributeType, constraintType = constraintDTO.constraintType, internalData = strData)
            return constraintEntityRepository.save(ent).toOutputDTO()
        }
        if (constraintDTO.constraintType == ConstraintType.RANGE_INT) {
            if (attributeType.type != AttributeValueType.INT) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "RANGE_INT constraints can only be set on int attributes")
            }
            val rangeInt: List<Int> = constraintDTO.rangeIntData ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "rangeIntData field missing from rangeFloat constraint")
            if (rangeInt.size != 2) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "rangeIntData must have exactly two items")
            }
            if (rangeInt[0] >= rangeInt[1]) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "the elements of rangeIntData must be sorted")
            }
            val objectMapper = ObjectMapper()
            val strData = objectMapper.writeValueAsString(rangeInt)
            log.info(strData)
            val ent = ConstraintEntity(id = null, productType = productType, attributeType = attributeType, constraintType = constraintDTO.constraintType, internalData = strData)
            return constraintEntityRepository.save(ent).toOutputDTO()
        }
        if (constraintDTO.constraintType == ConstraintType.WHITELIST) {
            if (attributeType.type != AttributeValueType.STRING) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "WHITELIST constraints can only be set on string attributes")
            }
            val whiteList: List<String> = constraintDTO.whitelistData ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "whitelistData field missing from whitelist constraint")
            if (whiteList.isEmpty()) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "whitelistData cannot be empty")
            }
            val objectMapper = ObjectMapper()
            val strData = objectMapper.writeValueAsString(whiteList)
            log.info(strData)
            val ent = ConstraintEntity(id = null, productType = productType, attributeType = attributeType, constraintType = constraintDTO.constraintType, internalData = strData)
            return constraintEntityRepository.save(ent).toOutputDTO()
        }
        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "unexpected constraintType " + constraintDTO.constraintType)
        return null
    }
}