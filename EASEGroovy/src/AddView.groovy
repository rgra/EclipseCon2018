//name: Add E4 View
//description: Add a E4 View to existing package
//popup: enableFor(org.eclipse.jdt.core.IPackageFragment)

loadModule('/System/UI');
loadModule('/System/Platform');
include("script://E4.groovy")

Object selection = getSelection("Package Explorer");
if (selection instanceof org.eclipse.jface.viewers.IStructuredSelection) {
	selection = selection.getFirstElement();
}
org.eclipse.jdt.core.IPackageFragment packag = adapt(selection,org.eclipse.jdt.core.IPackageFragment.class)

if(packag == null) {
	exit()
}

def packageName = packag.getElementName()
def projectName = packag.getJavaProject().getElementName()
String className = "${packageName}.SampleView"
className = showInputDialog("FQ Classname", className, "Create new E4 View")

if(className.isEmpty()) {
	exit()
}
String label = "Test"
if(!className.contains(".")) {
	label = className
	className = packageName + "."+className
}
println(className)


def plugin = projectName

createNewView(plugin,"io.github.rgra.rcp4app.partstack.sample",className,label)






