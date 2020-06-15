package com.expedia.s3.cars.framework.test.common.passwordvault;

//import com.expedia.s3.cars.framework.test.common.json.Json;
//import com.expedia.s3.cars.framework.test.common.json.JsonObject;
import org.testng.Assert;
import java.util.Map;

import java.io.IOException;

import static java.lang.Runtime.getRuntime;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by jiyu on 11/7/16.
 */
@SuppressWarnings("PMD")
public class PasswordVaultCurlRuntime
{
    public static final String URL_ENDPOINT_PV_TEST ="https://ewe-vault.test.expedia.com:8200";
    public static final String LABLE_SERVICETEST_ALL = "cars-supplyconnectivity-titanium-service-tests";
    public static final String URL_GETOAUTHTOKEN_TEST = "https://ewe-vault.test.expedia.com:8200/v1/auth/app-id/login";
    public static final String URL_GETSECRET_TEST_ROOT ="https://ewe-vault.test.expedia.com:8200/v1/secret";
    public static final String APP_ID = "6502b390-f110-40e3-96b5-cd2754150961";
    public static final String USER_ID = "44c5a109-c565-48bc-9574-4b976c77c167";
    public static final String PASSWORD_LABLE = "db_password";
    /*
        //  test data against old VAULT with UI existence vault info
        public static final String URL_ENDPOINT_PV_TEST ="https://store.test.expedia.com";
        public static final String LABLE_SERVICETEST_ALL = "car-shopping-web";
        public static final String URL_GETOAUTHTOKEN_TEST = "https://store.test.expedia.com/v1/auth/app-id/login";
        public static final String URL_GETSECRET_TEST_ROOT ="https://store.test.expedia.com/v1/secret";
        public static final String APP_ID = "77873694-a294-4cb6-ae40-af73fcfa8373";
        public static final String USER_ID = "b423d57e-56ed-46d7-bd74-c16766423b22";
        public static final String PASSWORD_LABLE = "testuser3_password";
    */
    //--------------------------------------------------------------------------
    public static final String SLASH = "/";
    public static final String APP_ID_LABLE = "app_id";
    public static final String USER_ID_LABLE = "user_id";

    public static final String LABLE_CLIENT_TOKEN = "client_token";
    public static final String LABLE_VAULT_TOKEN = "X-Vault-Token";
    public static final String LABLE_VAULT_AUTH = "auth";
    public static final String LABLE_VAULT_SECRET_DATA = "data";

    private PasswordVaultCurlRuntime() {}

    private static String authToken = null;
    private static String secretPassword = null;

    private static com.bettercloud.vault.VaultConfig vaultConfig = null;
    private Map<String, String> overrides = null;

    //  the curl commands running on terminal are very tricky due to the difference or nuiance in coomand.
//  private final static String AUTH_FORMAT = "curl -XPOST %1s -d \'{\"%2s\":\"%3s\",\"%4s\":\"%5s\"}\'";
    private final static String AUTH_FORMAT = "curl -XPOST %1s -d {\"%2s\":\"%3s\",\"%4s\":\"%5s\"}";
//  private final static String SECRET_FPRMAT = "curl -H \"%1s:%2s\" %3s";
    private final static String SECRET_FPRMAT0 = "curl -H %1s:%2s %3s";

    /*
        execution of curl xommand : curl anything
     */
    private static String executeCurlCommand(String command)
    {
        final StringBuilder output = new StringBuilder();

        try {
            final Process proc = getRuntime().exec(command);
            proc.waitFor();

            final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            while (true) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                output.append(line);
            }
        }
        catch (InterruptedException e) { }
        catch (IOException e) {}
        catch (Exception e) { }
        finally {
                return output.toString();
        }
    }

    /*
    curl -X POST https://ewe-vault.test.expedia.com:8200/v1/auth/app-id/login
        -d '{
        "app_id": "6502b390-f110-40e3-96b5-cd2754150961",
        "user_id": "44c5a109-c565-48bc-9574-4b976c77c167"
        }'
    */
    public static String getSQLAuthToken()
    {
        if (authToken == null) {

            try {
                final String getTokenCommand = String.format(AUTH_FORMAT,
                        URL_GETOAUTHTOKEN_TEST, APP_ID_LABLE, APP_ID, USER_ID_LABLE, USER_ID);

                final String responseJson = executeCurlCommand(getTokenCommand);
                final byte [] byteStream = responseJson.getBytes();
                final ObjectMapper objectMapper = new ObjectMapper();
                //convert json string to object
                final JsonNode nodeRoot = objectMapper.readTree(byteStream);
                final JsonNode nodeAuth = nodeRoot.path(LABLE_VAULT_AUTH);
                final JsonNode nodeToken = nodeAuth.path(LABLE_CLIENT_TOKEN);

                authToken = nodeToken.asText();
            }
            catch (Exception e) {}
        }

        Assert.assertTrue(authToken != null);
        return authToken;
    }

    /*
    curl -H "X-Vault-Token: ${VAULT_TOKEN}" https://ewe-vault.test.expedia.com:8200/v1/secret/cars-supplyconnectivity-titanium-service-tests
    */
    public static String getSQLSecretPassword()
    {
        if (secretPassword == null)
        {
            try {
                final String getSecretCommand = String.format(SECRET_FPRMAT0,
                        LABLE_VAULT_TOKEN,
                        getSQLAuthToken(),
                        URL_GETSECRET_TEST_ROOT + SLASH + LABLE_SERVICETEST_ALL);

                final String responseJson = executeCurlCommand(getSecretCommand);
                final byte [] byteStream = responseJson.getBytes();

                final ObjectMapper objectMapper = new ObjectMapper();
                final JsonNode nodeRoot = objectMapper.readTree(byteStream);
                final JsonNode nodeData = nodeRoot.path(LABLE_VAULT_SECRET_DATA);
                final JsonNode nodeSecret = nodeData.path(PASSWORD_LABLE);

                secretPassword = nodeSecret.asText();

            }
            catch (Exception e) {}
        }

        Assert.assertTrue(secretPassword != null);
        return secretPassword;
    }

}
