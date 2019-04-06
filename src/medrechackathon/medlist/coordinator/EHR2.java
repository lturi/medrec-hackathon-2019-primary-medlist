package medrechackathon.medlist.coordinator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.hl7.fhir.dstu3.model.Base;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;

public class EHR2 {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
	String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	String appConfigPath = rootPath + "../fhirpit.properties";
	
	Properties props = new Properties();
	props.load(new FileInputStream(appConfigPath));
	
	FhirContext ctx = FhirContext.forDstu3();
	String serverBase1 = "https://fire-pit.mihin.org/po-ehr-1/baseDstu3";

	String serverBase2 = "https://fire-pit.mihin.org/po-ehr-2/baseDstu3";
	String serverBase3 = "https://fire-pit.mihin.org/po-ehr-3/baseDstu3";
	//String serverBaseOpenEMR = "https://fire-pit.mihin.org/po-ehr-2/baseDstu3";
	 
	// Create an HTTP basic auth interceptor
	String username = props.getProperty("fhir_pit_user");
	String password = props.getProperty("fhir_pit_pw");
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
	 
	Patient pt = client
		      .read()
		      .resource(Patient.class)
		      .withId((results.getEntryFirstRep()).getResource().getId())
		      .execute();
	
	//Patient p = results.castToResource(Base b);
	System.out.println("First Name: "+(pt.getName().get(0)).getGivenAsSingleString());
	System.out.println("Last Name: " + (pt.getName().get(0)).getFamily().toString());
	System.out.println("ID: " + pt.getIdBase());
	System.out.println("DOB: " + pt.getBirthDate().toString());
	
	DbConnector db = new DbConnector();
	db.connect();
	int count = db.getPtFromCentralDb(pt);
	if (count == 1) {
		System.out.println((pt.getName().get(0)).getGivenAsSingleString() + " is in the central db");
	} else {
		int pt_id = db.insertPt(pt);
		db.insertPtSource(pt_id, "po-ehr-1", pt, serverBase1);
		System.out.println((pt.getName().get(0)).getGivenAsSingleString() + " has been added to the central db");
	}
	
	//System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(results));
	}
}
