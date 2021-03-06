package org.jboss.errai.security.client.local.identity;

import static org.jboss.errai.ui.nav.client.local.api.LoginPage.CURRENT_PAGE_COOKIE;

import java.io.Serializable;

import javax.inject.Singleton;

import org.jboss.errai.bus.client.api.BusErrorCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.databinding.client.api.Bindable;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.security.shared.AuthenticationService;
import org.jboss.errai.security.shared.Role;
import org.jboss.errai.security.shared.User;
import org.jboss.errai.ui.nav.client.local.Navigation;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Identity holds the username and password and performs the authentication
 * tasks.
 * 
 * @author edewit@redhat.com
 */
@Bindable
@Singleton
public class Identity implements Serializable {
  private static final long serialVersionUID = 1L;
  private String username;
  private String password;

  public void login(final RemoteCallback<User> callback, BusErrorCallback errorCallback) {
    MessageBuilder.createCall(new RemoteCallback<User>() {
      @Override
      public void callback(User user) {
        StyleBindingsRegistry.get().updateStyles();
        final String page = Cookies.getCookie(CURRENT_PAGE_COOKIE);
        IOC.getAsyncBeanManager().lookupBean(Navigation.class).getInstance(new CreationalCallback<Navigation>() {

          @Override
          public void callback(Navigation navigation) {
            if (page != null) {
              Cookies.removeCookie(CURRENT_PAGE_COOKIE);
              navigation.goTo(page);
            }
            else {
              navigation.goTo(navigation.getCurrentPage().name());
            }
          }
        });
        if (callback != null) {
          callback.callback(user);
        }
      }
    }, errorCallback, AuthenticationService.class).login(username, password);
  }

  public void logout() {
    MessageBuilder.createCall(new VoidRemoteCallback(), AuthenticationService.class).logout();
    StyleBindingsRegistry.get().updateStyles();
  }

  public void getUser(final AsyncCallback<User> callback) {
    MessageBuilder.createCall(new RemoteCallback<User>() {
      @Override
      public void callback(User response) {
        callback.onSuccess(response);
      }
    }, AuthenticationService.class).getUser();
  }

  public void hasPermission(final AsyncCallback<Boolean> callback, final String... roleNames) {
    MessageBuilder.createCall(new RemoteCallback<User>() {
      @Override
      public void callback(final User user) {
        if (user == null) {
          callback.onSuccess(false);
          return;
        }
        for (String roleName : roleNames) {
          final Role role = new Role(roleName);
          if (!user.getRoles().contains(role)) {
            callback.onSuccess(false);
            return;
          }
        }
        callback.onSuccess(true);
      }
    }, AuthenticationService.class).getUser();
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  private static class VoidRemoteCallback implements RemoteCallback<Void> {
    @Override
    public void callback(final Void response) {
    }
  }
}
