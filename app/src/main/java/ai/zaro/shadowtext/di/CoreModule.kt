package ai.zaro.shadowtext.di

import ai.zaro.shadowtext.core.encoding.InvisibleEncoder
import ai.zaro.shadowtext.core.encoding.ZeroWidthEncoder
import ai.zaro.shadowtext.core.engine.StegoDecoder
import ai.zaro.shadowtext.core.engine.StegoEncoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    @Provides @Singleton fun provideInvisibleEncoders(): List<InvisibleEncoder> = listOf(ZeroWidthEncoder())
    @Provides @Singleton fun provideStegoEncoder(encoders: List<@JvmSuppressWildcards InvisibleEncoder>): StegoEncoder = StegoEncoder(encoder = encoders.first())
    @Provides @Singleton fun provideStegoDecoder(encoders: List<@JvmSuppressWildcards InvisibleEncoder>): StegoDecoder = StegoDecoder(encoders)
}
