/*
 * Copyright 2005-2014 Red Hat, Inc.
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.apache.activemq.javaee.example;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Date;
import java.util.Properties;

import org.apache.activemq.javaee.example.server.XARecoveryExampleService;

/**
 * An example which invokes an EJB. The EJB will be involved in a
 * transaction with a "buggy" XAResource to crash the server.
 * When the server is restarted, the recovery manager will recover the message
 * so that the consumer can receive it.
 *
 * @author <a href="mailto:andy.taylor@jboss.org">Andy Taylor</a>
 * @author <a href="mailto:jmesnil@redhat.com">Jeff Mesnil</a>
 */
public class XARecoveryExampleStepTwo
{
   public static void main(final String[] args) throws Exception
   {
      // Step 1. We will try to receive a message. Once the server is restarted, the message will be recovered and the consumer will receive it.
      boolean received = false;
      while (!received)
      {
         try
         {
            Thread.sleep(15000);
            XARecoveryExampleStepTwo.receiveMessage();
            received = true;
         }
         catch (Exception e)
         {
            System.out.println(".");
         }
      }
   }

   private static void receiveMessage() throws Exception
   {
      InitialContext initialContext = null;
      Connection connection = null;
      try
      {
         final Properties env = new Properties();

         env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");

         env.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

         initialContext = new InitialContext(env);

         ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("jms/RemoteConnectionFactory");

         Queue queue = (Queue) initialContext.lookup("jms/queues/testQueue");

         connection = cf.createConnection("guest", "password");
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         MessageConsumer consumer = session.createConsumer(queue);

         connection.start();

         System.out.println("\nwaiting to receive a message...");
         TextMessage messageReceived = (TextMessage) consumer.receive(3600 * 1000);
         System.out.format("Received message: %s \n\t(JMS MessageID: %s)\n",
                           messageReceived.getText(),
                           messageReceived.getJMSMessageID());
      }
      finally
      {
         // Step 7. Be sure to close the resources!
         if (initialContext != null)
         {
            initialContext.close();
         }
         if (connection != null)
         {
            connection.close();
         }
      }
   }
}
