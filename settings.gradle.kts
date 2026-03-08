rootProject.name = "bleach-reiryoku"
// settings.gradle.kts
plugins {
    id("dev.scaffoldit") version "0.2.+"
}
hytale {
    manifest {
        Group = "Reiryoku Team"
        Name = "BleachReiryoku"
        Main = "com.bleachreiryoku.BleachReiryokuPlugin"
        IncludesAssetPack = true
    }
}