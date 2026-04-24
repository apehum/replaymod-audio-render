package com.apehum.replayaudio

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.semver4j.Semver
import org.semver4j.range.RangeListFactory
import java.util.concurrent.CompletableFuture

object VersionResolver {
    private val client =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
        }
    private val mutex = Mutex()

    private var response: VersionManifest? = null

    fun getMinecraftVersionsInRange(
        type: String,
        versionRange: String,
    ): CompletableFuture<List<Version>> =
        getMinecraftVersions()
            .thenApply { versions ->
                val range = RangeListFactory.create(versionRange)
                versions
                    .filter { it.type == type }
                    .mapNotNull { version ->
                        val versionId =
                            if (version.id.split(".").size < 3) {
                                version.id + ".0"
                            } else {
                                version.id
                            }

                        Semver
                            .parse(versionId)
                            ?.let { it to version }
                    }.filter { range.isSatisfiedBy(it.first) }
                    .map { it.second }
            }

    fun getMinecraftVersions(): CompletableFuture<List<Version>> =
        CoroutineScope(Dispatchers.Default).future {
            mutex.withLock {
                response?.let { return@future it.versions }

                val manifest: VersionManifest = client.get("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json").body()
                response = manifest

                manifest.versions
            }
        }

    @Serializable
    data class VersionManifest(
        val versions: List<Version>,
    )

    @Serializable
    data class Version(
        val id: String,
        val type: String,
    )
}
