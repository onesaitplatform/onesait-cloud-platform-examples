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

package com.minsait.onesait.microservice.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;
import java.util.stream.Collectors;
import java.lang.String;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.minsait.onesait.microservice.enums.ModelTaskPolicy;
import com.minsait.onesait.microservice.enums.ModelTasks;
import com.minsait.onesait.microservice.error.ModelErrorHandler;
import com.minsait.onesait.microservice.model.ModelTask;
import com.minsait.onesait.microservice.repository.ModelStatusRepositoryImpl;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModelServiceImpl implements ModelService {
	
	@Value("${onesaitplatform.remoteModelService.uri}")
	private String remoteModelServiceUri;

	@Value("${onesaitplatform.supervisor.useSupervisor}")
	private boolean useSupervisor;
	
	@Autowired 
	private ModelStatusRepositoryImpl modelStatusRepository;
	
	@Autowired 
	private SupervisorService supervisorService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private final String ENDPOINT_HEALTH = "%s/%s/api/%s/health";
	private final String ENDPOINT_INFORMATION = "%s/%s/api/%s/information";
	private final String ENDPOINT_STATUS = "%s/%s/api/%s/status";
	private final String ENDPOINT_SAMPLE = "%s/%s/api/%s/sample";
	private final String ENDPOINT_STATS = "%s/%s/api/%s/stats";
	private final String ENDPOINT_PREDICT = "%s/%s/api/%s/predict";
	private final String ENDPOINT_TRAIN = "%s/%s/api/%s/train";
	
	private static final String HTTP_GET = "GET";
	private static final String HTTP_POST = "POST";
	private static final String HTTP_PUT = "PUT";
	private static final String HTTP_DELETE = "DELETE";
	private static final String EMPTY_STR = "";
	
	private final String BUSY_MSG = "{\"msg\":\"The resource %s is training\"}";
	private final String ERROR_MSG = "{\"msg\":\"Not possible to make prox request to endpoint %s\", \"error\":\"%s\"}";
	
	private String stringVersion(String modelversion) {
		String modelversionStr = modelversion;
		if (!modelversion.startsWith("v")) {
			modelversionStr = "v" + modelversion;
		}
		return modelversionStr;
	}
	
	@Override
	public ResponseEntity<String> health(String modelendpoint, String modelversion) {
		modelversion = stringVersion(modelversion);
		String endpoint = String.format(ENDPOINT_HEALTH, remoteModelServiceUri, modelendpoint, modelversion);
		log.info("Request on " + endpoint);
		ResponseEntity<String> result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		try {
			result = sendHttp(endpoint, HttpMethod.GET, EMPTY_STR);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			result = new ResponseEntity<>(String.format(ERROR_MSG, endpoint, e.getMessage()), HttpStatus.BAD_GATEWAY);
		}
		
		return result;
		
		
	}

	@Override
	public ResponseEntity<String> information(String modelendpoint, String modelversion) {
		modelversion = stringVersion(modelversion);
		String endpoint = String.format(ENDPOINT_INFORMATION, remoteModelServiceUri, modelendpoint , modelversion);
		log.info("Request on " + endpoint);
		ResponseEntity<String> result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		try {
			result = sendHttp(endpoint, HttpMethod.GET, EMPTY_STR);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			result = new ResponseEntity<>(String.format(ERROR_MSG, endpoint, e.getMessage()), HttpStatus.BAD_GATEWAY);
		}
		return result;
	}

	@Override
	public ResponseEntity<String> status(String modelendpoint, String modelversion) {
		modelversion = stringVersion(modelversion);
		String endpoint = String.format(ENDPOINT_STATUS, remoteModelServiceUri, modelendpoint , modelversion);
		log.info("Request on " + endpoint);
		ResponseEntity<String> result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		try {
			result = sendHttp(endpoint, HttpMethod.GET, EMPTY_STR);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			result = new ResponseEntity<>(String.format(ERROR_MSG, endpoint, e.getMessage()), HttpStatus.BAD_GATEWAY);
		}
		return result;
	}

	@Override
	public ResponseEntity<String> sample(String modelendpoint, String modelversion) {
		modelversion = stringVersion(modelversion);
		String endpoint = String.format(ENDPOINT_SAMPLE, remoteModelServiceUri, modelendpoint , modelversion);
		log.info("Request on " + endpoint);
		ResponseEntity<String> result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		try {
			result = sendHttp(endpoint, HttpMethod.GET, EMPTY_STR);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			result = new ResponseEntity<>(String.format(ERROR_MSG, endpoint, e.getMessage()), HttpStatus.BAD_GATEWAY);
		}
		return result;
	}

	@Override
	public ResponseEntity<String> stats(String modelendpoint, String modelversion) {
		modelversion = stringVersion(modelversion);
		String endpoint = String.format(ENDPOINT_STATS, remoteModelServiceUri, modelendpoint , modelversion);
		log.info("Request on " + endpoint);
		ResponseEntity<String> result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		try {
			result = sendHttp(endpoint, HttpMethod.GET, EMPTY_STR);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			result = new ResponseEntity<>(String.format(ERROR_MSG, endpoint, e.getMessage()), HttpStatus.BAD_GATEWAY);
		}
		return result;
	}

	@Override
	public ResponseEntity<String> predict(String modelendpoint, String modelversion, String body) {
		modelversion = stringVersion(modelversion);
		String endpoint = String.format(ENDPOINT_PREDICT, remoteModelServiceUri, modelendpoint , modelversion);
		log.info("Request on " + endpoint);
		ResponseEntity<String> result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		try {
			result = sendHttp(endpoint, HttpMethod.POST, body);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			result = new ResponseEntity<>(String.format(ERROR_MSG, endpoint, e.getMessage()), HttpStatus.BAD_GATEWAY);
		}
		return result;
	}

	@Override
	public ResponseEntity<String> train(String modelendpoint, String modelversion, String body) {
		modelversion = stringVersion(modelversion);
		String endpoint = String.format(ENDPOINT_TRAIN, remoteModelServiceUri, modelendpoint , modelversion);
		log.info("Request on " + endpoint);
		ResponseEntity<String> result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		try {
			if (useSupervisor) {
				boolean isTraining = modelStatusRepository.isModelTraining(modelendpoint, modelversion);
				log.info("Getting model is training: " + isTraining);
				if (isTraining) {
					result = new ResponseEntity<String>(String.format(BUSY_MSG, modelendpoint + "_" + modelversion), HttpStatus.CONFLICT); 
				}
				else { // add as unique task
					ModelTask trainTask = new ModelTask();
					trainTask.set_id(UUID.randomUUID().toString());
					trainTask.setExecution_policy(ModelTaskPolicy.ONLY_ONE.name());
					trainTask.setExecution_task(ModelTasks.TRAIN.name());
					trainTask.setTimestamp(new Date(System.currentTimeMillis()));
					result = supervisorService.receiveTask(modelendpoint, modelversion, trainTask);
				}
			}
			else { // proxy train
				try {
					result = sendHttp(endpoint, HttpMethod.POST, body);
				} catch (Exception e) {
					result =  new ResponseEntity<>(String.format(ERROR_MSG, endpoint), HttpStatus.BAD_GATEWAY);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return result;
	}
	
	public ResponseEntity<String> proxy(HttpServletRequest request) {
		
		// process method
		String method = request.getMethod().toUpperCase();
		String uri = request.getRequestURI();
		log.info("Received proxy request: method - " + method + " - uri - " + uri);
		HttpMethod httpMethod = null;
		switch (method) {
			case HTTP_GET:
				httpMethod = HttpMethod.GET;
				break;
			case HTTP_POST:
				httpMethod = HttpMethod.POST;
				break;
			case HTTP_PUT:
				httpMethod = HttpMethod.PUT;
				break;
			case HTTP_DELETE:
				httpMethod = HttpMethod.DELETE;
				break;
		}
		// process uri path
		String[] pathParts = uri.split("/");
		String modelendpoint = pathParts[3];
		String str_api = pathParts[4];
		String modelversion = pathParts[5];
		String modelPath = "/" + modelendpoint + "/" + str_api + "/" + modelversion;
		for (int i=6; i<pathParts.length; i++) {
			modelPath = modelPath + "/" + pathParts[i];
		}
		String url = remoteModelServiceUri + modelPath;
		// process headers
		HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            headers.add(key, value);
        }
		// process body
		String body = EMPTY_STR;
		if (method.equals(HTTP_POST) || method.equals(HTTP_PUT)) {
		   try {
			body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		   } catch (IOException e) {
			   return new ResponseEntity<>(String.format(ERROR_MSG, url, e), HttpStatus.BAD_REQUEST);
		   }
		}
		// send request
		try {
			return sendHttp(url, httpMethod, body, headers);
		} catch(Exception e) {
			return new ResponseEntity<>(String.format(ERROR_MSG, url, e), HttpStatus.BAD_REQUEST);
		}
		//return new ResponseEntity<>("proxy", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<String> sendHttp(HttpServletRequest requestServlet, HttpMethod httpMethod, String body)
			throws URISyntaxException, IOException {
		return sendHttp(requestServlet.getServletPath(), httpMethod, body); 
	}

	@Override
	public ResponseEntity<String> sendHttp(String url, HttpMethod httpMethod, String body)
			throws URISyntaxException, IOException {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return sendHttp(url, httpMethod, body, headers);
	}

	@Override
	public ResponseEntity<String> sendHttp(String url, HttpMethod httpMethod, String body, HttpHeaders headers)
			throws URISyntaxException, IOException {
		
		restTemplate.setErrorHandler(new ModelErrorHandler());

		//headers.add("Authorization", encryptRestUserpass());
		final HttpEntity<String> request = new HttpEntity<String>(body, headers);
		log.debug("Sending method " + httpMethod.toString() + " ModelService");
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.ACCEPTED);
		
		try {
			response = restTemplate.exchange(url, httpMethod, request, String.class);
		} catch (final Exception e) {
			log.error(e.getMessage());
		}
		log.debug("Execute method " + httpMethod.toString() + " '" + url + "' Model Service");
		final HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", response.getHeaders().getContentType().toString());
		return new ResponseEntity<String>(response.getBody(), responseHeaders,
				HttpStatus.valueOf(response.getStatusCode().value()));
	}
}