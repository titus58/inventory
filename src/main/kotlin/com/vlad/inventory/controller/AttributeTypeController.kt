package com.vlad.inventory.controller

import com.vlad.inventory.model.AttributeType
import com.vlad.inventory.model.MultipleAttributeTypesResponse
import com.vlad.inventory.service.AttributeTypeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.server.ResponseStatusException

@Controller
class AttributeTypeController {

    @Autowired
    lateinit var attributeTypeRepository: AttributeTypeRepository

    @GetMapping("/attribute-types")
    @ResponseBody
    fun getAllAttributeTypes(): MultipleAttributeTypesResponse {
        return MultipleAttributeTypesResponse(attributeTypeRepository.findAll().toList())
    }

    @PostMapping("/attribute-types")
    @ResponseBody
    fun postAttributeType(@RequestBody attributeType: AttributeType): AttributeType {
        if (attributeType.name.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Attribute name is missing")
        }
        return attributeTypeRepository.save(attributeType)
    }
}