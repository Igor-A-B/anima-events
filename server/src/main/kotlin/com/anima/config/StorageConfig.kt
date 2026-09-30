package com.anima.config

import com.google.cloud.NoCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

// firebase storage is a gcs bucket, so the plain gcs client talks to it (and to the emulator)
@Configuration
class StorageConfig {

    @Bean
    fun storage(
        @Value($$"${storage.project-id}") projectId: String,
        @Value($$"${storage.emulator-host:}") emulatorHost: String,
    ): Storage =
        if (emulatorHost.isNotBlank()) {
            StorageOptions.newBuilder()
                .setHost(emulatorHost)
                .setProjectId(projectId)
                .setCredentials(NoCredentials.getInstance())
                .build()
                .service
        } else {
            // application default credentials (service account) outside of local dev
            StorageOptions.newBuilder().setProjectId(projectId).build().service
        }
}
