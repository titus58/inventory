package com.vlad.inventory.controller

import com.vlad.inventory.model.Owner
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class OwnerController {
    val log = LoggerFactory.getLogger(this::class.java)


    @GetMapping("/owners")
    @ResponseBody
    fun getAllOwners(): Owner {
        return Owner(name = "vlad",id = 0)
    }
}