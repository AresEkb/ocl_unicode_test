package ocl_unicode_test;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ocl.pivot.ExpressionInOCL;
import org.eclipse.ocl.pivot.utilities.OCL;
import org.eclipse.ocl.pivot.utilities.OCLHelper;
import org.eclipse.ocl.pivot.utilities.ParserException;
import org.eclipse.ocl.xtext.essentialocl.EssentialOCLStandaloneSetup;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;

public class Main {

    public static void main(String[] args) {
        final String input = "model/My.uml";
        
        System.out.println("Initialization");
        ResourceSet rs = new ResourceSetImpl();
        rs.setURIConverter(new CustomURIConverter());
        
        EssentialOCLStandaloneSetup.doSetup();
        org.eclipse.ocl.pivot.uml.UMLStandaloneSetup.init();
        
        OCL ocl = OCL.newInstance(rs);

        System.out.println("Loading UML model " + input);
        Resource resource = ocl.getResourceSet().getResource(createFileURI(input), true);

        Model uml = (Model)EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.eINSTANCE.getModel());
        System.out.println("Root: " + uml);
        
        System.out.println("\n\nTest 1\n");
        try {
            Class person = (Class)uml.getPackagedElement("Person");
            System.out.println("Person: " + person);

            org.eclipse.ocl.pivot.Class personAS = ocl.getMetamodelManager().getASOf(org.eclipse.ocl.pivot.Class.class, person);
            System.out.println("PersonAS: " + personAS);

            OCLHelper helper = ocl.createOCLHelper(personAS);
            System.out.println("Helper: " + helper);
            System.out.println("Context: " + helper.getContextClass());
            ExpressionInOCL expr = ocl.createInvariant(personAS, "fullName.matches('[Р-пр-џ]+')");
            System.out.println("Expression: " + expr);
            
            for (Constraint rule : person.getOwnedRules()) {
                ExpressionInOCL expr2 = getExpressionInOCL(ocl, rule);
                System.out.println("Expression: " + expr2);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        ocl.dispose();
    }

    private static URI createFileURI(String relativePath)
    {
        return URI.createFileURI(new File(relativePath).getAbsolutePath());
    }

    public static ExpressionInOCL getExpressionInOCL(OCL ocl, Constraint constraint) throws ParserException
    {
        org.eclipse.ocl.pivot.Constraint asConstraint = ocl.getMetamodelManager().getASOf(org.eclipse.ocl.pivot.Constraint.class, constraint);
        return ocl.getSpecification(asConstraint);
    }
}
