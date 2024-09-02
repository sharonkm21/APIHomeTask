package APIPackage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HomeTask_Webservices_Functional
{
    private static final String SOAP_URL = "http://www.dneonline.com/calculator.asmx";
    private static final String SOAP_ACTION_PREFIX = "http://tempuri.org/";

    public static void main(String[] args) throws Exception {

        callSoapService("Add", 10, 5);
        callSoapService("Subtract", 10, 5);
        callSoapService("Multiply", 10, 5);
        callSoapService("Divide", 10, 5);

        // Test edge cases like divide by zero
        callSoapService("Divide", 10, 0);
    }

    private static void callSoapService(String method, int intA, int intB) throws Exception {
        String soapAction = SOAP_ACTION_PREFIX + method;

        String soapRequest = generateSoapRequest(method, intA, intB);

        // Create HTTP client
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(SOAP_URL);

        // Set the request headers
        post.setHeader("Content-Type", "text/xml; charset=utf-8");
        post.setHeader("SOAPAction", soapAction);

        // Set the request body
        StringEntity requestEntity = new StringEntity(soapRequest);
        post.setEntity(requestEntity);

        // Send the request and get the response
        HttpResponse response = httpClient.execute(post);
        HttpEntity responseEntity = response.getEntity();

        // Print the response
        String result = EntityUtils.toString(responseEntity);
        System.out.println("Response for " + method + " with intA=" + intA + ", intB=" + intB + ":");
        System.out.println(result);
        System.out.println("-----------------------------------");

        httpClient.close();
    }

    private static String generateSoapRequest(String method, int intA, int intB) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soap:Body>\n" +
                "    <" + method + " xmlns=\"http://tempuri.org/\">\n" +
                "      <intA>" + intA + "</intA>\n" +
                "      <intB>" + intB + "</intB>\n" +
                "    </" + method + ">\n" +
                "  </soap:Body>\n" +
                "</soap:Envelope>";
    }
}
