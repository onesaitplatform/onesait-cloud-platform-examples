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

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

import com.minsait.onesait.microservice.model.ModelStatus;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ModelStatusRepositoryImpl implements ModelStatusRepository {
	
	private final int NOTIFY_MILISECONDS = 60000;
	
	private ConcurrentHashMap<String, ModelStatus> uniqueModelsStatusMap = new ConcurrentHashMap<String, ModelStatus>();
	// {"modelenpoint1_modelversion1": ModelStatus1, ...}
	private ConcurrentHashMap<String, HashMap<String, ModelStatus>> workersModelsStatusMap = new ConcurrentHashMap<String, HashMap<String, ModelStatus>>();
	// {"modelenpoint1_modelversion1": {"_id1":ModelStatus1, "_id2":ModelStatus2, ...}, ...}
		
	private String generateKeyMap(String modelendpoint, String modelversion) {
		return modelendpoint + "_" + modelversion;
	}
		
	public void clearCache() {
		Instant now = Instant.now();
		
		// update workers
		for(HashMap.Entry<String, HashMap<String, ModelStatus>> modelgroup : workersModelsStatusMap.entrySet()) {
			HashMap<String, ModelStatus> modelStatusMap = modelgroup.getValue();
			
			if (modelStatusMap.isEmpty()) {
				workersModelsStatusMap.remove(modelgroup.getKey());
			}
			else {
				
				for(Entry<String, ModelStatus> modelentry : modelStatusMap.entrySet()) {
					Instant modelDatetime = modelentry.getValue().getTimestamp().toInstant();
					Duration timeElapsed = Duration.between(modelDatetime, now);
					
					if(timeElapsed.toMillis() > NOTIFY_MILISECONDS) {
						log.info("Removed worker after " + NOTIFY_MILISECONDS + " miliseconds: " + modelgroup.getKey() + " - " + modelentry.getKey());
						workersModelsStatusMap.get(modelgroup.getKey()).remove(modelentry.getKey());
					}
				}
				
			}
		}
		
		// update unique
		for(HashMap.Entry<String, ModelStatus> modelunique : uniqueModelsStatusMap.entrySet()) {
			String uniqueKey = modelunique.getKey();
			ModelStatus uniqueStatus = modelunique.getValue();
			Instant modelDatetime = uniqueStatus.getTimestamp().toInstant();
			Duration timeElapsed = Duration.between(modelDatetime, now);
			
			if(timeElapsed.toMillis() > NOTIFY_MILISECONDS) {
				log.info("Removed unique after " + NOTIFY_MILISECONDS + " miliseconds: " + uniqueKey);
				uniqueModelsStatusMap.remove(uniqueKey);
				
				// replace with first worker if exists
				if (workersModelsStatusMap.containsKey(uniqueKey) && !workersModelsStatusMap.get(uniqueKey).isEmpty()) {
					HashMap<String, ModelStatus> workersStatusMap = workersModelsStatusMap.get(uniqueKey);
					uniqueModelsStatusMap.put(uniqueKey, workersStatusMap.get(workersStatusMap.keySet().toArray()[0]));
					
				}
			  }
		}
		
	}
		
	@Override
	public ModelStatus getUniqueModelStatus(String modelendpoint, String modelversion) {
		ModelStatus modelStatus = null;
		String uniqueModelKey = generateKeyMap(modelendpoint, modelversion);
		if (uniqueModelsStatusMap.containsKey(uniqueModelKey)) {
			modelStatus = uniqueModelsStatusMap.get(uniqueModelKey);
		}
		return modelStatus;
	}
	
	public boolean removeUniqueModelStatus(String modelendpoint, String modelversion) {
		boolean done = false;
		String uniqueModelKey = generateKeyMap(modelendpoint, modelversion);
			if (uniqueModelsStatusMap.containsKey(uniqueModelKey)) {
				uniqueModelsStatusMap.remove(uniqueModelKey);
				done = true;
			}
		return done;
	}
	
	public boolean addUniqueModelStatus(String modelendpoint, String modelversion, ModelStatus modelStatus) {
		boolean done = false;
		String uniqueModelKey = generateKeyMap(modelendpoint, modelversion);
		// update in unique
		if (uniqueModelsStatusMap.containsKey(uniqueModelKey)) {
			if (uniqueModelsStatusMap.get(uniqueModelKey).get_id().equals(modelStatus.get_id())) {
				uniqueModelsStatusMap.put(uniqueModelKey, modelStatus); // update if same worker _id
			}
			/**else if (!uniqueModelsStatusMap.get(uniqueModelKey).is_training()) {
				uniqueModelsStatusMap.put(uniqueModelKey, modelStatus); // update different worker if not training
			}*/
		}
		else {
			uniqueModelsStatusMap.put(uniqueModelKey, modelStatus); // create unique status
		}
		
		return done;
	}
	
	public boolean addWorkerModelStatus(String modelendpoint, String modelversion, ModelStatus modelStatus) {
		boolean done = false;
		String uniqueModelKey = generateKeyMap(modelendpoint, modelversion);
		// update in workers
			HashMap<String, ModelStatus> workersStatusMap;
			if (workersModelsStatusMap.containsKey(uniqueModelKey)) {
				workersStatusMap = workersModelsStatusMap.get(uniqueModelKey);
				workersStatusMap.put(modelStatus.get_id(), modelStatus); // update worker status
				workersModelsStatusMap.put(uniqueModelKey, workersStatusMap); // update worker group
				done = true;
			}
			else {
				workersStatusMap = new HashMap<String, ModelStatus>();
				workersStatusMap.put(modelStatus.get_id(), modelStatus); // create worker status
				workersModelsStatusMap.put(uniqueModelKey, workersStatusMap); // create worker group
				done = true;
			}
		
		return done;
	}
	
	public boolean removeWorkerModelStatus(String modelendpoint, String modelversion, String workerid) {
		boolean done = false;
		String uniqueModelKey = generateKeyMap(modelendpoint, modelversion);
			if (workersModelsStatusMap.containsKey(uniqueModelKey)) {
				if (workersModelsStatusMap.get(uniqueModelKey).containsKey(workerid)) {
					workersModelsStatusMap.get(uniqueModelKey).remove(workerid);
					done = true;
				}
				
			}
		return done;
	}

	@Override
	public ModelStatus addModelStatus(String modelendpoint, String modelversion, ModelStatus modelStatus) {
		String uniqueModelKey = generateKeyMap(modelendpoint, modelversion);
		
		// update in workers
		HashMap<String, ModelStatus> workersStatusMap;
		if (workersModelsStatusMap.containsKey(uniqueModelKey)) {
			workersStatusMap = workersModelsStatusMap.get(uniqueModelKey);
			workersStatusMap.put(modelStatus.get_id(), modelStatus); // update worker status
			workersModelsStatusMap.put(uniqueModelKey, workersStatusMap); // update worker group
		}
		else {
			workersStatusMap = new HashMap<String, ModelStatus>();
			workersStatusMap.put(modelStatus.get_id(), modelStatus); // create worker status
			workersModelsStatusMap.put(uniqueModelKey, workersStatusMap); // create worker group
		}
		
		// update in unique
		if (uniqueModelsStatusMap.containsKey(uniqueModelKey)) {
			if (uniqueModelsStatusMap.get(uniqueModelKey).get_id().equals(modelStatus.get_id())) {
				uniqueModelsStatusMap.put(uniqueModelKey, modelStatus); // update if same worker _id
			}
			/**else if (!uniqueModelsStatusMap.get(uniqueModelKey).is_training()) {
				uniqueModelsStatusMap.put(uniqueModelKey, modelStatus); // update different worker if not training
			}*/
		}
		else {
			uniqueModelsStatusMap.put(uniqueModelKey, modelStatus); // create unique status
		}
		
		return modelStatus;
	}
	
	@Override
	public boolean isModelTraining(String modelendpoint, String modelversion) {
		boolean isTraining = false;
		String uniqueModelKey = generateKeyMap(modelendpoint, modelversion);
		
		if (uniqueModelsStatusMap.containsKey(uniqueModelKey)) {
			isTraining = uniqueModelsStatusMap.get(uniqueModelKey).is_training();
		}
		return isTraining;
	}
	
	@Override
	public ArrayList<ModelStatus> getModelWorkersStatus(String modelendpoint, String modelversion) {
		ArrayList<ModelStatus> workersStatusList = new ArrayList<ModelStatus>();
		String uniqueModelKey = generateKeyMap(modelendpoint, modelversion);
		HashMap<String, ModelStatus> workersStatusMap;
		if (workersModelsStatusMap.containsKey(uniqueModelKey)) {
			workersStatusMap = workersModelsStatusMap.get(uniqueModelKey);
			for (ModelStatus workerStatus: workersStatusMap.values()) {
				workersStatusList.add(workerStatus);
			}
		}
		
		return workersStatusList;
	}
	
	@Override
	public ArrayList<String> getModelWorkersIds(String modelendpoint, String modelversion) {
		ArrayList<String> workersIdsList = new ArrayList<String>();
		String uniqueModelKey = generateKeyMap(modelendpoint, modelversion);
		HashMap<String, ModelStatus> workersStatusMap;
		if (workersModelsStatusMap.containsKey(uniqueModelKey)) {
			workersStatusMap = workersModelsStatusMap.get(uniqueModelKey);
			for (ModelStatus workerStatus: workersStatusMap.values()) {
				workersIdsList.add(workerStatus.get_id());
			}
		}
		
		return workersIdsList;
	}

}
