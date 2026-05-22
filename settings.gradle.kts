pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "delivery-msa"

include(
    "common",
    "eureka-server",
    "api-gateway",
    "user-service",
    "restaurant-service",
    "order-service",
    "payment-service",
    "delivery-service",
    "notification-service",
)
