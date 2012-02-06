package org.jboss.errai.enterprise.rebind;

import org.jboss.errai.ioc.rebind.ioc.QualifyingMetadata;

import javax.enterprise.inject.Any;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class JSR299QualifyingMetadata implements QualifyingMetadata {
  private Set<Annotation> qualifiers;
  private static Any ANY_INSTANCE = new Any() {
    @Override
    public Class<? extends Annotation> annotationType() {
      return Any.class;
    }

    public String toString() {
      return "@Any";
    }
  };

  public JSR299QualifyingMetadata(Collection<Annotation> qualifiers) {
    this.qualifiers = Collections.unmodifiableSet(new HashSet<Annotation>(qualifiers));
  }

  @Override
  public boolean doesSatisfy(QualifyingMetadata metadata) {
    if (metadata instanceof JSR299QualifyingMetadata) {
      JSR299QualifyingMetadata comparable = (JSR299QualifyingMetadata) metadata;

      return ((comparable.qualifiers.size() == 1
              && comparable.qualifiers.contains(ANY_INSTANCE))
              || qualifiers.size() == 1
              && qualifiers.contains(ANY_INSTANCE)
              || qualifiers.containsAll(comparable.qualifiers));
    }
    else return metadata == null;
  }

  static JSR299QualifyingMetadata createFromAnnotations(Annotation[] annotations) {
    if (annotations == null || annotations.length == 0) return createDefaultQualifyingMetaData();

    Set<Annotation> qualifiers = new HashSet<Annotation>();

    for (Annotation a : annotations) {
      if (a.annotationType().isAnnotationPresent(Qualifier.class)) {
        qualifiers.add(a);
      }
    }

    return qualifiers.isEmpty() ? null : new JSR299QualifyingMetadata(qualifiers);
  }

  static JSR299QualifyingMetadata createDefaultQualifyingMetaData() {
    return new JSR299QualifyingMetadata(
            Collections.<Annotation>singleton(ANY_INSTANCE));
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();

    for (Annotation a : qualifiers) {
      buf.append(" @").append(a.annotationType().getSimpleName()).append(" ");
    }

    return buf.toString();
  }
}
