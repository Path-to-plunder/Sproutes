package com.casadetasha.kexp.sproute.annotations

sealed class AuthConfig {
    class Secured(val type: String) : AuthConfig()

    class Unsecured() : AuthConfig() {
        override fun equals(other: Any?): Boolean {
            return other is Unsecured
        }

        override fun hashCode(): Int {
            return AuthConfig::class.hashCode()
        }
    }

    class Default() : AuthConfig() {
        override fun equals(other: Any?): Boolean {
            return other is Default
        }

        override fun hashCode(): Int {
            return AuthConfig::class.hashCode()
        }
    }
}