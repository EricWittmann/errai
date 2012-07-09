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
import org.jboss.errai.marshalling.client.api.json.EJValue;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
@ClientMarshaller
@ServerMarshaller
public class ShortMarshaller extends AbstractNumberMarshaller<Short> {
  private static final Short[] EMPTY_ARRAY = new Short[0];

  @Override
  public Class<Short> getTypeHandled() {
    return Short.class;
  }

  @Override
  public Short[] getEmptyArray(MarshallingSession ctx) {
    return EMPTY_ARRAY;
  }

  @Override
  public Short doNotNullDemarshall(EJValue o, MarshallingSession ctx) {
    if (o.isObject() != null) {
      return o.isObject().get(SerializationParts.NUMERIC_VALUE).isNumber().shortValue();
    }
    else {
      return o.isNumber().shortValue();
    }
  }
}
