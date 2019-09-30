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

package com.minsait.onesait.microservice.rest;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.minsait.onesait.microservice.enums.ModelTaskPolicy;
import com.minsait.onesait.microservice.enums.ModelTasks;
import com.minsait.onesait.microservice.model.ModelStatus;
import com.minsait.onesait.microservice.model.ModelTask;
import com.minsait.onesait.microservice.repository.ModelStatusRepositoryImpl;
import com.minsait.onesait.microservice.repository.ModelTasksRepositoryImpl;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SupervisorServiceImpl implements SupervisorService {
	
	@Autowired 
	private ModelStatusRepositoryImpl modelStatusRepository;
	@Autowired 
	private ModelTasksRepositoryImpl modelTasksRepository;
	
	private final String ADDED_STATUS_MSG = "{\"msg\":\"Added model status correctly!\",\"more_info\":\"%s\"}";
	private final String ADDED_TASK_MSG = "{\"msg\":\"Added model task correctly!\",\"more_info\":\"%s\"}";
	private final String NOT_ADDED_TASK_MSG = "{\"msg\":\"Not added model task correctly!\",\"more_info\":\"%s\"}";
	private final String BUSY_MSG = "{\"msg\":\"Not possible to add task to busy model\",\"more_info\":\"%s\"}";
	
	private boolean hasModelFinishedTraining(String modelendpoint, String modelversion, ModelStatus modelstatus) {
		boolean finished = false;
		if (modelStatusRepository.isModelTraining(modelendpoint, modelversion)) {
			ModelStatus uniqueModelStatus = modelStatusRepository.getUniqueModelStatus(modelendpoint, modelversion);
			if (uniqueModelStatus.get_id().equals(modelstatus.get_id()) && !modelstatus.is_training()) {
				finished = true;
			}
		}
		return finished;
	}
	
	private boolean hasWorkedStartTraining(String modelendpoint, String modelversion, ModelStatus modelstatus) {
		boolean started = false;
		if (modelstatus.is_training() && !modelStatusRepository.isModelTraining(modelendpoint, modelversion)) {
			started = true;
		}
		return started;
	}
	
	@Override
	public ResponseEntity<?> status(String modelendpoint, String modelversion) {	
		modelStatusRepository.clearCache();
		ModelStatus modelStatus = modelStatusRepository.getUniqueModelStatus(modelendpoint, modelversion);
		
		if (modelStatus != null) {
			log.info("Getting model status: " + modelStatus.toString());
			return new ResponseEntity<ModelStatus>(modelStatus, HttpStatus.OK);
		}
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<String> receiveStatus(String modelendpoint, String modelversion, ModelStatus modelStatus) {
		log.info("Received new model status: " + modelStatus.toString());
		modelStatusRepository.clearCache();
		boolean done = false;
		done = modelStatusRepository.addWorkerModelStatus(modelendpoint, modelversion, modelStatus); // update/add worker
		
		if (modelStatusRepository.getUniqueModelStatus(modelendpoint, modelversion) == null) {
			modelStatusRepository.addUniqueModelStatus(modelendpoint, modelversion, modelStatus); // add unique if not exists
		}
		
		else if (hasWorkedStartTraining(modelendpoint, modelversion, modelStatus)) {
			modelStatusRepository.addUniqueModelStatus(modelendpoint, modelversion, modelStatus); // overwrite if start train
		}
		
		// if is worker training and finish, update unique and send reload to rest of workers
		else if (hasModelFinishedTraining(modelendpoint, modelversion, modelStatus)) { 
			modelStatusRepository.addUniqueModelStatus(modelendpoint, modelversion, modelStatus);
			ModelTask reloadTask = new ModelTask();
			reloadTask.set_id(UUID.randomUUID().toString());
			reloadTask.setExecution_policy(ModelTaskPolicy.ALL.name());
			reloadTask.setExecution_task(ModelTasks.RELOAD.name());
			reloadTask.setTimestamp(new Date(System.currentTimeMillis()));
			return receiveTask(modelendpoint, modelversion, reloadTask);
		}
		
		if (done) {
			return new ResponseEntity<String>(String.format(ADDED_STATUS_MSG, modelStatus.toString()), HttpStatus.OK);
		}
		return new ResponseEntity<String>(String.format(NOT_ADDED_TASK_MSG, modelStatus.toString()), HttpStatus.BAD_REQUEST);
		
	}
	
	@Override
	public ResponseEntity<?> task(String modelendpoint, String modelversion, String workerid) {	
		if (!modelStatusRepository.isModelTraining(modelendpoint, modelversion)) {
			
			ModelTask sendTask;
			sendTask = modelTasksRepository.getWorkerModelTask(modelendpoint, modelversion, workerid);
			
			if (sendTask == null) {
				sendTask = modelTasksRepository.getUniqueModelTask(modelendpoint, modelversion);
			}
			
			if (sendTask == null) {
				log.info("No tasks for: " + modelendpoint + "_" + modelversion + "(" + workerid + ")" );
				return new ResponseEntity<ModelTask>(sendTask, HttpStatus.NO_CONTENT);
			}
			
			log.info("Sending task to: " + modelendpoint + "_" + modelversion + "(" + workerid + ")" + " - " + sendTask.toString() );
			return new ResponseEntity<ModelTask>(sendTask, HttpStatus.OK);
		}
		
		log.warn(" (" + workerid + "): " + (String.format(BUSY_MSG, modelendpoint + "_" + modelversion)) );
		return new ResponseEntity<ModelTask>(HttpStatus.CONFLICT);
	}

	@Override
	public ResponseEntity<String> receiveTask(String modelendpoint, String modelversion, ModelTask modeltask) {
		log.info("Received new model task: " + modeltask.toString());
		boolean done = false;
		String receivedTaskPolicy = modeltask.getExecution_policy().toUpperCase();
		if (receivedTaskPolicy.equals(ModelTaskPolicy.ALL.name())) {
			//ArrayList<Boolean> allDone =  new ArrayList<Boolean>();
			//ArrayList<String> workersid = modelStatusRepository.getModelWorkersIds(modelendpoint, modelversion);
			//done = modelTasksRepository.addUniqueModelTask(modelendpoint, modelversion, modeltask);
			log.info("Added unique task (" + done + "): " + modeltask.toString() + " in " + modelendpoint + "_" + modelversion + " - " + modeltask.toString());
			for (String workerid: modelStatusRepository.getModelWorkersIds(modelendpoint, modelversion)) {
				modelTasksRepository.addWorkersModelTask(modelendpoint, modelversion, workerid, modeltask);
			}
			done = true;

		}
		else if (receivedTaskPolicy.equals(ModelTaskPolicy.ONLY_ONE.name())) {
			done = modelTasksRepository.addUniqueModelTask(modelendpoint, modelversion, modeltask);
			log.info("Added worker task (" + done + "): " + modeltask.toString() + " in " + modelendpoint + "_" + modelversion + " - " + modeltask.toString());
			
		}
		else {
			log.warn("Bad task request: " + modeltask.toString());
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		
		if (done) {
			return new ResponseEntity<String>(
				String.format(ADDED_TASK_MSG, modelendpoint + "_" + modelversion + " - " + modeltask.toString()), 
				HttpStatus.OK
				);
		}
		
		log.warn(" (" + modeltask.toString() + "): " + (String.format(BUSY_MSG, modelendpoint + "_" + modelversion)) );
		return new ResponseEntity<String>(HttpStatus.CONFLICT);
	}
	
	

}
