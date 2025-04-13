import toni.blahaj.setup.annotationProcessor
import toni.blahaj.setup.compileOnly
import toni.blahaj.setup.implementation
import toni.blahaj.setup.include

plugins {
	id("toni.blahaj")
}

blahaj {
	config {}
	setup {
		deps.implementation(modrinth("sodium", "mc1.21.5-0.6.13-neoforge"))

		deps.compileOnly(deps.annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:0.2.0-beta.6")!!)
		deps.include(deps.implementation("com.github.bawnorton.mixinsquared:mixinsquared-neoforge:0.2.0-beta.6")!!)!!

		deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-api-base:0.4.42+d1308ded19")
		deps.compileOnly("net.caffeinemc:fabric-renderer-api-v1:6.0.0")
		deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-rendering-data-attachment-v1:0.3.48+73761d2e19")
		deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-block-view-api-v2:1.0.10+9afaaf8c19")
	}
}