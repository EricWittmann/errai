/*
 * Copyright 2011 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.marshalling.client.marshallers;

import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.annotations.ClientMarshaller;
import org.jboss.errai.marshalling.client.api.annotations.ServerMarshaller;
import org.jboss.errai.marshalling.client.api.exceptions.MarshallingException;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.util.NumbersUtils;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
@ClientMarshaller @ServerMarshaller
public class LongMarshaller extends AbstractNumberMarshaller<Long> {
  private static final Long[] EMPTY_ARRAY = new Long[0];
  
  @Override
  public Class<Long> getTypeHandled() {
    return Long.class;
  }
  
  @Override
  public Long[] getEmptyArray(MarshallingSession ctx) {
    return EMPTY_ARRAY;
  }

  @Override
  public Long doNotNullDemarshall(EJValue o, MarshallingSession ctx) {
    if (o.isObject() != null) {
      EJValue numValue = o.isObject().get(SerializationParts.NUMERIC_VALUE);
      return Long.parseLong(numValue.isString().stringValue());
    }
    else {
      throw new MarshallingException("cannot demarshall as java.lang.Long: expected qualified value but got: " + o);
    }
  }

  @Override
  public String doNotNullMarshall(Long o, MarshallingSession ctx) {
    return NumbersUtils.qualifiedNumericEncoding(o);
  }
}
