package org.github.chinlinlee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hl7.fhir.r5.formats.JsonParser;
import org.hl7.fhir.r5.model.OperationOutcome;

public class Caller {
    private final Validator validator;

    public Caller(String... igDir) {
        this.validator = initializeValidator(igDir);
    }

    /**
     * Only used for getting the FHIR artifacts cached.
     */
    private static Validator initializeValidator(String... igDir) {
        try {
            System.out.println("Initializing Validator App...");
            if (igDir.length > 0 ) {
              return new Validator(igDir[0]);
            }
            return new Validator("./igs");
        } catch (Exception e) {
            System.out.print("There was an error initializing the validator: ");
            System.out.println(e.toString());
            System.exit(1);
            return null; // unreachable
        }
    }

    /**
     * Lists the names of resources defined for this version of the validator.
     *
     * @return a sorted list of distinct resource names
     */
    public List<String> getResources() {
        return this.validator.getResources();
    }

    /**
     * Lists the StructureDefinitions loaded in the validator.
     *
     * @return a sorted list of distinct structure canonicals
     */
    public List<String> getStructures() {
        return this.validator.getStructures();
    }

    /**
     * Load a profile into the validator.
     *
     * @param profile the profile to be loaded
     */
    public void loadProfile(String profile) throws IOException {
        this.validator.loadProfile(profile);
    }


    public String validateResource(String resource, String profile) throws Exception {
        byte[] resourceBytes = resource.getBytes();
        return this.validateResource(resourceBytes, profile);
    }

    public String validateResource(byte[] resource, String profile) throws Exception {
        List<String> patientProfiles;
        if (profile != null) {
            patientProfiles = Arrays.asList(profile.split(","));
        } else {
            patientProfiles = new ArrayList<String>();
        }

        OperationOutcome oo = validator.validate(resource, patientProfiles);
        return new JsonParser().composeString(oo);
    }

    /**
     * For checking program load this instance successful
     * @return Hello world
     */
    public String printTest() {
        return "Hello world!";
    }

    public static void main(String[] args) throws Exception {
        Caller testCall = new Caller();
        String patientResource = "{\"resourceType\":\"Patient\",\"id\":\"pat-example-tw-1\",\"meta\":{\"profile\":[\"https://twcore.mohw.gov.tw/fhir/StructureDefinition/Patient-twcore\"]},\"name\":[{\"use\":\"official\",\"text\":\"王大明\",\"family\":\"WANG\",\"given\":[\"DA-MING\"]}],\"gender\":\"male\",\"birthDate\":\"1990-01-01\"}";
        try {
            String result = testCall.validateResource(patientResource, "https://twcore.mohw.gov.tw/fhir/StructureDefinition/Patient-twcore123");
            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
