package medrechackathon.medlist.coordinator;

import org.hl7.fhir.dstu3.model.Base;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;

public class EHR2 implements FHIRClient {
	public static void main(String[] args) {
	FhirContext ctx = FhirContext.forDstu3();
	String serverBase1 = "https://fire-pit.mihin.org/po-ehr-1/baseDstu3";

	String serverBase2 = "https://fire-pit.mihin.org/po-ehr-2/baseDstu3";
	String serverBase3 = "https://fire-pit.mihin.org/po-ehr-3/baseDstu3";
	//String serverBaseOpenEMR = "https://fire-pit.mihin.org/po-ehr-2/baseDstu3";
	 
	// Create an HTTP basic auth interceptor
	String username = "";
	String password = "";
	IClientInterceptor authInterceptor = new BasicAuthInterceptor(username, password);
	IGenericClient client = ctx.newRestfulGenericClient(serverBase1);
	client.registerInterceptor(authInterceptor);
	
	// Perform a search
	Bundle results = client
	      .search()
	      .forResource(Patient.class)
	      .where(Patient.GIVEN.matches().value("Millie"))
	      .returnBundle(Bundle.class)
	      .encodedJson()
	      .execute();
	 
	//Patient p = results.castToResource(Base b);
	
	System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(results));
	}
}
