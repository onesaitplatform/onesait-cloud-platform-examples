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

package com.minsait.onesait.microservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minsait.onesait.microservice.model.ModelStatus;
import com.minsait.onesait.microservice.model.ModelTask;
import com.minsait.onesait.microservice.rest.SupervisorService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RequestMapping("supervisor")
@RestController
@Api(value = "Models supervisor REST service", tags = { "Supervisor" })
@ApiResponses({ @ApiResponse(code = 429, message = "Too Many Requests"),
		@ApiResponse(code = 500, message = "Error processing request"),
		@ApiResponse(code = 403, message = "Forbidden") })
public class SupervisorControllerImpl implements SupervisorController {
	
	private static final String VAR_MODEL_ENDPOINT = "modelendpoint";
	private static final String VAR_MODEL_VERSION = "modelversion";
	private static final String VAR_WORKER_ID = "workerid";
	
	private static final String PATH_STATUS = "{"+VAR_MODEL_ENDPOINT+"}/{"+VAR_MODEL_VERSION+"}/status";
	private static final String PATH_TASK_GET = "{"+VAR_MODEL_ENDPOINT+"}/{"+VAR_MODEL_VERSION+"}/task/{"+VAR_WORKER_ID+"}";
	private static final String PATH_TASK_POST = "{"+VAR_MODEL_ENDPOINT+"}/{"+VAR_MODEL_VERSION+"}/task";
	
	@Autowired
	private SupervisorService supervisorService;
	
	@Override
	@GetMapping(PATH_STATUS)
	@ApiOperation(response = String.class, httpMethod = "GET", value = "Last model status")
	@ApiResponse(code = 429, message = "Too Many Requests")
	public ResponseEntity<?> status(@PathVariable(VAR_MODEL_ENDPOINT) String modelendpoint, @PathVariable(VAR_MODEL_VERSION) String modelversion) {
		return supervisorService.status(modelendpoint, modelversion);
	}
	
	@Override
	@PostMapping(PATH_STATUS)
	@ApiOperation(response = String.class, httpMethod = "POST", value = "Receive new model status")
	@ApiResponse(code = 429, message = "Too Many Requests")
	public ResponseEntity<String> receiveStatus(@PathVariable(VAR_MODEL_ENDPOINT) String modelendpoint, @PathVariable(VAR_MODEL_VERSION) String modelversion,
			@RequestBody ModelStatus modelStatus) {		
		return supervisorService.receiveStatus(modelendpoint, modelversion, modelStatus);
	}
	
	@Override
	@GetMapping(PATH_TASK_GET)
	@ApiOperation(response = String.class, httpMethod = "GET", value = "Task for model worker")
	@ApiResponse(code = 429, message = "Too Many Requests")
	public ResponseEntity<?> task(@PathVariable(VAR_MODEL_ENDPOINT) String modelendpoint, @PathVariable(VAR_MODEL_VERSION) String modelversion, 
			@PathVariable(VAR_WORKER_ID) String workerid) {
		return supervisorService.task(modelendpoint, modelversion, workerid);
	}

	@Override
	@GetMapping(PATH_TASK_POST)
	@ApiOperation(response = String.class, httpMethod = "POST", value = "Receive new model task")
	@ApiResponse(code = 429, message = "Too Many Requests")
	public ResponseEntity<String> receiveTask(@PathVariable(VAR_MODEL_ENDPOINT) String modelendpoint, @PathVariable(VAR_MODEL_VERSION) String modelversion,
			@RequestBody ModelTask modeltask) {
		return supervisorService.receiveTask(modelendpoint, modelversion, modeltask);
	}


}
	