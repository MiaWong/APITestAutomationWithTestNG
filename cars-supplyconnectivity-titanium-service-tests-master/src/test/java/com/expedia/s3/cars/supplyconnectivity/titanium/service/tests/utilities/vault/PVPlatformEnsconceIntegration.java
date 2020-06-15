package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.vault;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.VaultConfig;
import com.expedia.s3.cars.framework.test.common.passwordvault.PVPlatformEnsconceIntegrationValues;
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


   public  String getSQLSecretPassword(String pemResource, String vaultUrl, String roleId, String secretId, String secretApp, String secretDomain,String secretUser,String secretKey)
    {
        if(pemResource == null) {
            pemResource = PVPlatformEnsconceIntegrationValues.getCertResource();
        }

        Assert.assertNotNull(pemResource);

       if (secretPassword == null) {
            try {

                Authenticator appRoleAuthenticator = new AppRoleAuthenticator(roleId, secretId);
                VaultConfig vaultConfig = new VaultConfig().sslConfig(new SslConfig().pemResource("/" + pemResource)).address(vaultUrl).build();
                PVPlatformEnsconceIntegration vault = new PVPlatformEnsconceIntegration();
                VaultWrapper vaultWrapper = vault.vaultWrapperFactory.apply(vaultConfig);
                Retriever retriever = vault.retrieverFactory.apply(appRoleAuthenticator, vaultWrapper);
                final Map<String, String> keyValueMap = retriever.get(new KeyPath(secretApp));

                if (StringUtil.isNotBlank(secretDomain)) {
                    secretUserDomain = String.valueOf(keyValueMap.get(secretDomain));
                }
                secretUserName = String.valueOf(keyValueMap.get(secretUser));
                secretPassword = String.valueOf(keyValueMap.get(secretKey));
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

}
