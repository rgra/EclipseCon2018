//name:Script Locations
//menu:Package Explorer

loadModule('/System/Platform');
loadModule('/System/Resources');

addScriptLocation("workspace://EASEGroovy/src")
addScriptLocation("workspace://EASE/script")
addScriptLocation("workspace://EASEJS/scripts")


private addScriptLocation(location) {
	def node = location.replace("/", "|");
	//println(node);

	// verify that location is not already registered
	def storedLocation = readPreferences("org.eclipse.ease.ui.scripts/" + node, "location");
	println(storedLocation)
	if (storedLocation == "") {
		// add location preferences
		writePreferences("org.eclipse.ease.ui.scripts/" + node, "location", null);
		writePreferences("org.eclipse.ease.ui.scripts/" + node, "default", false);
		writePreferences("org.eclipse.ease.ui.scripts/" + node, "recursive", true);
	}

	def repositoryService = getService(org.eclipse.ease.ui.scripts.repository.IRepositoryService);
	repositoryService.addLocation(location, false, true);
}
