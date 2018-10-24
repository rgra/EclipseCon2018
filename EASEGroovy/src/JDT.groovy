organizeImports= {project->
	org.eclipse.ui.IWorkbenchPart view = getActiveView();

	org.eclipse.jdt.ui.actions.OrganizeImportsAction  action = new org.eclipse.jdt.ui.actions.OrganizeImportsAction(view.getSite());
	org.eclipse.jdt.core.IJavaProject  javaProject = org.eclipse.jdt.core.JavaCore.create(project);
	org.eclipse.jdt.core.IPackageFragment[]  packages = javaProject.getPackageFragments();
	for(int i = 0 ;i < packages.length; i++){
		org.eclipse.jdt.core.IPackageFragment  packag = packages[i];
		if(!packag.isReadOnly() && packag.containsJavaResources()){
			org.eclipse.jdt.core.ICompilationUnit[]  units = packag.getCompilationUnits();
			if(units != null){
				println("run on: " +packag.getElementName());
				action.runOnMultiple(units);
			}
		}
	}
};

executeInUI = { code -> org.eclipse.swt.widgets.Display.getDefault().syncExec(code)};

getJavaProjectSelection ={
	->
	Object selection = getSelection("Package Explorer");
	if (selection instanceof org.eclipse.jface.viewers.IStructuredSelection) {
		selection = selection.getFirstElement();
	}
	org.eclipse.jdt.core.IJavaProject javaProject = adapt(selection, org.eclipse.jdt.core.IJavaProject.class)
	javaProject.getPath()
	if (javaProject == null) {
		println("Not a Java Project")
		exit()
	}
	return javaProject;
};
