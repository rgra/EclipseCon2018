//name:Organize Imports
//popup:enableFor(org.eclipse.jdt.core.IJavaProject)

loadModule("/System/Resources")
loadModule('/System/UI');
loadModule('/System/Platform');

include("script://JDT.groovy")
//println(organizeImportsCall.dump())
def selection = getJavaProjectSelection();
//println(selection.getClass().getMethods())
executeInUI({organizeImports(selection.getProject())});
//executeUI("organizeImports(getJavaProjectSelection().getProject())");


