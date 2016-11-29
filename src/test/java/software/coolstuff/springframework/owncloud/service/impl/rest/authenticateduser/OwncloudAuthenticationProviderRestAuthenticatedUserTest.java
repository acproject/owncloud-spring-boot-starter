package software.coolstuff.springframework.owncloud.service.impl.rest.authenticateduser;

import org.springframework.test.context.ActiveProfiles;

import software.coolstuff.springframework.owncloud.service.impl.rest.AbstractOwncloudAuthenticationProviderRestTest;

@ActiveProfiles("URL-TEST-AUTHENTICATED-USER")
public class OwncloudAuthenticationProviderRestAuthenticatedUserTest extends AbstractOwncloudAuthenticationProviderRestTest {

  @Override
  protected String getBasicAuthenticationHeaderForUserDetailsService() {
    return getSecurityContextBasicAuthorizationHeader();
  }

}
