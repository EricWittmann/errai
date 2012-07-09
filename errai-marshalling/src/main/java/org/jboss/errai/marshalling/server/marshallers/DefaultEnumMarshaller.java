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

package org.jboss.errai.marshalling.server.marshallers;

import java.lang.reflect.Array;

import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.json.EJValue;

/**
 * @author Mike Brock
 */
public class DefaultEnumMarshaller implements Marshaller<Enum> {
  private final Class enumType;

  public DefaultEnumMarshaller(Class enumType) {
    this.enumType = enumType;
  }

  @Override
  public Class<Enum> getTypeHandled() {
    return Enum.class;
  }

  @Override
  public Enum demarshall(EJValue a0, MarshallingSession a1) {
    try {
      if (a0.isNull()) {
        return null;
      }
      return Enum.valueOf(enumType, a0.isObject().get(SerializationParts.ENUM_STRING_VALUE).isString().stringValue());
    }
    catch (Throwable t) {
      t.printStackTrace();
      throw new RuntimeException("error demarshalling enum: " + enumType.getName(), t);
    }
  }

  @Override
  public String marshall(Enum a0, MarshallingSession a1) {
    if (a0 == null) {
      return "null";
    }

    if (a1.hasObjectHash(a0)) {
      return new StringBuilder().append("{\"" + SerializationParts.ENCODED_TYPE + "\":\"" + enumType.getName()
              + "\",\"" + SerializationParts.OBJECT_ID + "\":\"").append(a1.getObjectHash(a0)).append("\"}").toString();
    }

    return new StringBuilder().append("{\"" + SerializationParts.ENCODED_TYPE + "\":\"" + enumType.getName()
            + "\","
            + "\"" + SerializationParts.OBJECT_ID + "\":\"" + a1.getObjectHash(a0) + "\""
            + ",\"" + SerializationParts.ENUM_STRING_VALUE + "\":\"").append(a0.name()).append("\"}").toString();
  }

  @Override
  public Enum[] getEmptyArray(MarshallingSession ctx) {
    return (Enum[]) Array.newInstance(enumType, 0);
  }

}
