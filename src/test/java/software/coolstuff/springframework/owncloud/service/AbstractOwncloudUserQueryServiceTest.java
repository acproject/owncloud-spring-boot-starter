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
package software.coolstuff.springframework.owncloud.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;

import software.coolstuff.springframework.owncloud.exception.auth.OwncloudGroupNotFoundException;
import software.coolstuff.springframework.owncloud.model.OwncloudUserDetails;
import software.coolstuff.springframework.owncloud.service.api.OwncloudUserQueryService;
import software.coolstuff.springframework.owncloud.service.impl.AbstractOwncloudServiceTest;

@RestClientTest(OwncloudUserQueryService.class)
public abstract class AbstractOwncloudUserQueryServiceTest extends AbstractOwncloudServiceTest {

  @Autowired
  private OwncloudUserQueryService userQueryService;

  @Test
  @WithMockUser(username = "user1", password = "password")
  public void testFindAllUsers() throws Exception {
    prepareTestFindAllUsers("user1", "user2");

    List<String> users = userQueryService.findAllUsers();
    verifyServer();

    assertThat(users)
        .isNotNull()
        .containsOnly("user1", "user2");
  }

  protected void prepareTestFindAllUsers(String... users) throws Exception {}

  @Test
  @WithMockUser(username = "user1", password = "password")
  public void testFindAllUsersWithFilter() throws Exception {
    prepareTestFindAllUsersWithFilter("User 1", "user1");

    List<String> users = userQueryService.findAllUsers("User 1");
    verifyServer();

    assertThat(users)
        .isNotNull()
        .containsOnly("user1");
  }

  protected void prepareTestFindAllUsersWithFilter(String filter, String... users) throws Exception {}

  @Test
  @WithMockUser(username = "user1", password = "password")
  public void testFindAllGroups() throws Exception {
    prepareTestFindAllGroups("group1", "group2", "group3");

    List<String> groups = userQueryService.findAllGroups();
    verifyServer();

    assertThat(groups)
        .isNotNull()
        .containsOnly("group1", "group2", "group3");
  }

  protected void prepareTestFindAllGroups(String... groups) throws Exception {}

  @Test
  @WithMockUser(username = "user1", password = "password")
  public void testFindAllGroupsWithFilter() throws Exception {
    prepareTestFindAllGroupsWithFilter("p2", "group2");

    List<String> groups = userQueryService.findAllGroups("p2");
    verifyServer();

    assertThat(groups)
        .isNotNull()
        .containsOnly("group2");
  }

  protected void prepareTestFindAllGroupsWithFilter(String filter, String... groups) throws Exception {}

  @Test
  @WithMockUser(username = "user1", password = "password")
  public void testFindAllMembersOfGroup_OK() throws Exception {
    prepareTestFindAllMembersOfGroup_OK("group1", "user1");

    List<String> membersOfGroup = userQueryService.findAllMembersOfGroup("group1");
    verifyServer();

    assertThat(membersOfGroup)
        .isNotNull()
        .containsOnly("user1");
  }

  protected void prepareTestFindAllMembersOfGroup_OK(String group, String... users) throws Exception {}

  @Test(expected = OwncloudGroupNotFoundException.class)
  @WithMockUser(username = "user1", password = "password")
  public void testFindAllMembersOfGroup_UnknownGroup() throws Exception {
    prepareTestFindAllMembersOfGroup_UnknownGroup("group4");
    userQueryService.findAllMembersOfGroup("group4");
  }

  protected void prepareTestFindAllMembersOfGroup_UnknownGroup(String group) throws Exception {}

  @Test
  @WithMockUser(username = "user1", password = "password")
  public void testFindAllMembersOfGroup_GroupWithoutMembers() throws Exception {
    prepareTestFindAllMembersOfGroup_GroupWithoutMembers("group3");
    List<String> membersOfGroup = userQueryService.findAllMembersOfGroup("group3");
    assertThat(membersOfGroup)
        .isNotNull()
        .isEmpty();;
  }

  protected void prepareTestFindAllMembersOfGroup_GroupWithoutMembers(String groupname) throws Exception {}

  @Test(expected = NullPointerException.class)
  @WithMockUser(username = "user1", password = "password")
  public void testFindAllMembersOfGroup_NoGroup() {
    userQueryService.findAllMembersOfGroup(null);
  }

  @Test(expected = IllegalArgumentException.class)
  @WithMockUser(username = "user1", password = "password")
  public void testFindAllMembersOfGroup_BlankGroup() {
    userQueryService.findAllMembersOfGroup("");
  }

  @Test
  @WithMockUser(username = "user1", password = "password")
  public void testFindOneUser_OK() throws Exception {
    OwncloudUserDetails expectedUser = OwncloudUserDetails.builder()
        .username("user1")
        .enabled(true)
        .email("user1@example.com")
        .displayname("Mr. User 1")
        .quota(1024L)
        .build();
    prepareTestFindOneUser_OK(expectedUser, "group1", "group2");

    OwncloudUserDetails actualUser = userQueryService.findOneUser("user1");
    verifyServer();

    assertThat(actualUser).isNotNull();
    assertThat(actualUser.getUsername()).isEqualTo(expectedUser.getUsername());
    assertThat(actualUser.getPassword()).isNull();
    assertThat(actualUser.getDisplayname()).isEqualTo(expectedUser.getDisplayname());
    assertThat(actualUser.getEmail()).isEqualTo(expectedUser.getEmail());
    assertThat(actualUser.getQuota()).isEqualByComparingTo(expectedUser.getQuota());

    checkAuthorities(actualUser.getUsername(), actualUser.getAuthorities(), "group1", "group2");
  }

  protected void prepareTestFindOneUser_OK(OwncloudUserDetails expectedUser, String... groups) throws Exception {}

  @Test(expected = UsernameNotFoundException.class)
  @WithMockUser(username = "user3", password = "password")
  public void testFindOneUser_UnknownUser() throws Exception {
    prepareTestFindOneUser_UnknownUser("user3");
    userQueryService.findOneUser("user3");
  }

  protected void prepareTestFindOneUser_UnknownUser(String user) throws Exception {}

  @Test(expected = NullPointerException.class)
  @WithMockUser(username = "user1", password = "password")
  public void testFindOneUser_NoUser() {
    userQueryService.findOneUser(null);
  }

  @Test(expected = IllegalArgumentException.class)
  @WithMockUser(username = "user1", password = "password")
  public void testFindOneUser_BlankUser() {
    userQueryService.findOneUser("");
  }

  @Test
  @WithMockUser(username = "user1", password = "password")
  public void testFindAllGroupsOfUser_OK() throws Exception {
    prepareTestFindAllGroupsOfUser_OK("user1", "group1", "group2");

    List<String> groups = userQueryService.findAllGroupsOfUser("user1");
    verifyServer();

    assertThat(groups)
        .isNotNull()
        .containsOnly("group1", "group2");
  }

  protected void prepareTestFindAllGroupsOfUser_OK(String user, String... groups) throws Exception {}

  @Test
  @WithMockUser(username = "user2", password = "password")
  public void testFindAllGroupsOfUser_OK_NoGroups() throws Exception {
    prepareTestFindAllGroupsOfUser_OK_NoGroups("user2");

    List<String> groups = userQueryService.findAllGroupsOfUser("user2");
    verifyServer();

    assertThat(groups)
        .isNotNull()
        .isEmpty();
  }

  protected void prepareTestFindAllGroupsOfUser_OK_NoGroups(String user) throws Exception {}
}
