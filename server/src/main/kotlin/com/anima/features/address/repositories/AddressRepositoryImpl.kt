package com.anima.features.address.repositories

import com.anima.features.address.entities.AddressEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class AddressRepositoryImpl(private val jpa: AddressJpaRepository) : AddressRepository {
    override fun findById(id: String): Optional<AddressEntity> = jpa.findById(id)
    override fun findAllByExhibitorId(exhibitorId: String): List<AddressEntity> = jpa.findAllByExhibitorId(exhibitorId)
    override fun save(address: AddressEntity): AddressEntity = jpa.save(address)
    override fun deleteById(id: String) = jpa.deleteById(id)
}

interface AddressJpaRepository : JpaRepository<AddressEntity, String> {
    fun findAllByExhibitorId(exhibitorId: String): List<AddressEntity>
}
