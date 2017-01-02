/*
   Copyright (C) 2016 by the original Authors.

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software Foundation,
   Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
*/
package software.coolstuff.springframework.owncloud.service.impl;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import software.coolstuff.springframework.owncloud.model.OwncloudUserDetails;

class OwncloudRestUserDetailsService extends AbstractOwncloudRestServiceImpl implements UserDetailsService {

  @Autowired
  private OwncloudUserDetailsConversionService conversionService;

  public OwncloudRestUserDetailsService(RestTemplateBuilder builder) {
    super(builder);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Validate.notBlank(username);

    Ocs.User user = exchange("/cloud/users/{user}", HttpMethod.GET, emptyEntity(), Ocs.User.class, username);
    return loadPreloadedUserByUsername(username, user);
  }

  OwncloudUserDetails loadPreloadedUserByUsername(String username, Ocs.User preloadedUser) throws UsernameNotFoundException {
    Ocs.Groups groups = exchange("/cloud/users/{user}/groups", HttpMethod.GET, emptyEntity(), Ocs.Groups.class, username);
    return conversionService.convert(username, preloadedUser, groups);
  }

}