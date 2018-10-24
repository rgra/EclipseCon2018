//name:Cleanup
//menu:Package Explorer 

loadModule('/System/Platform');
loadModule('/System/Resources');

createOrganizeImportsTestFiles()
refreshResource("workspace://TestProject");

deleteProject("workspace://de.mekos.test");
deleteFile("workspace://EASEJS/scripts/First.js")
deleteFile("workspace://EASEGroovy/src/First.groovy")
deleteFile("workspace://EASEGroovy/src/RemoveView.groovy")
copyFile("workspace://EASEGroovy/backup/E4_small.groovy","workspace://EASEGroovy/src/E4.groovy")
refreshResource("workspace://EASEGroovy");

deleteFile("workspace://io.github.rgra.rcp4plugin/src/io/github/rgra/rcp4plugin/parts/MyView.java")
deleteFile("workspace://io.github.rgra.rcp4plugin/fragment.e4xmi")
deleteFile("workspace://io.github.rgra.rcp4plugin/plugin.xml")
replaceBuildProps()
refreshResource("workspace://io.github.rgra.rcp4plugin");

deleteFile("workspace://EASE/script/Add println Tasks.js")
refreshResource("workspace://EASE");

cleanUpScriptLocation("workspace://EASEGroovy/src")
cleanUpScriptLocation("workspace://EASE/script")
cleanUpScriptLocation("workspace://EASEJS/scripts")

updateScriptLocations();

private replaceBuildProps() {
def template ='''source.. = src/
output.. = bin/
bin.includes = META-INF/,\
			   .,\
			   icons/
'''
writeFile(new File(getWorkspace().getLocation().toString(),"io.github.rgra.rcp4plugin/build.properties"),template)
}

private createOrganizeImportsTestFiles() {
def template ='''
package {package};

public class {className} {
	
	public static void main(String[] args) {
		ArrayList<String> list;
	}

}
'''
writeFile(new File(getWorkspace().getLocation().toString(),"TestProject/src/io/github/rgra/imports/TestOrganizeImports1.java"),template.replace("{package}", "io.github.rgra.imports").replace("{className}", "TestOrganizeImports1"))
writeFile(new File(getWorkspace().getLocation().toString(),"TestProject/src/io/github/rgra/imports/sub/TestOrganizeImports2.java"),template.replace("{package}", "io.github.rgra.imports.sub").replace("{className}", "TestOrganizeImports2"))
}


private cleanUpScriptLocation(location) {
	def node = location.replace("/", "|");
	//println(node);

	// verify that location is not already registered
	def storedLocation = readPreferences("org.eclipse.ease.ui.scripts/" + node, "location");
	println(storedLocation)
	if (storedLocation == "") {

	}else {
		// add location preferences
		writePreferences("org.eclipse.ease.ui.scripts/" + node, "location", null);
		writePreferences("org.eclipse.ease.ui.scripts/" + node, "default", false);
		writePreferences("org.eclipse.ease.ui.scripts/" + node, "recursive", true);
	}
}

private updateScriptLocations() {
	// trigger update
	def repositoryService = getService(org.eclipse.ease.ui.scripts.repository.IRepositoryService);
	repositoryService.update(true);
}