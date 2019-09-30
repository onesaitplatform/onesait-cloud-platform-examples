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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minsait.onesait.microservice.rest.ModelService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RequestMapping("models")
@RestController
@Api(value = "Model REST service", tags = { "Model" })
@ApiResponses({ @ApiResponse(code = 429, message = "Too Many Requests"),
		@ApiResponse(code = 500, message = "Error processing request"),
		@ApiResponse(code = 403, message = "Forbidden") })
public class ModelControllerImpl implements ModelController {
	
	private static final String VAR_MODEL_ENDPOINT = "modelendpoint";
	private static final String VAR_MODEL_VERSION = "modelversion";
	
	private static final String PATH_HEALTH = "{"+VAR_MODEL_ENDPOINT+"}/api/{"+VAR_MODEL_VERSION+"}/health";
	private static final String PATH_INFORMATION = "{"+VAR_MODEL_ENDPOINT+"}/api/{"+VAR_MODEL_VERSION+"}/information";
	private static final String PATH_STATUS = "{"+VAR_MODEL_ENDPOINT+"}/api/{"+VAR_MODEL_VERSION+"}/status";
	private static final String PATH_SAMPLE = "{"+VAR_MODEL_ENDPOINT+"}/api/{"+VAR_MODEL_VERSION+"}/sample";
	private static final String PATH_STATS = "{"+VAR_MODEL_ENDPOINT+"}/api/{"+VAR_MODEL_VERSION+"}/stats";
	private static final String PATH_PREDICT = "{"+VAR_MODEL_ENDPOINT+"}/api/{"+VAR_MODEL_VERSION+"}/predict";
	private static final String PATH_TRAIN = "{"+VAR_MODEL_ENDPOINT+"}/api/{"+VAR_MODEL_VERSION+"}/train";
	private static final String PATH_OTHER = "{"+VAR_MODEL_ENDPOINT+"}/api/{"+VAR_MODEL_VERSION+"}/**";
	
	private static final String HTTP_GET = "GET";
	private static final String HTTP_POST = "POST";

	
	@Autowired
	private ModelService modelService;
	
	@GetMapping(PATH_HEALTH)
	@ApiOperation(response = String.class, httpMethod = HTTP_GET, value = "Return model health")
	@ApiResponse(code = 429, message = "Too Many Requests")
	public ResponseEntity<String> health(@PathVariable(VAR_MODEL_ENDPOINT) String modelendpoint, @PathVariable(VAR_MODEL_VERSION) String modelversion) {
		ResponseEntity<String> response =  modelService.health(modelendpoint, modelversion);
		return response;
	}
	
	@GetMapping(PATH_INFORMATION)
	@ApiOperation(response = String.class, httpMethod = HTTP_GET, value = "Return model information")
	@ApiResponse(code = 429, message = "Too Many Requests")
	public ResponseEntity<String> information(@PathVariable(VAR_MODEL_ENDPOINT) String modelendpoint, @PathVariable(VAR_MODEL_VERSION) String modelversion) {
		return modelService.information(modelendpoint, modelversion);
	}
	
	@GetMapping(PATH_STATUS)
	@ApiOperation(response = String.class, httpMethod = HTTP_GET, value = "Return model status")
	@ApiResponse(code = 429, message = "Too Many Requests")
	public ResponseEntity<String> status(@PathVariable(VAR_MODEL_ENDPOINT) String modelendpoint, @PathVariable(VAR_MODEL_VERSION) String modelversion) {
		return modelService.status(modelendpoint, modelversion);
	}
	
	@GetMapping(PATH_SAMPLE)
	@ApiOperation(response = String.class, httpMethod = HTTP_GET, value = "Return model input body sample")
	@ApiResponse(code = 429, message = "Too Many Requests")
	public ResponseEntity<String> sample(@PathVariable(VAR_MODEL_ENDPOINT) String modelendpoint, @PathVariable(VAR_MODEL_VERSION) String modelversion) {
		return modelService.sample(modelendpoint, modelversion);
	}
	
	@GetMapping(PATH_STATS)
	@ApiOperation(response = String.class, httpMethod = HTTP_GET, value = "Return model stats")
	@ApiResponse(code = 429, message = "Too Many Requests")
	public ResponseEntity<String> stats(@PathVariable(VAR_MODEL_ENDPOINT) String modelendpoint, @PathVariable(VAR_MODEL_VERSION) String modelversion) {
		return modelService.stats(modelendpoint, modelversion);
	}
	
	@PostMapping(path = PATH_PREDICT) //, consumes = "application/json", produces = "*/*")
	@ApiOperation(response = String.class, httpMethod = HTTP_POST, value = "Return model prediction")
	@ApiResponse(code = 429, message = "Too Many Requests")
	public ResponseEntity<String> predict(@PathVariable(VAR_MODEL_ENDPOINT) String modelendpoint, @PathVariable(VAR_MODEL_VERSION) String modelversion, 
			@RequestBody String body) {
		return modelService.predict(modelendpoint, modelversion, body);
	}
	
	@PostMapping(path = PATH_TRAIN) //, consumes = "application/json", produces = "application/json")
	@ApiOperation(response = String.class, httpMethod = HTTP_POST, value = "Return model prediction")
	@ApiResponse(code = 429, message = "Too Many Requests")
	public ResponseEntity<String> train(@PathVariable(VAR_MODEL_ENDPOINT) String modelendpoint, @PathVariable(VAR_MODEL_VERSION) String modelversion, 
			@RequestBody String body) {
		return modelService.train(modelendpoint, modelversion, body);
	}
	
	@RequestMapping(PATH_OTHER)
	public ResponseEntity<String> proxy(HttpServletRequest request) {
		
		return modelService.proxy(request);
	}


}
