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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.ParserFactory;
import org.jboss.errai.marshalling.client.api.annotations.AlwaysQualify;
import org.jboss.errai.marshalling.client.api.annotations.ClientMarshaller;
import org.jboss.errai.marshalling.client.api.annotations.ImplementationAliases;
import org.jboss.errai.marshalling.client.api.annotations.ServerMarshaller;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.util.MarshallUtil;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@ClientMarshaller
@ServerMarshaller
@AlwaysQualify
@ImplementationAliases({AbstractMap.class, HashMap.class})
public class MapMarshaller<T extends Map> implements Marshaller<T> {
  public static final MapMarshaller INSTANCE = new MapMarshaller();
  private static final HashMap[] EMPTY_ARRAY = new HashMap[0];

  @Override
  public Class<T> getTypeHandled() {
    return (Class<T>) Map.class;
  }

  @Override
  public T[] getEmptyArray(MarshallingSession ctx) {
    return (T[]) EMPTY_ARRAY;
  }
  
  @Override
  public T demarshall(EJValue o, MarshallingSession ctx) {
    return doDermashall((T) new HashMap(), o, ctx);
  }

  protected T doDermashall(T impl, EJValue o, MarshallingSession ctx) {
    EJObject jsonObject = o.isObject();

    Object demarshalledKey, demarshalledValue;
    for (String key : jsonObject.keySet()) {
      if (key.startsWith(SerializationParts.EMBEDDED_JSON)) {
        EJValue val = ParserFactory.get().parse(key.substring(SerializationParts.EMBEDDED_JSON.length()));
        demarshalledKey = ctx.getMarshallerInstance(ctx.determineTypeFor(null, val)).demarshall(val, ctx);
      }
      else {
        demarshalledKey = key;
      }

      EJValue v = jsonObject.get(key);
      if (!v.isNull()) {
       demarshalledValue = ctx.getMarshallerInstance(ctx.determineTypeFor(null, v)).demarshall(v, ctx);
      } 
      else {
        demarshalledValue = null;
      }
      impl.put(demarshalledKey, demarshalledValue);
    }
    return impl;
  }

  @Override
  public String marshall(T o, MarshallingSession ctx) {
    StringBuilder buf = new StringBuilder();
    buf.append("{");
    Object key, val;
    int i = 0;
    for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) o).entrySet()) {
      if (i++ > 0) {
        buf.append(",");
      }
      key = entry.getKey();
      val = entry.getValue();

      Marshaller<Object> keyMarshaller;
      Marshaller<Object> valueMarshaller;
      if (key instanceof String) {
        buf.append("\"" + key + "\"");
      }
      else if (key != null) {
        if (key instanceof Number || key instanceof Boolean || key instanceof Character) {
          keyMarshaller = MarshallUtil.getQualifiedNumberMarshaller(key);
        }
        else {
          keyMarshaller = ctx.getMarshallerInstance(key.getClass().getName());
        }
        buf.append(("\"" + SerializationParts.EMBEDDED_JSON))
                .append(MarshallUtil.jsonStringEscape(keyMarshaller.marshall(key, ctx)))
                .append("\"");
      }

      buf.append(":");

      if (val == null) {
        buf.append("null");
      }
      else {
        if ((val instanceof Number && !(val instanceof BigInteger || val instanceof BigDecimal))
                || val instanceof Boolean || val instanceof Character) {

          valueMarshaller = MarshallUtil.getQualifiedNumberMarshaller(val);
        }
        else {
          valueMarshaller = ctx.getMarshallerInstance(val.getClass().getName());
        }
        buf.append(valueMarshaller.marshall(val, ctx));
      }
    }

    return buf.append("}").toString();
  }
}