package com.anima.features.address.repositories
import com.anima.features.address.entities.AddressEntity
import java.util.Optional

interface AddressRepository {
    fun findById(id: String): Optional<AddressEntity>
    fun findAllByExhibitorId(exhibitorId: String): List<AddressEntity>
    fun save(address: AddressEntity): AddressEntity
    fun deleteById(id: String)
}
