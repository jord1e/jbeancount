plugins {
    application
}

dependencies {
    implementation(project(":jbeancount"))
}

application {
    mainClass.set("nl.jrdie.beancount.test.Main")
}
