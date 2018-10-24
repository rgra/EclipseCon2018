loadModule('/System/Resources');
loadModule('/System/UI');

createNewView = { String plugin, String parentStackId, String className, String label ->
	def workspaceLocation = getWorkspace().getLocation().toString()
	createViewClass(className, workspaceLocation, plugin)
	addViewToFragment(workspaceLocation,parentStackId,plugin,className,label)
	refreshResource("workspace://${plugin}");
	String pathOfClass = className.replace(".","/")
	def classFile = "workspace://${plugin}/src/${pathOfClass}.java"
	showEditor(classFile);
}

removeView = { String plugin, String parentStackId, String className->
	def workspaceLocation = getWorkspace().getLocation().toString()
	removeViewFromFragment(workspaceLocation,parentStackId,plugin,className)
	String pathOfClass = className.replace(".","/")
	def classFile = "workspace://${plugin}/src/${pathOfClass}.java"
	deleteFile(classFile)
	refreshResource("workspace://${plugin}");
}

void checkBinIncludes(java.io.File parentFile, String entryToCheck) {
	def file = new java.io.File(parentFile,"build.properties")

	def fileContent = readFile(file)

	if(!fileContent.contains(entryToCheck)) {
		fileContent = fileContent.replace("bin.includes =","bin.includes = ${entryToCheck},\\\n              ")
		writeFile(file, fileContent)
	}
}


void createFragmentIfNotExists(String uriPath, String plugin) {
	def file = new java.io.File(new java.net.URL(uriPath).toURI())
	if(! file.exists()) {
		createNewFragmentFile(file)

		def pluginFile = new java.io.File(file.getParentFile(),"plugin.xml")
		if(!pluginFile.exists()) {
			createNewPluginFile(plugin, pluginFile)
		}
		else {
			addFragmentToPluginFile(plugin, pluginFile)
		}
	}
}

private addFragmentToPluginFile(String plugin, File pluginFile) {
	def pluginTemplate = '''
   <extension
         id="${plugin}.fragment"
         point="org.eclipse.e4.workbench.model">
      <fragment
            apply="notexists"
            uri="fragment.e4xmi">
      </fragment>
   </extension>
</plugin>
'''
	pluginTemplate = pluginTemplate.replace("\${plugin}", plugin)
	def pluginFileContent = readFile(pluginFile)
	pluginFileContent = pluginFileContent.replace("</plugin>",pluginTemplate)

	if(!pluginFileContent.contains("fragment.e4xmi")) {
		writeFile(pluginFile, pluginFileContent)
	}
}

private createNewPluginFile(String plugin, File pluginFile) {
	def pluginTemplate = '''<?xml version="1.0" encoding="UTF-8"?>
<?eclipse version="3.4"?>
<plugin>

   <extension
         id="${plugin}.fragment"
         point="org.eclipse.e4.workbench.model">
      <fragment
            apply="notexists"
            uri="fragment.e4xmi">
      </fragment>
   </extension>

</plugin>
'''
	pluginTemplate = pluginTemplate.replace("\${plugin}", plugin)
	writeFile(pluginFile, pluginTemplate)

	checkBinIncludes(pluginFile.getParentFile(),"plugin.xml")
}

private createNewFragmentFile(File file) {
	createFile(file)
	def fragmentTemplate = '''<?xml version="1.0" encoding="ASCII"?>
<fragment:ModelFragments xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:basic="http://www.eclipse.org/ui/2010/UIModel/application/ui/basic" xmlns:fragment="http://www.eclipse.org/ui/2010/UIModel/fragment" xmi:id="_BxaXACerEeWxCPrV0pAZQQ">
</fragment:ModelFragments>
		'''
	writeFile(file, fragmentTemplate)
	checkBinIncludes(file.getParentFile(),"fragment.e4xmi")
}

void addViewToFragment(String workspaceLocation, String parentStackId, String plugin, String className, String label) {
	def uriPath ="file:////${workspaceLocation}/${plugin}/fragment.e4xmi";
	createFragmentIfNotExists(uriPath,plugin)

	org.eclipse.emf.ecore.resource.Resource res = loadResource(uriPath);
	org.eclipse.e4.ui.model.fragment.MModelFragments app = (org.eclipse.e4.ui.model.fragment.MModelFragments) res.getContents().get(0);
	//	for (fragment in app.getFragments()) {
	//		println(fragment)
	//	}

	def x = new org.eclipse.e4.ui.model.fragment.impl.StringModelFragmentImpl()
	x.setFeaturename("children")
	x.setParentElementId(parentStackId)

	app.getFragments().add(x);

	def part = org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory.INSTANCE.createPart()

	part.setElementId(className)
	part.setContributionURI("bundleclass://${plugin}/${className}")
	part.setLabel(label)
	part.setIconURI("platform:/plugin/${plugin}/icons/Sample.png")

	x.elements.add(part)
	//resourceSet.URIConverter.createOutputStream(arg0)
	res.save(new java.util.HashMap())
}

void removeViewFromFragment(String workspaceLocation, String parentStackId, String plugin, String className) {
	def uriPath ="file:////${workspaceLocation}/${plugin}/fragment.e4xmi";

	org.eclipse.emf.ecore.resource.Resource res = loadResource(uriPath);
	org.eclipse.e4.ui.model.fragment.MModelFragments app = (org.eclipse.e4.ui.model.fragment.MModelFragments) res.getContents().get(0);

	List<org.eclipse.e4.ui.model.fragment.MModelFragment> toRemove = new ArrayList();
	for (fragment in app.getFragments()) {
		for (element in fragment.elements) {
			if(element.getContributionURI().contains(className)) {
				toRemove.add(fragment);
			}
		}
	}

	if(!toRemove.isEmpty()) {
		app.getFragments().removeAll(toRemove);
		res.save(new java.util.HashMap())
	}
}

private org.eclipse.emf.ecore.resource.Resource loadResource(String uriPath) {
	org.eclipse.emf.ecore.resource.ResourceSet resourceSet = new org.eclipse.emf.ecore.resource.impl.ResourceSetImpl();

	org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl.init();
	org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI
			.createURI(uriPath);

	resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
			.put("e4xmi", new org.eclipse.e4.ui.internal.workbench.E4XMIResourceFactory());
	org.eclipse.emf.ecore.resource.Resource res = resourceSet.getResource(uri, true)
	return res
}

private createViewClass(String className, String workspaceLocation, String plugin) {
	def template = '''
package io.github.rgra.rcp4plugin.parts;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.widgets.Composite;

public class ${className} {

	@PostConstruct
	public void createPartControl(Composite parent) {
		System.out.println("Enter in ${className} postConstruct");
		//TODO
	}

	@Focus
	public void setFocus() {
		//TODO
	}

}
'''
	def simpleClassName = className.substring(className.lastIndexOf(".")+1)
	template = template.replace("\${className}", simpleClassName)
	String pathOfClass = className.replace(".","/")
	def classlocation = new java.io.File("${workspaceLocation}/${plugin}/src/${pathOfClass}.java")

	def file = createFile(classlocation)
	//println(file)
	writeFile(file, template)
}
