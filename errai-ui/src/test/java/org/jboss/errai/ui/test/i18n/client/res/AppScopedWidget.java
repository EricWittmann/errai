package org.jboss.errai.ui.test.i18n.client.res;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;

@Templated
@ApplicationScoped
public class AppScopedWidget extends Composite {
  
  @Inject
  @DataField
  private InlineLabel test;
  
  public String getInlineLabelText() {
    return test.getText();
  }

}
