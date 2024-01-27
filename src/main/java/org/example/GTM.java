package org.example;

/**
 * Access and manage a Google Tag Manager account.
 */

import com.google.analytics.admin.v1beta.AnalyticsAdminServiceSettings;
import com.google.analytics.admin.v1beta.ConversionEvent;
import com.google.analytics.admin.v1beta.CreateConversionEventRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.tagmanager.TagManager;
import com.google.api.services.tagmanager.TagManagerScopes;
import com.google.api.services.tagmanager.model.Condition;
import com.google.api.services.tagmanager.model.Parameter;
import com.google.api.services.tagmanager.model.Tag;
import com.google.api.services.tagmanager.model.Trigger;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GTM {
    // Path to client_secrets.json file downloaded from the Developer's Console.
    // The path is relative to HelloWorld.java.
    private static final String CLIENT_SECRET_JSON_RESOURCE = "credentials.json";

    // The directory where the user's credentials will be stored for the application.
    private static final String APPLICATION_NAME = "HelloWorld";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String accountId = "xxxx2952";
    private static final String containerId = "xxxxx3917";

    private static final String  propertyId = "xxxxx242";
    private static NetHttpTransport httpTransport;

    public static void main(String[] args) {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleCredential credential = GoogleCredential
                    .fromStream(new FileInputStream(CLIENT_SECRET_JSON_RESOURCE))
                    .createScoped(Collections.singleton(TagManagerScopes.TAGMANAGER_EDIT_CONTAINERS));

            TagManager manager = new TagManager.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();

           Trigger trigger = createTrigger("whatsapp-mario-lopez","linkClick","/doctores/mario-lopez" ,manager);
           List<String> firingTriggerId = new ArrayList<>();
           firingTriggerId.add(trigger.getTriggerId());
           System.out.println("trigger id: "+trigger.getTriggerId());

           Tag tag = createTag("whatsapp_mario_lopez","whatsapp_mario_lopez","gaawe",firingTriggerId, manager);
            System.out.println("tag id: "+tag.getTagId());
            System.out.println("tag name: "+tag.getName());

            AnalyticsAdminServiceClient analyticsAdminServiceClient = createAnalyticsAdmin();

            ConversionEvent conversionEvent = createConversionEvent(analyticsAdminServiceClient, tag.getName(), tag.getName());
            System.out.println("event name: " + conversionEvent.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ConversionEvent createConversionEvent(AnalyticsAdminServiceClient analyticsAdmin, String name, String eventName){
        ConversionEvent conversionEvent = ConversionEvent.newBuilder()
                .setName(name)
                .setEventName(eventName)
                .build();

        // Construye el objeto CreateConversionEventRequest
        CreateConversionEventRequest request = CreateConversionEventRequest.newBuilder()
                .setParent(String.format("properties/%s", propertyId))
                .setConversionEvent(conversionEvent)
                .build();

        // Llama al método de la API para crear la conversión de evento
        ConversionEvent createdEvent = analyticsAdmin.createConversionEvent(request);
        return createdEvent;
    }

    private static  Trigger createTrigger(String name, String type, String pagePath, TagManager service) throws IOException {
        Trigger trigger = new Trigger();
        trigger.setName(name);
        trigger.setType(type);

        /*condition 1*/

        List<Parameter> parameterList1 = new ArrayList<>();
        Condition equal = new Condition();
        Parameter pagePathParameter = new Parameter();
        pagePathParameter.setType("template");
        pagePathParameter.setKey("arg0");
        pagePathParameter.setValue("{{Page Path}}");

        Parameter urlDoctorParameter = new Parameter();
        urlDoctorParameter.setType("template");
        urlDoctorParameter.setKey("arg1");
        urlDoctorParameter.setValue(pagePath);

        parameterList1.add(pagePathParameter);
        parameterList1.add(urlDoctorParameter);

        equal.setParameter(parameterList1);
        equal.setType("equals");

        /*condition 2*/
        List<Parameter> parameterList2 = new ArrayList<>();
        Condition contains = new Condition();
        contains.setType("contains");
        Parameter clickUrlParameter = new Parameter();
        clickUrlParameter.setType("template");
        clickUrlParameter.setKey("arg0");
        clickUrlParameter.setValue("{{Click URL}}");

        Parameter whatsappParameter = new Parameter();
        whatsappParameter.setType("template");
        whatsappParameter.setKey("arg1");
        whatsappParameter.setValue("wa");

        parameterList2.add(clickUrlParameter);
        parameterList2.add(whatsappParameter);

        contains.setParameter(parameterList2);

        /* add conditions to filter */


        List<Condition> conditions = new ArrayList<>();

        conditions.add(equal);
        conditions.add(contains);

        /*trigger settings*/
        Parameter checkValidation = new Parameter();
        checkValidation.setType("boolean");
        checkValidation.setValue("false");

        Parameter waitForTags = new Parameter();
        waitForTags.setType("boolean");
        waitForTags.setValue("false");


        trigger.setFilter(conditions);
        trigger.setCheckValidation(checkValidation);
        trigger.setWaitForTags(waitForTags);

        trigger = service.accounts().containers().triggers().create(accountId, containerId, trigger).execute();
        return trigger;

    }
    private static Tag createTag(String name, String tagName, String type,List<String> firingTriggerId ,TagManager service) throws IOException {
        Tag ua = new Tag();
        ua.setName(tagName);
        ua.setType(type);

        List<Parameter> uaParams = new ArrayList<>();
        Parameter measurementIdOverride = new Parameter();
        measurementIdOverride.setKey("measurementIdOverride");
        measurementIdOverride.setValue("{{GA4 - ID de medicion}}");
        measurementIdOverride.setType("template");

        Parameter eventName = new Parameter();
        eventName.setKey("eventName");
        eventName.setValue(name);
        eventName.setType("template");

        uaParams.add(measurementIdOverride);
        uaParams.add(eventName);

        ua.setParameter(uaParams);
        ua.setTagFiringOption("oncePerEvent");
        ua.setFiringTriggerId(firingTriggerId);
        ua = service.accounts().containers().tags().create(accountId, containerId, ua)
                .execute();

        return ua;
    }

    private static AnalyticsAdminServiceClient createAnalyticsAdmin() throws IOException {
        InputStream fileInputStream = new FileInputStream(CLIENT_SECRET_JSON_RESOURCE);
        GoogleCredentials credential = GoogleCredentials.fromStream(fileInputStream);

        AnalyticsAdminServiceSettings analyticsAdminServiceSettings = AnalyticsAdminServiceSettings
                .newHttpJsonBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credential)).build();

        AnalyticsAdminServiceClient analyticsAdminServiceClient = null;
        try {
            analyticsAdminServiceClient = AnalyticsAdminServiceClient.create(analyticsAdminServiceSettings);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return analyticsAdminServiceClient;
    }



}


