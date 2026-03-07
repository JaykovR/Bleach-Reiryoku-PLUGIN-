plugins{
    id("java")
}

java{
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(files("server/HytaleServer.jar"))
}

tasks.test{
    useJUnitPlatform()
}

tasks.jar{
    destinationDirectory.set(file("C:\\Users\\jacob\\OneDrive\\Desktop"))
}