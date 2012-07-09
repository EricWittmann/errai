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
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.annotations.ClientMarshaller;
import org.jboss.errai.marshalling.client.api.annotations.ServerMarshaller;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJString;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.util.NumbersUtils;

/**
 * This class is used to handle untyped Objects on the wire.
 *
 * @author Mike Brock <cbrock@redhat.com>
 */
@ClientMarshaller
@ServerMarshaller
public class ObjectMarshaller extends AbstractNullableMarshaller<Object> {
  
  private static final Object[] EMPTY_ARRAY = new Object[0];

  @Override
  public Class<Object> getTypeHandled() {
    return Object.class;
  }
  
  @Override
  public Object[] getEmptyArray(MarshallingSession ctx) {
    return EMPTY_ARRAY;
  }

  @Override
  public Object doNotNullDemarshall(EJValue o, MarshallingSession ctx) {
    if (o.isObject() != null) {
      EJObject jsObject = o.isObject();
      EJString string = jsObject.get(SerializationParts.ENCODED_TYPE).isString();
      if (string == null) {
        return MapMarshaller.INSTANCE.demarshall(o, ctx);
      }

      if (jsObject.containsKey(SerializationParts.NUMERIC_VALUE)) {
        return NumbersUtils.getNumber(string.stringValue(), jsObject.get(SerializationParts.NUMERIC_VALUE));
      }

      Marshaller<Object> marshaller = ctx.getMarshallerInstance(string.stringValue());

      if (marshaller == null) {
        throw new RuntimeException("marshalled type is unknown to the marshalling framework: " + string.stringValue());
      }

      return marshaller.demarshall(o, ctx);
    }
    else if (o.isArray() != null) {
      return new ListMarshaller().demarshall(o, ctx);
    }
    else if (o.isString() != null) {
      return o.isString().stringValue();
    }

    return null;
  }

  @Override
  public String doNotNullMarshall(Object o, MarshallingSession ctx) {
    if ((o instanceof Number && !o.getClass().getName().startsWith("java.math.Big")) || o instanceof Boolean) {
      return NumbersUtils.qualifiedNumericEncoding(o);
    }

    Marshaller<Object> marshaller = ctx.getMarshallerInstance(o.getClass().getName());

    if (marshaller == null) {
      throw new RuntimeException("marshalled type is unknown to the marshalling framework: " + o.getClass().getName());
    }

    return marshaller.marshall(o, ctx);
  }

}
