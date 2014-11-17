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
package org.apache.activemq.core.server.impl;

import javax.transaction.xa.Xid;
import java.util.List;
import java.util.Map;

import org.apache.activemq.api.core.Pair;
import org.apache.activemq.api.core.SimpleString;
import org.apache.activemq.core.journal.Journal;
import org.apache.activemq.core.persistence.GroupingInfo;
import org.apache.activemq.core.persistence.QueueBindingInfo;
import org.apache.activemq.core.persistence.impl.PageCountPending;
import org.apache.activemq.core.persistence.impl.journal.AddMessageRecord;
import org.apache.activemq.core.server.MessageReference;
import org.apache.activemq.core.server.ServerMessage;
import org.apache.activemq.core.transaction.ResourceManager;
import org.apache.activemq.core.transaction.Transaction;

public interface JournalLoader
{
   void initQueues(Map<Long, QueueBindingInfo> queueBindingInfosMap, List<QueueBindingInfo> queueBindingInfos) throws Exception;

   void handleAddMessage(Map<Long, Map<Long, AddMessageRecord>> queueMap) throws Exception;

   void handleNoMessageReferences(Map<Long, ServerMessage> messages);

   void handleGroupingBindings(List<GroupingInfo> groupingInfos);

   void handleDuplicateIds(Map<SimpleString, List<Pair<byte[], Long>>> duplicateIDMap) throws Exception;

   void postLoad(Journal messageJournal, ResourceManager resourceManager, Map<SimpleString, List<Pair<byte[], Long>>> duplicateIDMap) throws Exception;

   void handlePreparedSendMessage(ServerMessage message, Transaction tx, long queueID) throws Exception;

   void handlePreparedAcknowledge(long messageID, List<MessageReference> referencesToAck, long queueID) throws Exception;

   void handlePreparedTransaction(Transaction tx, List<MessageReference> referencesToAck, Xid xid, ResourceManager resourceManager) throws Exception;

   void recoverPendingPageCounters(List<PageCountPending> pendingNonTXPageCounter) throws Exception;

   void cleanUp();
}
