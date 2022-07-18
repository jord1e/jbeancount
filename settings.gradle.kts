pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "beancount-parser"

include("jbeancount")
include("jbeancount-cli")
include("test-dont-push")
