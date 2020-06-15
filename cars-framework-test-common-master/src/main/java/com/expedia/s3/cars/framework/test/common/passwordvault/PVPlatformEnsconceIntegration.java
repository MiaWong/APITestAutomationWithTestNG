package com.expedia.s3.cars.framework.test.common.passwordvault;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.expedia.www.platform.ensconce.EnsconceException;
import com.expedia.www.platform.ensconce.KeyPath;
import com.expedia.www.platform.ensconce.Retriever;
import com.expedia.www.platform.ensconce.vault.VaultRetriever;
import com.expedia.www.platform.ensconce.vault.authenticator.AppRoleAuthenticator;
import com.expedia.www.platform.ensconce.vault.authenticator.Authenticator;
import com.expedia.www.platform.ensconce.vault.wrapper.VaultWrapper;
import com.expedia.www.platform.ensconce.vault.wrapper.VaultWrapperImpl;
import org.eclipse.jetty.util.StringUtil;
import org.testng.Assert;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by jiyu on 11/10/16.
 */
@SuppressWarnings("PMD")
public class PVPlatformEnsconceIntegration
{
    private final Function<VaultConfig, VaultWrapper> vaultWrapperFactory;
    private final BiFunction<Authenticator, VaultWrapper, Retriever> retrieverFactory;

    private static String secretUserName = null;
    private static String secretPassword = null;
    private static String secretSCSUserName = null;
    private static String secretSCSPassword = null;
    private static String secretUserDomain = null;

    public PVPlatformEnsconceIntegration() {
        this(VaultWrapperImpl::new, VaultRetriever::new);
    }

    /**
     * @param vaultWrapperFactory  used to construct a {@link VaultWrapper}
     * @param retrieverFactory     used to construct a {@link Retriever}
     */
    public PVPlatformEnsconceIntegration(Function<VaultConfig, VaultWrapper> vaultWrapperFactory,
                                                    BiFunction<Authenticator, VaultWrapper, Retriever> retrieverFactory) {
        this.vaultWrapperFactory = vaultWrapperFactory;
        this.retrieverFactory = retrieverFactory;
    }


   public static String getSQLSecretPassword(String pemResource, String vaultUrl, String roleId, String secretId, String secretApp
           , String secretDomain,String secretUser,String secretKey, String secretscsUser,String secretscsKey)
    {
        if (pemResource == null) {
            pemResource = PVPlatformEnsconceIntegrationValues.getCertResource();
        }
       Assert.assertNotNull(pemResource);
        if (secretPassword == null) {
            try {

                final Map<String, String> keyValueMap = getStringStringMap(pemResource, vaultUrl, roleId, secretId, secretApp);

                if (StringUtil.isNotBlank(secretDomain)) {
                    secretUserDomain = String.valueOf(keyValueMap.get(secretDomain));
                }
                secretUserName = String.valueOf(keyValueMap.get(secretUser));
                secretPassword = String.valueOf(keyValueMap.get(secretKey));
                secretSCSUserName = String.valueOf(keyValueMap.get(secretscsUser));
                secretSCSPassword = String.valueOf(keyValueMap.get(secretscsKey));
            }
            catch (EnsconceException e) {
            Assert.fail(e.getMessage());
            }
            catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        }

        if (StringUtil.isNotBlank(secretDomain)) {
            Assert.assertNotNull(secretUserDomain);
        }
        Assert.assertNotNull(secretUserName );
        Assert.assertNotNull(secretPassword );
        Assert.assertNotNull(secretSCSUserName );
        Assert.assertNotNull(secretSCSPassword );
        return secretPassword;
    }

    private static Map<String, String> getStringStringMap(String pemResource, String vaultUrl, String roleId, String secretId, String secretApp) throws VaultException {
        Authenticator appRoleAuthenticator = new AppRoleAuthenticator(roleId, secretId);
        VaultConfig vaultConfig = new VaultConfig().sslConfig(new SslConfig().pemResource("/" + pemResource)).address(vaultUrl).build();
        PVPlatformEnsconceIntegration vault = new PVPlatformEnsconceIntegration();
        VaultWrapper vaultWrapper = vault.vaultWrapperFactory.apply(vaultConfig);
        Retriever retriever = vault.retrieverFactory.apply(appRoleAuthenticator, vaultWrapper);
        return retriever.get(new KeyPath(secretApp));
    }


    public static String getSQLSecretPassword(String pemResource, String vaultUrl, String roleId, String secretId, String secretApp, String secretDomain,String secretUser,String secretKey)
    {
        if (pemResource == null) {
            pemResource = PVPlatformEnsconceIntegrationValues.getCertResource();
        }
        Assert.assertNotNull(pemResource);
        if (secretPassword == null) {
            try {

                final Map<String, String> keyValueMaps = getStringStringMap(pemResource, vaultUrl, roleId, secretId, secretApp);

                if (StringUtil.isNotBlank(secretDomain)) {
                    secretUserDomain = String.valueOf(keyValueMaps.get(secretDomain));
                }
                secretUserName = String.valueOf(keyValueMaps.get(secretUser));
                secretPassword = String.valueOf(keyValueMaps.get(secretKey));
            }
            catch (EnsconceException e) {
                Assert.fail(e.getMessage());
            }
            catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        }

        if (StringUtil.isNotBlank(secretDomain)) {
            Assert.assertNotNull(secretUserDomain);
        }
        Assert.assertNotNull(secretUserName );
        Assert.assertNotNull(secretPassword );
        return secretPassword;
    }

    public static String getSQLSecretDomain()
    {
        return secretUserDomain;
    }

    public static String getSQLSecretUser()
    {
        return secretUserName;
    }

    public static String getSCSSQLSecretUser()
    {
        return secretSCSUserName;
    }

    public static String getSCSSQLSecretPassword()
    {
        return secretSCSPassword;
    }
}

