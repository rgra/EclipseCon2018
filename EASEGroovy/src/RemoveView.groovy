//name: Remove E4 View
//popup: enableFor(org.eclipse.jdt.core.ICompilationUnit)

loadModule('/System/UI');
loadModule('/System/Platform');
include("script://E4.groovy")

Object selection = getSelection("Package Explorer");
if (selection instanceof org.eclipse.jface.viewers.IStructuredSelection) {
	selection = selection.getFirstElement();
}
org.eclipse.jdt.core.ICompilationUnit unit = adapt(selection,org.eclipse.jdt.core.ICompilationUnit.class)

if(unit == null) {
	exit()
}

def packageName = unit.getParent().getElementName()
def projectName = unit.getJavaProject().getElementName()
String className = packageName+"."+unit.getElementName()
className = className.replace(".java","").replace("/",".")
println(className)

def plugin = projectName

removeView(plugin,"io.github.rgra.rcp4app.partstack.sample",className)






