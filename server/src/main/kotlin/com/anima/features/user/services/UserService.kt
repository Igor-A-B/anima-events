package com.anima.features.user.services

import at.favre.lib.crypto.bcrypt.BCrypt
import com.anima.features.auth.repositories.RefreshTokenRepository
import com.anima.features.user.dtos.MeResponseDto
import com.anima.features.user.exceptions.IncorrectPasswordException
import com.anima.features.user.models.AccountType
import com.anima.features.user.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
    }

    // the account type comes from the access token, so it is not looked up again
    fun me(userId: UUID, accountType: AccountType): MeResponseDto {
        val user = userRepository.findById(userId).orElseThrow { NoSuchElementException("User not found") }
        return MeResponseDto(user.name, user.email, accountType, user.registerDate.toString())
    }

    // new password and token cleanup commit together, any failure rolls both back
    @Transactional(rollbackFor = [Exception::class])
    fun changePassword(userId: UUID, currentPassword: String, newPassword: String) {
        require(newPassword.length >= MIN_PASSWORD_LENGTH) { "Password must have at least $MIN_PASSWORD_LENGTH characters" }

        val user = userRepository.findById(userId).orElseThrow { NoSuchElementException("User not found") }
        val currentOk = BCrypt.verifyer().verify(currentPassword.toCharArray(), user.passwordHash).verified
        if (!currentOk) throw IncorrectPasswordException()

        user.passwordHash = BCrypt.withDefaults().hashToString(10, newPassword.toCharArray())
        userRepository.save(user)
        // ends every session, the app signs in again with the new password
        refreshTokenRepository.deleteAllByUserId(userId)
    }
}
