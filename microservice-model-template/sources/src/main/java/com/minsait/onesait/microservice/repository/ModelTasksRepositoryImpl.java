/**
 * Copyright Indra Soluciones Tecnologías de la Información, S.L.U.
 * 2013-2019 SPAIN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.minsait.onesait.microservice.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.minsait.onesait.microservice.model.ModelTask;

@Repository
public class ModelTasksRepositoryImpl implements ModelTasksRepository {
	
	private final int MAX_TASK_QUEUE_SIZE = 1;
	
	private ConcurrentHashMap<String, Queue<ModelTask>> uniqueModelTasksMap = new ConcurrentHashMap<String, Queue<ModelTask>>();
	// {"modelenpoint1_modelversion1": ModelTask, ...}
	private ConcurrentHashMap<String, HashMap<String, Queue<ModelTask>>> workersModelTasksMap = new ConcurrentHashMap<String, HashMap<String, Queue<ModelTask>>>();
	// {"modelenpoint1_modelversion1": {"_id1":ModelStatus1, "_id2":ModelStatus2, ...}, ...}
	

	private String generateKeyMap(String modelendpoint, String modelversion) {
		return modelendpoint + "_" + modelversion;
	}
	
	@Override
	public boolean isThereModelTask(String modelendpoint, String modelversion) {
		boolean isThereTask = false;
		String uniqueModelKey = generateKeyMap(modelendpoint, modelversion);
		if (uniqueModelTasksMap.containsKey(uniqueModelKey)) {
			if (!uniqueModelTasksMap.get(uniqueModelKey).isEmpty()) {
				isThereTask = true;
			}
		}
		return isThereTask;
	}
	
	@Override
	public ModelTask getUniqueModelTask(String modelendpoint, String modelversion) {
		ModelTask modelTask = null;
		String uniqueModelKey = generateKeyMap(modelendpoint, modelversion);
		if (uniqueModelTasksMap.containsKey(uniqueModelKey)) {
			modelTask = uniqueModelTasksMap.get(uniqueModelKey).poll();
		}
		return modelTask;
	}
	
	@Override
	public boolean addUniqueModelTask(String modelendpoint, String modelversion,  ModelTask modeltask) {
		boolean done = false;
		String uniqueModelKey = generateKeyMap(modelendpoint, modelversion);
		if (uniqueModelTasksMap.containsKey(uniqueModelKey)) {
			done = uniqueModelTasksMap.get(uniqueModelKey).offer(modeltask);
			
		}
		else {
			Queue<ModelTask> uniqueModelQueue = new ArrayBlockingQueue<ModelTask>(MAX_TASK_QUEUE_SIZE);
			uniqueModelTasksMap.put(uniqueModelKey, uniqueModelQueue); // create unique task queue 
		}
		return done;
	}
	
	@Override
	public ModelTask getWorkerModelTask(String modelendpoint, String modelversion, String workerid) {
		ModelTask workerTask = null;
		String uniqueModelKey = generateKeyMap(modelendpoint, modelversion);
		HashMap<String, Queue<ModelTask>> workersStatusMap;
		if (workersModelTasksMap.containsKey(uniqueModelKey)) {
			workersStatusMap = workersModelTasksMap.get(uniqueModelKey);
			
			if (workersStatusMap.containsKey(workerid)) {
				workerTask = workersStatusMap.get(workerid).poll();
			}
		}
		return workerTask;
	}
	
	public ArrayList<Boolean> addWorkersModelTask(String modelendpoint, String modelversion, ArrayList<String> workerids, ModelTask modeltask) {
		 ArrayList<Boolean> done = new ArrayList<Boolean>(workerids.size());
		 workerids.stream().forEach(
				 workerid -> done.add(
						 addWorkersModelTask(modelendpoint, modelversion, workerid, modeltask)
						 )
				 );
		 return done;
	}
	
	@Override
	public boolean addWorkersModelTask(String modelendpoint, String modelversion, String workerid, ModelTask modeltask) {
		boolean done = false;
		String uniqueModelKey = generateKeyMap(modelendpoint, modelversion);
		HashMap<String, Queue<ModelTask>> workersStatusMap;
		if (workersModelTasksMap.containsKey(uniqueModelKey)) {
			workersStatusMap = workersModelTasksMap.get(uniqueModelKey);

			if (workersStatusMap.containsKey(workerid)) {
				done = workersStatusMap.get(workerid).offer(modeltask);
			}
			else {
				Queue<ModelTask> workerModelQueue = new ArrayBlockingQueue<ModelTask>(MAX_TASK_QUEUE_SIZE);
				done = workerModelQueue.offer(modeltask);
				workersStatusMap.put(workerid, workerModelQueue); // create worker task queue 
			}
		
		}
		else {
			Queue<ModelTask> workerModelQueue = new ArrayBlockingQueue<ModelTask>(MAX_TASK_QUEUE_SIZE);
			HashMap<String, Queue<ModelTask>> workersTaskMap = new HashMap<String, Queue<ModelTask>>();
			done =  workerModelQueue.offer(modeltask);
			workersTaskMap.put(workerid, workerModelQueue);
			workersModelTasksMap.put(uniqueModelKey, workersTaskMap); // create workers task queue 
			
		}
		return done;
	}
	
	
}
