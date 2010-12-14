/*
 * Copyright (C) 2010 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.opendatakit.aggregate.task.gae;

import org.opendatakit.aggregate.constants.ServletConsts;
import org.opendatakit.aggregate.form.Form;
import org.opendatakit.aggregate.submission.SubmissionKey;
import org.opendatakit.aggregate.task.CsvGenerator;
import org.opendatakit.aggregate.task.gae.servlet.CsvGeneratorTaskServlet;
import org.opendatakit.common.persistence.Datastore;
import org.opendatakit.common.persistence.exception.ODKDatastoreException;
import org.opendatakit.common.security.User;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * This is a singleton bean.  It cannot have any per-request state.
 * It uses a static inner class to encapsulate the per-request state
 * of a running background task.
 * 
 * @author wbrunette@gmail.com
 * @author mitchellsundt@gmail.com
 * 
 */
public class CsvGeneratorImpl implements CsvGenerator {

  @Override
  public void createCsvTask(Form form, SubmissionKey persistentResultsKey, long attemptCount, String baseServerWebUrl, Datastore datastore, User user) throws ODKDatastoreException {
    TaskOptions task = TaskOptions.Builder.withUrl(ServletConsts.WEB_ROOT
        + CsvGeneratorTaskServlet.ADDR);
    task.method(TaskOptions.Method.GET);
    task.countdownMillis(1);
    task.param(ServletConsts.FORM_ID, form.getFormId());
    task.param(ServletConsts.PERSISTENT_RESULTS_KEY, persistentResultsKey.toString());
    task.param(ServletConsts.ATTEMPT_COUNT, Long.toString(attemptCount));
    Queue queue = QueueFactory.getDefaultQueue();
    queue.add(task);
  }
}
